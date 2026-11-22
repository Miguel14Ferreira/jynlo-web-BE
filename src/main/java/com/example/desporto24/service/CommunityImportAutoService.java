package com.example.desporto24.service;

import com.example.desporto24.model.dto.community.CommunityImportContextRequest;
import com.example.desporto24.model.dto.community.CommunityImportMappingRequest;
import com.example.desporto24.model.dto.community.CommunityImportPreviewResponse;
import com.example.desporto24.model.dto.community.CommunityImportRoundsPlanResponse;
import com.example.desporto24.model.dto.community.CommunityImportRoundsResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * One-step history import for the phone: only files whose columns are
 * recognised with high confidence (e.g. the Jynlo template). Anything else
 * goes through the full column-by-column review on the website.
 */
@Service
@RequiredArgsConstructor
public class CommunityImportAutoService {
    static final double MIN_CONFIDENCE = 0.9;

    private final CommunityImportService imports;

    public record AutoPlan(List<String> columns, CommunityImportRoundsPlanResponse plan) {}

    public AutoPlan plan(Long communityId, String username, MultipartFile file, Long seasonId, Integer defaultYear) {
        Mapped mapped = mapping(communityId, username, file);
        return new AutoPlan(mapped.columns(), imports.finalRoundsPlan(communityId, username, file,
                mapped.mapping(), context(seasonId, defaultYear)));
    }

    public CommunityImportRoundsResultResponse confirm(Long communityId, String username, MultipartFile file,
                                                       Long seasonId, Integer defaultYear) {
        Mapped mapped = mapping(communityId, username, file);
        return imports.confirmRounds(communityId, username, file, mapped.mapping(), context(seasonId, defaultYear));
    }

    record Mapped(CommunityImportMappingRequest mapping, List<String> columns) {}

    private Mapped mapping(Long communityId, String username, MultipartFile file) {
        CommunityImportPreviewResponse preview = imports.preview(communityId, username, file);
        return autoMapping(preview);
    }

    static Mapped autoMapping(CommunityImportPreviewResponse preview) {
        CommunityImportPreviewResponse.SheetPreview sheet = preview.sheets().stream()
                .filter(s -> "TABULAR".equals(s.structure())).findFirst()
                .orElse(preview.sheets().isEmpty() ? null : preview.sheets().get(0));
        if (sheet == null) throw new IllegalArgumentException("O ficheiro não tem dados para importar");
        List<CommunityImportMappingRequest.ColumnMapping> columns = new ArrayList<>();
        List<String> recognised = new ArrayList<>();
        boolean player = false, date = false;
        for (var suggestion : sheet.columnSuggestions()) {
            boolean confident = suggestion.confidence() >= MIN_CONFIDENCE && !"UNKNOWN".equals(suggestion.field());
            String field = confident ? suggestion.field() : "IGNORE";
            if (confident && columns.stream().anyMatch(c -> c.field().equals(field))) {
                columns.add(new CommunityImportMappingRequest.ColumnMapping(suggestion.columnIndex(), "IGNORE"));
                continue;
            }
            columns.add(new CommunityImportMappingRequest.ColumnMapping(suggestion.columnIndex(), field));
            if (confident) {
                recognised.add(suggestion.header());
                player |= "PLAYER".equals(field);
                date |= "DATE".equals(field);
            }
        }
        if (!player || !date) {
            throw new IllegalArgumentException("Não reconhecemos as colunas deste ficheiro. "
                    + "Usa o modelo Jynlo (Data, Jogador, Resultado…) ou importa no computador, onde podes escolher cada coluna.");
        }
        return new Mapped(new CommunityImportMappingRequest(sheet.name(), columns), recognised);
    }

    private static CommunityImportContextRequest context(Long seasonId, Integer defaultYear) {
        if (seasonId == null) throw new IllegalArgumentException("Escolhe a época para onde vão os jogos");
        return new CommunityImportContextRequest(seasonId, defaultYear, null, null, null);
    }
}
