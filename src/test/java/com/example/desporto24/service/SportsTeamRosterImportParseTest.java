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
}
