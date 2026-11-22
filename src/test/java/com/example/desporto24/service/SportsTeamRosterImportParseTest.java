package com.example.desporto24.service;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;

class SportsTeamRosterImportParseTest {

    private static XSSFWorkbook sheet(String[]... rows) {
        XSSFWorkbook wb = new XSSFWorkbook();
        var sheet = wb.createSheet("Plantel");
        for (int r = 0; r < rows.length; r++) {
            var row = sheet.createRow(r);
            for (int c = 0; c < rows[r].length; c++) row.createCell(c).setCellValue(rows[r][c]);
        }
        return wb;
    }

    @Test
    void readsNameNumberPositionAndUsernameButNeverContacts() throws Exception {
        try (var wb = sheet(
                new String[]{"Nº", "Nome", "Posição", "Email", "Utilizador Jynlo"},
                new String[]{"10", "Ana Silva", "Avançada", "ana@example.com", "@ana_s"},
                new String[]{"#7", "Rui Costa", "", "rui@example.com", ""},
                new String[]{"", "", "", "", ""},
                new String[]{"abc", "Eva", "Guarda-redes", "", "not valid!"})) {
            var parsed = SportsTeamRosterImportService.parse(wb.getSheetAt(0));
            assertEquals(3, parsed.rows().size());
            var ana = parsed.rows().get(0);
            assertEquals("Ana Silva", ana.name());
            assertEquals(10, ana.shirtNumber());
            assertEquals("Avançada", ana.position());
            assertEquals("ana_s", ana.username());
            assertEquals(7, parsed.rows().get(1).shirtNumber());
            assertNull(parsed.rows().get(1).username());
            assertNull(parsed.rows().get(2).shirtNumber());
            assertNull(parsed.rows().get(2).username());
            assertEquals(2, parsed.warnings().size());
            assertFalse(parsed.rows().toString().contains("@example.com"));
        }
    }

    @Test
    void requiresANameColumn() throws Exception {
        try (var wb = sheet(new String[]{"Número", "Posição"}, new String[]{"1", "GR"})) {
            var error = assertThrows(ResponseStatusException.class,
                    () -> SportsTeamRosterImportService.parse(wb.getSheetAt(0)));
            assertTrue(error.getReason().contains("coluna do nome"));
        }
    }

    @Test
    void suggestsOnlyClearNameMatches() {
        var rui = new com.example.desporto24.model.Profile();
        rui.setUsername("ruicosta"); rui.setFullName("Rui Miguel Costa");
        assertTrue(SportsTeamRosterImportService.sameName("rui costa", rui));
        assertFalse(SportsTeamRosterImportService.sameName("rui silva", rui));
        assertFalse(SportsTeamRosterImportService.sameName("rui", rui));
        assertTrue(SportsTeamRosterImportService.sameName("ruicosta", rui));
    }

    @Test
    void claimTokensAreStoredOnlyAsHashes() {
        String hash = SportsTeamRosterImportService.sha256("123e4567-e89b-12d3-a456-426614174000");
        assertEquals(64, hash.length());
        assertNotEquals("123e4567-e89b-12d3-a456-426614174000", hash);
    }
}
