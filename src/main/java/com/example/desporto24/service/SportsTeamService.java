package com.example.desporto24.service;

import com.example.desporto24.model.team.SportsTeam;
import com.example.desporto24.repository.ProfileRepository;
import com.example.desporto24.repository.team.SportsTeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example.desporto24.storage.MediaStorage;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.text.Normalizer;
import java.time.Instant;
import java.util.List;
import java.util.Locale;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;

@Service
@Slf4j
@RequiredArgsConstructor
public class SportsTeamService {
    private static final ObjectMapper KIT_MAPPER = new ObjectMapper();
    private static final TypeReference<List<KitVariant>> KIT_LIST_TYPE = new TypeReference<>() {};
    private final SportsTeamRepository teams;
    private final com.example.desporto24.repository.community.CommunityTournamentTeamRepository tournamentTeams;
    private final ProfileRepository profiles;
    @Autowired(required = false)
    private MediaStorage mediaStorage;
    @Autowired(required = false)
    private org.springframework.context.ApplicationEventPublisher realtimeEvents;

    public record TeamView(Long id, String slug, String name, String sport, String location,
                           String description, Long ownerProfileId, boolean feedEnabled,
                           boolean chatEnabled, String logoUrl,
                           String kitPrimary, String kitSecondary, String kitPattern,
                           String kitGarment, String kitAccent, String kitPlayerName, String kitPlayerNumber,
                           List<KitVariant> kitVariants, String crestDesign) {
        public TeamView(Long id, String slug, String name, String sport, String location,
                        String description, Long ownerProfileId, boolean feedEnabled,
                        boolean chatEnabled, String logoUrl,
                        String kitPrimary, String kitSecondary, String kitPattern) {
            this(id, slug, name, sport, location, description, ownerProfileId, feedEnabled,
                    chatEnabled, logoUrl, kitPrimary, kitSecondary, kitPattern, null, null, null, null, List.of(), null);
        }
        public TeamView(Long id, String slug, String name, String sport, String location,
                        String description, Long ownerProfileId, boolean feedEnabled,
                        boolean chatEnabled, String logoUrl, String kitPrimary, String kitSecondary,
                        String kitPattern, String kitGarment, String kitAccent, String kitPlayerName,
                        String kitPlayerNumber, List<KitVariant> kitVariants) {
            this(id, slug, name, sport, location, description, ownerProfileId,
                    feedEnabled, chatEnabled, logoUrl, kitPrimary, kitSecondary, kitPattern,
                    kitGarment, kitAccent, kitPlayerName, kitPlayerNumber, kitVariants, null);
        }
        public TeamView(Long id, String slug, String name, String sport, String location,
                        String description, Long ownerProfileId, boolean feedEnabled,
                        boolean chatEnabled, String logoUrl, String kitPrimary, String kitSecondary,
                        String kitPattern, String kitGarment, String kitAccent, String kitPlayerName,
                        String kitPlayerNumber) {
            this(id, slug, name, sport, location, description, ownerProfileId,
                    feedEnabled, chatEnabled, logoUrl, kitPrimary, kitSecondary, kitPattern,
                    kitGarment, kitAccent, kitPlayerName, kitPlayerNumber, List.of(), null);
        }
    }
    public record CreateTeam(String name, String sport, String location, String description) {}
    public record TeamFeatures(Boolean feedEnabled, Boolean chatEnabled) {}
    public record KitVariant(String type, String primary, String secondary, String pattern,
                             String garment, String accent, String playerName, String playerNumber) {}
    public record KitSettings(String primary, String secondary, String pattern,
                              String garment, String accent, String playerName, String playerNumber,
                              List<KitVariant> variants) {
        public KitSettings(String primary, String secondary, String pattern) {
            this(primary, secondary, pattern, null, null, null, null, null);
        }
        public KitSettings(String primary, String secondary, String pattern,
                           String garment, String accent, String playerName, String playerNumber) {
            this(primary, secondary, pattern, garment, accent, playerName, playerNumber, null);
        }
    }

    private TeamView view(SportsTeam team) {
        return new TeamView(team.getId(), team.getSlug(), team.getName(), team.getSport(),
                team.getLocation(), team.getDescription(), team.getOwner().getId(),
                team.isFeedEnabled(), team.isChatEnabled(),team.getLogoUrl(),
                team.getKitPrimary(),team.getKitSecondary(),
                team.getKitPattern() == null ? "PLAIN" : team.getKitPattern(),
                team.getKitGarment(), team.getKitAccent(), team.getKitPlayerName(), team.getKitPlayerNumber(),
                readVariants(team),team.getCrestDesign());
    }

    @Transactional(readOnly = true)
    public List<TeamView> discover(String query) {
        return teams.findByNameContainingIgnoreCaseOrderByIdDesc(query == null ? "" : query.trim(),
                org.springframework.data.domain.PageRequest.of(0, 30)).stream().map(this::view).toList();
    }

    /** Results are derived from officially finalized tournaments; never user-editable. */
    public record TeamCompetitionHistory(Long tournamentId, String tournamentName,
            Long communityId, String communityName, String sport, String status,
            Instant completedAt, boolean champion, boolean archived) {}

    @Transactional(readOnly = true)
    public List<TeamCompetitionHistory> competitionHistory(Long id) {
        required(id);
        return tournamentTeams.historyForGlobalTeam(id).stream().map(entry -> {
            var tournament = entry.getTournament();
            boolean completed = tournament.getStatus() ==
                    com.example.desporto24.enumeration.TournamentStatus.COMPLETED;
            boolean winner = completed && !tournament.isChampionTied()
                    && java.util.Objects.equals(tournament.getChampionTeamId(), entry.getId());
            return new TeamCompetitionHistory(tournament.getId(), tournament.getName(),
                    tournament.getCommunity().getId(), tournament.getCommunity().getName(),
                    tournament.getSport(), tournament.getStatus().name(),
                    tournament.getCompletedAt(), winner, completed);
        }).toList();
    }

    @Transactional(readOnly = true)
    public TeamView get(Long id) {
        return view(required(id));
    }

    @Transactional(readOnly = true)
    public List<TeamView> mine(String username) {
        var owner = profiles.findUserByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        return teams.findByOwnerIdOrderByIdDesc(owner.getId()).stream().map(this::view).toList();
    }

    @Transactional
    public TeamView create(CreateTeam request, String username) {
        var owner = profiles.findUserByUsername(username)
                .orElseThrow(() -> new AccessDeniedException("Utilizador desconhecido"));
        requireEligibleOwner(owner);
        if (request == null || request.name() == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome da equipa obrigatório");
        String name = request.name().trim().replaceAll("\\s+", " ");
        if (name.isEmpty() || name.length() > 100)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome da equipa inválido");
        String base = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-").replaceAll("^-|-$", "");
        if (base.isBlank()) base = "equipa";
        base = base.substring(0, Math.min(base.length(), 75));
        // Unique slug is enforced by the database as well; never overwrite another team's identity.
        String slug = base + "-" + java.util.UUID.randomUUID().toString().substring(0, 8);
        SportsTeam team = SportsTeam.builder().owner(owner).name(name).slug(slug)
                .sport(clean(request.sport(), 100)).location(clean(request.location(), 100))
                .description(clean(request.description(), 500)).createdAt(Instant.now())
                .feedEnabled(true).chatEnabled(true).build();
        return view(teams.saveAndFlush(team));
    }

    @Transactional
    public TeamView features(Long id, TeamFeatures request, String username) {
        SportsTeam team = ownedTeam(id, username);
        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Configuração obrigatória");
        boolean feedChanged = request.feedEnabled() != null
                && team.isFeedEnabled() != request.feedEnabled();
        boolean chatChanged = request.chatEnabled() != null
                && team.isChatEnabled() != request.chatEnabled();
        if (request.feedEnabled() != null) team.setFeedEnabled(request.feedEnabled());
        if (request.chatEnabled() != null) team.setChatEnabled(request.chatEnabled());
        TeamView saved = view(teams.saveAndFlush(team));
        // Content-free invalidations are delivered after commit. In particular,
        // disabling a feature must invalidate already-open private conversations.
        if (realtimeEvents != null) {
            if (feedChanged) realtimeEvents.publishEvent(
                    new com.example.desporto24.websocket.TeamContentChangedEvent(id, "feed"));
            if (chatChanged) realtimeEvents.publishEvent(
                    new com.example.desporto24.websocket.TeamContentChangedEvent(id, "chat"));
        }
        return saved;
    }

    @Transactional
    public TeamView kit(Long id, KitSettings request, String username) {
        SportsTeam team = ownedTeam(id, username);
        if (request == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Configuração obrigatória");
        team.setKitPrimary(color(request.primary()));
        team.setKitSecondary(color(request.secondary()));
        if (request.pattern() != null) team.setKitPattern(pattern(request.pattern()));
        if (request.garment() != null) team.setKitGarment(garment(request.garment()));
        if (request.accent() != null) team.setKitAccent(color(request.accent()));
        if (request.playerName() != null) team.setKitPlayerName(playerName(request.playerName()));
        if (request.playerNumber() != null) team.setKitPlayerNumber(playerNumber(request.playerNumber()));
        if (request.variants() != null) team.setKitVariants(writeVariants(request.variants()));
        return view(teams.saveAndFlush(team));
    }

    @Transactional
    public TeamView uploadLogo(Long id, MultipartFile file, String username) {
        return uploadLogo(id,file,null,username);
    }

    @Transactional
    public TeamView uploadLogo(Long id, MultipartFile file, String crestDesign, String username) {
        SportsTeam team = ownedTeam(id, username);
        String validatedDesign = validateCrestDesign(crestDesign);
        if (mediaStorage == null)
            throw new IllegalStateException("Media storage is not configured");
        if (file == null || file.isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seleciona um logótipo");
        // The shared community-photo storage validates real image bytes and size,
        // not just the client-provided filename or Content-Type.
        var uri = mediaStorage.storeCommunityPhoto(
                "teams/" + id + "/" + UUID.randomUUID(), file);
        String old = team.getLogoUrl();
        team.setLogoUrl(uri.toString());
        team.setCrestDesign(validatedDesign);
        TeamView result = view(teams.saveAndFlush(team));
        deleteLogo(old, id);
        return result;
    }

    @Transactional
    public TeamView removeLogo(Long id, String username) {
        SportsTeam team = ownedTeam(id, username);
        String old = team.getLogoUrl();
        team.setLogoUrl(null);
        team.setCrestDesign(null);
        TeamView result = view(teams.saveAndFlush(team));
        deleteLogo(old, id);
        return result;
    }

    private void requireEligibleOwner(com.example.desporto24.model.Profile owner) {
        if (Boolean.FALSE.equals(owner.getEnabled()) || owner.getModerationSuspendedAt() != null)
            throw new AccessDeniedException("Conta indisponível para gerir equipas");
    }

    private SportsTeam ownedTeam(Long id, String username) {
        SportsTeam team = required(id);
        if (!team.getOwner().getUsername().equals(username))
            throw new AccessDeniedException("Apenas o responsável pode personalizar a equipa");
        requireEligibleOwner(team.getOwner());
        return team;
    }

    private String pattern(String value) {
        // Old clients omitted pattern. Preserve their current design; existing
        // teams without a saved design display the plain shirt by default.
        if (value == null) return null;
        if ("PLAIN".equals(value) || "STRIPES".equals(value) || "DIAGONAL".equals(value)
                || "HOOPS".equals(value) || "HALVES".equals(value) || "CHEVRON".equals(value))
            return value;
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Modelo de equipamento inválido");
    }

    private List<KitVariant> readVariants(SportsTeam team) {
        String serialized = team.getKitVariants();
        if (serialized == null || serialized.isBlank()) return List.of();
        try {
            return KIT_MAPPER.readValue(serialized, KIT_LIST_TYPE);
        } catch (JsonProcessingException ex) {
            log.error("Invalid saved kit variants for team {}", team.getId(), ex);
            return List.of();
        }
    }

    /** Server-owned validation: never trust the client's variant names or arbitrary JSON. */
    private String writeVariants(List<KitVariant> incoming) {
        if (incoming.size() > 2)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Máximo de dois equipamentos adicionais");
        Set<String> types = new HashSet<>();
        List<KitVariant> clean = new ArrayList<>();
        for (KitVariant value : incoming) {
            if (value == null || !Set.of("ALTERNATE", "TRAINING").contains(value.type())
                    || !types.add(value.type()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de equipamento duplicado ou inválido");
            clean.add(new KitVariant(value.type(), color(value.primary()), color(value.secondary()),
                    value.pattern() == null ? "PLAIN" : pattern(value.pattern()),
                    value.garment() == null ? "jersey" : garment(value.garment()),
                    color(value.accent()), value.playerName() == null ? null : playerName(value.playerName()),
                    value.playerNumber() == null ? null : playerNumber(value.playerNumber())));
        }
        try {
            return KIT_MAPPER.writeValueAsString(clean);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Could not serialize kit variants", ex);
        }
    }

    private String validateCrestDesign(String json) {
        if (json == null) return null; // Hand-uploaded logos remain supported.
        if (json.isBlank() || json.length() > 8192)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Projeto de emblema inválido");
        try {
            var root = KIT_MAPPER.readTree(json);
            int version = root.path("version").asInt(-1);
            if (!root.isObject() || (version != 1 && version != 2))
                throw new IllegalArgumentException("Unknown crest version");
            Set<String> allowed = new HashSet<>(Set.of("version","shape","symbol","pattern",
                    "primary","secondary","textColor","borderColor","patternOpacity",
                    "borderWidth","symbolSize","symbolY","showInitials","innerBorder",
                    "initialsSize","initialsY","customInitials","caption"));
            if (version == 2) allowed.add("layers");
            var fields = root.fieldNames();
            while (fields.hasNext()) if (!allowed.contains(fields.next()))
                throw new IllegalArgumentException("Unknown crest field");
            for (String key: List.of("shape","symbol","pattern","primary","secondary",
                    "textColor","borderColor","customInitials","caption")) {
                if (!root.path(key).isTextual()) throw new IllegalArgumentException("Missing text");
            }
            String shape = root.path("shape").asText();
            String symbol = root.path("symbol").asText();
            String pattern = root.path("pattern").asText();
            if (!Set.of("shield","rounded","circle","hex","diamond","square","oval","banner").contains(shape)
                || !Set.of("none","letter","star","bolt","crown","flame","heart","trophy",
                    "ball","basketball","volleyball","tennis","rugby","badminton","cricket",
                    "hockey","golf","cycling","running","swimming","boxing","target",
                    "mountain","wave").contains(symbol)
                || !Set.of("none","diagonal","stripes","hoops","split","chevron").contains(pattern))
                throw new IllegalArgumentException("Unknown crest style");
            for (String key: List.of("primary","secondary","textColor","borderColor"))
                if (!root.path(key).asText().matches("#[0-9a-fA-F]{6}"))
                    throw new IllegalArgumentException("Color invalid");
            if (!root.path("customInitials").asText().matches("[\\p{L}0-9]{0,5}")
                || root.path("caption").asText().length() > 22
                || root.path("caption").asText().chars().anyMatch(Character::isISOControl))
                throw new IllegalArgumentException("Text invalid");
            for (String key: List.of("showInitials","innerBorder"))
                if (!root.path(key).isBoolean()) throw new IllegalArgumentException("Flag invalid");
            int[][] ranges = {{0,100},{0,14},{30,112},{85,205},{16,54},{165,265}};
            String[] numbers = {"patternOpacity","borderWidth","symbolSize","symbolY",
                    "initialsSize","initialsY"};
            for (int i=0;i<numbers.length;i++) checkCrestInteger(root.path(numbers[i]),
                    ranges[i][0], ranges[i][1]);
            if (version == 2) {
                var layers = root.path("layers");
                if (!layers.isArray() || layers.size() > 6)
                    throw new IllegalArgumentException("Invalid crest layers");
                Set<String> ids = new HashSet<>();
                for (var layer:layers) {
                    if (!layer.isObject()) throw new IllegalArgumentException("Invalid layer");
                    Set<String> layerKeys = Set.of("id","kind","text","symbol","x","y",
                            "size","rotation","color","opacity");
                    var keys = layer.fieldNames();
                    while (keys.hasNext()) if (!layerKeys.contains(keys.next()))
                        throw new IllegalArgumentException("Unknown layer field");
                    for (String key:List.of("id","kind","text","symbol","color"))
                        if (!layer.path(key).isTextual())
                            throw new IllegalArgumentException("Invalid layer text");
                    String id=layer.path("id").asText();
                    String kind=layer.path("kind").asText();
                    String text=layer.path("text").asText();
                    if (!id.matches("[A-Za-z0-9_-]{1,32}") || !ids.add(id)
                        || !Set.of("text","symbol").contains(kind)
                        || text.length() > 18 || text.chars().anyMatch(Character::isISOControl)
                        || !layer.path("color").asText().matches("#[0-9a-fA-F]{6}"))
                        throw new IllegalArgumentException("Invalid layer values");
                    String icon=layer.path("symbol").asText();
                    if (!Set.of("star","bolt","crown","flame","heart","trophy","ball",
                        "basketball","volleyball","tennis","rugby","badminton","cricket",
                        "hockey","golf","cycling","running","swimming","boxing","target",
                        "mountain","wave").contains(icon))
                        throw new IllegalArgumentException("Invalid layer symbol");
                    checkCrestInteger(layer.path("x"),24,296);
                    checkCrestInteger(layer.path("y"),24,330);
                    checkCrestInteger(layer.path("size"),18,110);
                    checkCrestInteger(layer.path("rotation"),-180,180);
                    checkCrestInteger(layer.path("opacity"),0,100);
                }
            }
            return KIT_MAPPER.writeValueAsString(root);
        } catch (IllegalArgumentException | JsonProcessingException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Projeto de emblema inválido");
        }
    }

    private void checkCrestInteger(com.fasterxml.jackson.databind.JsonNode value,int min,int max) {
        if (!value.isIntegralNumber() || value.asInt() < min || value.asInt() > max)
            throw new IllegalArgumentException("Invalid crest numeric value");
    }

    private String garment(String input) {
        if (input == null) return null;
        return switch (input) {
            case "jersey", "singlet", "vest", "polo", "cycling", "rashguard" -> input;
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de equipamento inválido");
        };
    }

    private String playerName(String input) {
        String value = input.trim();
        if (value.isEmpty()) return null;
        if (value.length() > 14 || !value.matches("[\\p{L}0-9 .'-]+"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome do equipamento inválido");
        return value;
    }

    private String playerNumber(String input) {
        String value = input.trim();
        if (value.isEmpty()) return null;
        if (!value.matches("[0-9]{1,2}"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Número do equipamento inválido");
        return value;
    }

    private String color(String input) {
        if (input == null || input.isBlank()) return null;
        if (!input.matches("#[0-9a-fA-F]{6}"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cor do equipamento inválida");
        return input.toUpperCase(Locale.ROOT);
    }

    private void deleteLogo(String url, Long id) {
        if (mediaStorage == null || url == null) return;
        String prefix = "teams/" + id + "/";
        int start = url.indexOf(prefix);
        if (start < 0) return;
        try {
            mediaStorage.delete(url.substring(start));
        } catch (RuntimeException ex) {
            log.warn("Team {} previous logo cleanup failed", id, ex);
        }
    }

    private String clean(String value, int max) {
        if (value == null || value.isBlank()) return null;
        String clean = value.trim();
        if (clean.length() > max)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Campo demasiado longo");
        return clean;
    }

    /** Internal reference for associating a permanent identity with a competition entry. */
    @Transactional(readOnly = true)
    public SportsTeam entity(Long id) {
        return required(id);
    }

    private SportsTeam required(Long id) {
        return teams.findById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipa não encontrada"));
    }
}
