package com.example.desporto24.service;

import com.example.desporto24.model.dto.community.CommunityImportPreviewResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CommunityImportAutoServiceTest {
    private static CommunityImportPreviewResponse.ColumnSuggestion col(int i, String h, String f, double c) {
        return new CommunityImportPreviewResponse.ColumnSuggestion(i, h, f, c);
    }

    @Test
    void templateColumnsAreMappedAndUncertainOnesIgnored() {
        var sheet = new CommunityImportPreviewResponse.SheetPreview("Dados", 5, "TABULAR",
                List.of("Data", "Jogador", "Resultado", "Notas"), List.of(),
                List.of(col(0, "Data", "DATE", 0.95), col(1, "Jogador", "PLAYER", 0.95),
                        col(2, "Resultado", "RESULT", 0.9), col(3, "Notas", "DATE", 0.65)));
        var mapped = CommunityImportAutoService.autoMapping(new CommunityImportPreviewResponse("x.csv", List.of(sheet)));
        assertEquals("Dados", mapped.mapping().sheetName());
        assertEquals(List.of("DATE", "PLAYER", "RESULT", "IGNORE"),
                mapped.mapping().columns().stream().map(c -> c.field()).toList());
        assertEquals(List.of("Data", "Jogador", "Resultado"), mapped.columns());
    }

    @Test
    void filesWithoutRecognisedPlayerAndDateGoToTheWebsite() {
        var sheet = new CommunityImportPreviewResponse.SheetPreview("Folha", 5, "TABULAR",
                List.of("Nome", "Pontos"), List.of(),
                List.of(col(0, "Nome", "PLAYER", 0.95), col(1, "Pontos", "POINTS", 0.9)));
        var error = assertThrows(IllegalArgumentException.class, () ->
                CommunityImportAutoService.autoMapping(new CommunityImportPreviewResponse("x.csv", List.of(sheet))));
        assertTrue(error.getMessage().contains("modelo Jynlo"));
    }
}
