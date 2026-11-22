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
                        String username, String memberStatus) {}

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
                    e.getProfile() == null ? null : e.getProfile().getUsername(), status);
        }).toList();
    }

    @Transactional
    public void delete(Long teamId, Long entryId, String username) {
        ownedTeam(teamId, username);
        entries.delete(entries.findByIdAndTeamId(entryId, teamId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Jogador não encontrado no plantel")));
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
