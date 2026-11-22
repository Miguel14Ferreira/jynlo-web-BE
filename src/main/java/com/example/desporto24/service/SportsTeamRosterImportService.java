package com.example.desporto24.service;

import com.example.desporto24.model.Profile;
import com.example.desporto24.model.team.SportsTeam;
import com.example.desporto24.model.team.SportsTeamMember;
import com.example.desporto24.model.team.SportsTeamRosterEntry;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.team.SportsTeamMemberRepository;
import com.example.desporto24.repository.team.SportsTeamRepository;
import com.example.desporto24.repository.team.SportsTeamRosterEntryRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.text.Normalizer;
import java.time.Instant;
import java.util.*;

/**
 * Imports a team squad from Excel/CSV: name, shirt number, position and,
 * optionally, a Jynlo username. Only those columns are read; e-mails and
 * phone numbers in the file are never read or stored. Rows with a Jynlo
 * username get a normal team invitation; the others stay as squad entries
 * that only the team owner sees.
 */
@Service
@RequiredArgsConstructor
public class SportsTeamRosterImportService {
    static final int MAX_PLAYERS = 200;

    private static final Set<String> NAME = Set.of("nome", "name", "jogador", "jogadora", "player", "atleta",
            "nome completo", "full name");
    private static final Set<String> NUMBER = Set.of("numero", "n", "no", "nº", "n.º", "#", "camisola", "dorsal",
            "number", "shirt", "shirt number", "numero da camisola");
    private static final Set<String> POSITION = Set.of("posicao", "position", "pos", "funcao", "lugar");
    private static final Set<String> USERNAME = Set.of("utilizador", "utilizador jynlo", "username", "user",
            "conta jynlo", "jynlo", "conta");

    private final SportsTeamRepository teams;
    private final SportsTeamMemberRepository members;
    private final SportsTeamRosterEntryRepository entries;
    private final ProfileRepository profiles;
    private final SportsTeamMembershipService membership;

    public enum RowStatus { INVITE, ALREADY_IN_TEAM, NEW, DUPLICATE, UNKNOWN_USERNAME }
    public record PreviewRow(int line, String name, Integer shirtNumber, String position,
                             String username, RowStatus status) {}
    public record Preview(List<PreviewRow> rows, int invites, int newPlayers, int skipped,
                          List<String> warnings) {}
    public record Result(int invited, int added, int skipped) {}
    public record Entry(Long id, String name, Integer shirtNumber, String position,
                        String username, String memberStatus, boolean hasClaimLink) {}
    public record ClaimLink(String token, String path) {}
    public record ClaimPreview(Long teamId, String teamName, String teamSlug, String name,
                               Integer shirtNumber, String position, boolean alreadyLinked) {}
    public record Suggestion(Long entryId, String entryName, Integer shirtNumber,
                             Long profileId, String username, String fullName) {}
    public record LineUpdate(String name, Integer shirtNumber, String position) {}

    @Transactional(readOnly = true)
    public Preview preview(Long teamId, String username, MultipartFile file) {
        SportsTeam team = ownedTeam(teamId, username);
        return analyse(team, read(file));
    }

    @Transactional
    public Result confirm(Long teamId, String username, MultipartFile file) {
        SportsTeam team = ownedTeam(teamId, username);
        Preview preview = analyse(team, read(file));
        if (entries.countByTeamId(teamId) + preview.newPlayers() + preview.invites() > MAX_PLAYERS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O plantel pode ter até " + MAX_PLAYERS + " jogadores");
        }
        int invited = 0, added = 0;
        for (PreviewRow row : preview.rows()) {
            if (row.status() != RowStatus.INVITE && row.status() != RowStatus.NEW
                    && row.status() != RowStatus.UNKNOWN_USERNAME) continue;
            Profile profile = null;
            if (row.status() == RowStatus.INVITE) {
                profile = profiles.findUserByUsername(row.username()).orElse(null);
                if (profile == null) continue;
                membership.invite(teamId, profile.getId(), username);
                invited++;
            } else {
                added++;
            }
            entries.save(SportsTeamRosterEntry.builder()
                    .team(team).name(row.name()).shirtNumber(row.shirtNumber())
                    .position(row.position()).profile(profile).createdAt(Instant.now()).build());
        }
        return new Result(invited, added, preview.rows().size() - invited - added);
    }

    @Transactional(readOnly = true)
    public List<Entry> list(Long teamId, String username) {
        ownedTeam(teamId, username);
        return entries.findByTeamIdOrderByShirtNumberAscNameAsc(teamId).stream().map(e -> {
            String status = null;
            if (e.getProfile() != null) {
                status = members.findByTeamIdAndProfileId(teamId, e.getProfile().getId())
                        .map(m -> m.getStatus().name()).orElse(null);
            }
            return new Entry(e.getId(), e.getName(), e.getShirtNumber(), e.getPosition(),
                    e.getProfile() == null ? null : e.getProfile().getUsername(), status,
                    e.getClaimTokenHash() != null);
        }).toList();
    }

    @Transactional
    public void delete(Long teamId, Long entryId, String username) {
        ownedTeam(teamId, username);
        entries.delete(entries.findByIdAndTeamId(entryId, teamId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogador não encontrado no plantel")));
    }

    // --- Linking squad lines to Jynlo accounts ---------------------------

    /** A personal link for one squad line. Creating a new one invalidates the previous link. */
    @Transactional
    public ClaimLink createClaimLink(Long teamId, Long entryId, String username) {
        ownedTeam(teamId, username);
        SportsTeamRosterEntry entry = entry(teamId, entryId);
        if (entry.getProfile() != null) throw conflict("Este jogador já está associado a uma conta");
        String token = UUID.randomUUID().toString();
        entry.setClaimTokenHash(sha256(token));
        entry.setClaimTokenCreatedAt(Instant.now());
        entries.save(entry);
        return new ClaimLink(token, "/menu/teams/claim/" + token);
    }

    @Transactional(readOnly = true)
    public ClaimPreview claimPreview(String token) {
        SportsTeamRosterEntry entry = claimable(token);
        SportsTeam team = entry.getTeam();
        return new ClaimPreview(team.getId(), team.getName(), team.getSlug(), entry.getName(),
                entry.getShirtNumber(), entry.getPosition(), entry.getProfile() != null);
    }

    /** The person opening the link agrees to join: becomes this squad line and an active member. */
    @Transactional
    public ClaimPreview acceptClaim(String token, String username) {
        SportsTeamRosterEntry entry = claimable(token);
        if (entry.getProfile() != null) throw conflict("Este lugar no plantel já foi associado a uma conta");
        Profile profile = profiles.findUserByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        if (Boolean.FALSE.equals(profile.getEnabled()) || profile.getModerationSuspendedAt() != null)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Conta indisponível");
        Long teamId = entry.getTeam().getId();
        if (entries.findFirstByTeamIdAndProfileId(teamId, profile.getId()).isPresent())
            throw conflict("Já estás associado a outro jogador deste plantel");
        boolean owner = entry.getTeam().getOwner().getId().equals(profile.getId());
        if (!owner) {
            var member = members.findByTeamIdAndProfileId(teamId, profile.getId()).orElse(null);
            if (member == null || member.getStatus() != SportsTeamMember.Status.ACTIVE) {
                if (member == null) member = SportsTeamMember.builder()
                        .team(entry.getTeam()).profile(profile).build();
                member.setStatus(SportsTeamMember.Status.INVITED);
                member.setInvitedAt(Instant.now());
                member.setRespondedAt(null);
                members.saveAndFlush(member);
                // Same acceptance path as a normal invitation (realtime refresh, checks).
                membership.respond(teamId, profile.getUsername(), true);
            }
        }
        entry.setProfile(profile);
        entry.setClaimTokenHash(null);
        entry.setClaimTokenCreatedAt(null);
        entries.save(entry);
        return new ClaimPreview(teamId, entry.getTeam().getName(), entry.getTeam().getSlug(),
                entry.getName(), entry.getShirtNumber(), entry.getPosition(), true);
    }

    /**
     * Owner links a squad line to an account. An active member is linked at
     * once (they already joined); anyone else gets the normal invitation and
     * only becomes a member if they accept.
     */
    @Transactional
    public Entry link(Long teamId, Long entryId, Long profileId, String username) {
        SportsTeam team = ownedTeam(teamId, username);
        SportsTeamRosterEntry entry = entry(teamId, entryId);
        if (entry.getProfile() != null) throw conflict("Este jogador já está associado a uma conta");
        Profile target = profiles.findById(profileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogador não encontrado"));
        if (entries.findFirstByTeamIdAndProfileId(teamId, profileId).isPresent())
            throw conflict("Essa conta já está associada a outro jogador do plantel");
        boolean active = team.getOwner().getId().equals(profileId)
                || members.findByTeamIdAndProfileId(teamId, profileId)
                .map(m -> m.getStatus() == SportsTeamMember.Status.ACTIVE).orElse(false);
        boolean invited = members.findByTeamIdAndProfileId(teamId, profileId)
                .map(m -> m.getStatus() == SportsTeamMember.Status.INVITED).orElse(false);
        if (!active && !invited) membership.invite(teamId, profileId, username);
        entry.setProfile(target);
        entry.setClaimTokenHash(null);
        entry.setClaimTokenCreatedAt(null);
        entries.save(entry);
        return list(teamId, username).stream().filter(e -> e.id().equals(entryId)).findFirst().orElseThrow();
    }

    @Transactional
    public void unlink(Long teamId, Long entryId, String username) {
        ownedTeam(teamId, username);
        SportsTeamRosterEntry entry = entry(teamId, entryId);
        entry.setProfile(null);
        entries.save(entry);
    }

    @Transactional
    public Entry update(Long teamId, Long entryId, LineUpdate update, String username) {
        ownedTeam(teamId, username);
        SportsTeamRosterEntry entry = entry(teamId, entryId);
        applyLine(entry, update);
        entries.save(entry);
        return list(teamId, username).stream().filter(e -> e.id().equals(entryId)).findFirst().orElseThrow();
    }

    /** Number/position for a player who joined without a squad line. */
    @Transactional
    public Entry setMemberLine(Long teamId, Long profileId, LineUpdate update, String username) {
        SportsTeam team = ownedTeam(teamId, username);
        Profile target = profiles.findById(profileId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogador não encontrado"));
        boolean active = team.getOwner().getId().equals(profileId)
                || members.findByTeamIdAndProfileId(teamId, profileId)
                .map(m -> m.getStatus() == SportsTeamMember.Status.ACTIVE).orElse(false);
        if (!active) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O jogador não está na equipa");
        SportsTeamRosterEntry entry = entries.findFirstByTeamIdAndProfileId(teamId, profileId)
                .orElseGet(() -> SportsTeamRosterEntry.builder().team(team).profile(target)
                        .name(displayName(target)).createdAt(Instant.now()).build());
        applyLine(entry, new LineUpdate(entry.getName(), update.shirtNumber(), update.position()));
        SportsTeamRosterEntry saved = entries.save(entry);
        return list(teamId, username).stream().filter(e -> e.id().equals(saved.getId())).findFirst().orElseThrow();
    }

    /**
     * Suggests, never applies: active members whose name matches an unlinked
     * squad line. The owner confirms each one.
     */
    @Transactional(readOnly = true)
    public List<Suggestion> suggestions(Long teamId, String username) {
        SportsTeam team = ownedTeam(teamId, username);
        List<SportsTeamRosterEntry> all = entries.findByTeamIdOrderByShirtNumberAscNameAsc(teamId);
        Set<Long> linked = new HashSet<>();
        for (SportsTeamRosterEntry e : all) if (e.getProfile() != null) linked.add(e.getProfile().getId());
        List<Profile> candidates = new ArrayList<>();
        candidates.add(team.getOwner());
        members.findByTeamIdAndStatusOrderByIdAsc(teamId, SportsTeamMember.Status.ACTIVE)
                .forEach(m -> candidates.add(m.getProfile()));
        List<Suggestion> out = new ArrayList<>();
        Set<Long> used = new HashSet<>();
        for (SportsTeamRosterEntry e : all) {
            if (e.getProfile() != null) continue;
            String line = key(e.getName());
            List<Profile> matches = candidates.stream()
                    .filter(p -> p != null && !linked.contains(p.getId()) && !used.contains(p.getId()))
                    .filter(p -> sameName(line, p))
                    .toList();
            if (matches.size() != 1) continue; // two possible "João": ask nothing
            Profile p = matches.get(0);
            used.add(p.getId());
            out.add(new Suggestion(e.getId(), e.getName(), e.getShirtNumber(), p.getId(), p.getUsername(),
                    p.isPrivateProfile() ? null : p.getFullName()));
        }
        return out;
    }

    static boolean sameName(String lineKey, Profile profile) {
        if (lineKey.isBlank()) return false;
        // A private profile's real name is not used, so a suggestion never reveals it.
        String fullName = profile.isPrivateProfile() ? null : profile.getFullName();
        for (String candidate : new String[]{fullName, profile.getNickname(), profile.getUsername()}) {
            if (candidate == null || candidate.isBlank()) continue;
            String k = key(candidate);
            if (k.equals(lineKey)) return true;
            String[] a = lineKey.split(" "), b = k.split(" ");
            // "Rui Costa" vs "Rui Miguel Costa": same first and last name.
            if (a.length >= 2 && b.length >= 2 && a[0].equals(b[0]) && a[a.length - 1].equals(b[b.length - 1]))
                return true;
        }
        return false;
    }

    private static String displayName(Profile p) {
        if (!p.isPrivateProfile() && p.getFullName() != null && !p.getFullName().isBlank())
            return p.getFullName().trim().length() > 100 ? p.getFullName().trim().substring(0, 100) : p.getFullName().trim();
        return p.getUsername();
    }

    private static void applyLine(SportsTeamRosterEntry entry, LineUpdate update) {
        String name = update.name() == null ? entry.getName() : clean(update.name());
        if (name == null || name.isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O nome é obrigatório");
        if (name.length() > 100) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O nome pode ter até 100 caracteres");
        Integer number = update.shirtNumber();
        if (number != null && (number < 0 || number > 999))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O número tem de estar entre 0 e 999");
        String position = update.position() == null ? null : clean(update.position());
        if (position != null && position.length() > 40)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A posição pode ter até 40 caracteres");
        entry.setName(name);
        entry.setShirtNumber(number);
        entry.setPosition(position == null || position.isBlank() ? null : position);
    }

    private SportsTeamRosterEntry entry(Long teamId, Long entryId) {
        return entries.findByIdAndTeamId(entryId, teamId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogador não encontrado no plantel"));
    }

    private SportsTeamRosterEntry claimable(String token) {
        if (token == null || !token.matches("[0-9a-fA-F-]{36}"))
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Link inválido ou já usado");
        return entries.findByClaimTokenHash(sha256(token.toLowerCase(Locale.ROOT)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Link inválido ou já usado"));
    }

    static String sha256(String value) {
        try {
            byte[] digest = java.security.MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    private static ResponseStatusException conflict(String message) {
        return new ResponseStatusException(HttpStatus.CONFLICT, message);
    }

    // ---------------------------------------------------------------------

    record ParsedRow(int line, String name, Integer shirtNumber, String position, String username) {}
    record Parsed(List<ParsedRow> rows, List<String> warnings) {}

    private Preview analyse(SportsTeam team, Parsed parsed) {
        Set<String> seenNames = new HashSet<>();
        Set<String> existingNames = new HashSet<>();
        for (SportsTeamRosterEntry e : entries.findByTeamIdOrderByShirtNumberAscNameAsc(team.getId())) {
            existingNames.add(key(e.getName()));
        }
        List<PreviewRow> rows = new ArrayList<>();
        int invites = 0, fresh = 0;
        for (ParsedRow r : parsed.rows()) {
            String k = key(r.name());
            RowStatus status;
            String matchedUsername = null;
            if (!seenNames.add(k) || existingNames.contains(k)) {
                status = RowStatus.DUPLICATE;
            } else if (r.username() != null) {
                Optional<Profile> profile = profiles.findUserByUsername(r.username())
                        .filter(p -> !Boolean.FALSE.equals(p.getEnabled()) && p.getModerationSuspendedAt() == null);
                if (profile.isEmpty()) {
                    status = RowStatus.UNKNOWN_USERNAME;
                } else {
                    matchedUsername = profile.get().getUsername();
                    boolean inTeam = team.getOwner().getId().equals(profile.get().getId())
                            || members.findByTeamIdAndProfileId(team.getId(), profile.get().getId())
                            .map(m -> m.getStatus() != SportsTeamMember.Status.DECLINED).orElse(false);
                    status = inTeam ? RowStatus.ALREADY_IN_TEAM : RowStatus.INVITE;
                }
            } else {
                status = RowStatus.NEW;
            }
            if (status == RowStatus.INVITE) invites++;
            // A username that does not match still adds the player, without an account.
            if (status == RowStatus.NEW || status == RowStatus.UNKNOWN_USERNAME) fresh++;
            rows.add(new PreviewRow(r.line(), r.name(), r.shirtNumber(), r.position(),
                    matchedUsername != null ? matchedUsername : r.username(), status));
        }
        int skipped = (int) rows.stream().filter(row -> row.status() == RowStatus.DUPLICATE
                || row.status() == RowStatus.ALREADY_IN_TEAM).count();
        return new Preview(rows, invites, fresh, skipped, parsed.warnings());
    }

    Parsed read(MultipartFile file) {
        if (file == null || file.isEmpty()) throw bad("Escolhe um ficheiro Excel ou CSV");
        String lower = Optional.ofNullable(file.getOriginalFilename()).orElse("").toLowerCase(Locale.ROOT);
        if (!lower.endsWith(".xlsx") && !lower.endsWith(".xls") && !lower.endsWith(".csv") && !lower.endsWith(".tsv")) {
            throw bad("Formatos suportados: .xlsx, .xls, .csv e .tsv");
        }
        if (file.getSize() > 2L * 1024 * 1024) throw bad("O ficheiro não pode ultrapassar 2 MB");
        try (Workbook workbook = CommunityImportWorkbookReader.open(file)) {
            if (workbook.getNumberOfSheets() == 0) throw bad("O ficheiro está vazio");
            return parse(workbook.getSheetAt(0));
        } catch (IOException | RuntimeException e) {
            if (e instanceof ResponseStatusException rse) throw rse;
            throw bad(e instanceof IllegalArgumentException && e.getMessage() != null
                    ? e.getMessage() : "Não foi possível ler o ficheiro");
        }
    }

    static Parsed parse(Sheet sheet) {
        DataFormatter formatter = new DataFormatter();
        List<String> warnings = new ArrayList<>();
        Integer nameCol = null, numberCol = null, positionCol = null, usernameCol = null;
        int headerRow = -1;
        for (Row row : sheet) {
            List<String> cells = cells(row, formatter);
            if (cells.stream().allMatch(String::isBlank)) continue;
            for (int i = 0; i < cells.size(); i++) {
                String h = normalize(cells.get(i));
                if (nameCol == null && NAME.contains(h)) nameCol = i;
                else if (numberCol == null && NUMBER.contains(h)) numberCol = i;
                else if (positionCol == null && POSITION.contains(h)) positionCol = i;
                else if (usernameCol == null && USERNAME.contains(h)) usernameCol = i;
            }
            headerRow = row.getRowNum();
            break;
        }
        if (headerRow < 0) throw bad("O ficheiro está vazio");
        if (nameCol == null) {
            throw bad("Não encontrámos a coluna do nome. Usa um cabeçalho \"Nome\" na primeira linha.");
        }
        List<ParsedRow> rows = new ArrayList<>();
        for (Row row : sheet) {
            if (row.getRowNum() <= headerRow) continue;
            List<String> cells = cells(row, formatter);
            String name = clean(at(cells, nameCol));
            if (name.isBlank()) continue;
            int line = row.getRowNum() + 1;
            if (rows.size() >= MAX_PLAYERS) {
                warnings.add("Só lemos os primeiros " + MAX_PLAYERS + " jogadores");
                break;
            }
            if (name.length() > 100) {
                warnings.add("Linha " + line + ": nome com mais de 100 caracteres, foi cortado");
                name = name.substring(0, 100).trim();
            }
            Integer number = null;
            String rawNumber = clean(at(cells, numberCol));
            if (!rawNumber.isBlank()) {
                String digits = rawNumber.replaceAll("^#", "").replaceAll("[.,]0+$", "");
                if (digits.matches("\\d{1,3}")) number = Integer.parseInt(digits);
                else warnings.add("Linha " + line + ": número \"" + rawNumber + "\" ignorado");
            }
            String position = clean(at(cells, positionCol));
            if (position.length() > 40) position = position.substring(0, 40).trim();
            String username = clean(at(cells, usernameCol)).replaceFirst("^@", "");
            if (!username.isBlank() && !username.matches("[A-Za-z0-9._-]{2,50}")) {
                warnings.add("Linha " + line + ": utilizador \"" + username + "\" inválido, ignorado");
                username = "";
            }
            rows.add(new ParsedRow(line, name, number, position.isBlank() ? null : position,
                    username.isBlank() ? null : username));
        }
        if (rows.isEmpty()) throw bad("Não encontrámos jogadores no ficheiro");
        return new Parsed(rows, warnings);
    }

    private SportsTeam ownedTeam(Long teamId, String username) {
        SportsTeam team = teams.findById(teamId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipa não encontrada"));
        Profile actor = profiles.findUserByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        if (!team.getOwner().getId().equals(actor.getId())) {
            throw new AccessDeniedException("Apenas o responsável pode gerir o plantel");
        }
        return team;
    }

    private static List<String> cells(Row row, DataFormatter formatter) {
        List<String> out = new ArrayList<>();
        int last = Math.max(row.getLastCellNum(), 0);
        for (int i = 0; i < Math.min(last, 64); i++) {
            var cell = row.getCell(i);
            out.add(cell == null ? "" : formatter.formatCellValue(cell));
        }
        return out;
    }

    private static String at(List<String> cells, Integer index) {
        return index == null || index >= cells.size() ? "" : cells.get(index);
    }

    private static String clean(String value) {
        return value == null ? "" : value.replaceAll("[\\p{Cntrl}]", " ").replaceAll("\\s+", " ").trim();
    }

    private static String normalize(String value) {
        return Normalizer.normalize(clean(value), Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT).replace(':', ' ').trim();
    }

    private static String key(String name) {
        return normalize(name);
    }

    private static ResponseStatusException bad(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
