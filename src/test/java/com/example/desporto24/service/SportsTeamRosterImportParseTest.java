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

    @Test
    void readsAPastedPlainListOfPlayers() throws Exception {
        var file = new org.springframework.mock.web.MockMultipartFile("file", "lista.txt", "text/plain",
                "10 João Silva\n- Rui Costa; 7; Médio\nAna Santos - 3\n\n".getBytes(java.nio.charset.StandardCharsets.UTF_8));
        try (var wb = SportsTeamRosterImportService.pastedList(file)) {
            var parsed = SportsTeamRosterImportService.parse(wb.getSheetAt(0));
            assertEquals(3, parsed.rows().size());
            assertEquals("João Silva", parsed.rows().get(0).name());
            assertEquals(10, parsed.rows().get(0).shirtNumber());
            assertEquals("Rui Costa", parsed.rows().get(1).name());
            assertEquals(7, parsed.rows().get(1).shirtNumber());
            assertEquals("Médio", parsed.rows().get(1).position());
            assertEquals(3, parsed.rows().get(2).shirtNumber());
        }
    }

    @Test
    void readsCellsCopiedFromASpreadsheetWithTheirHeader() throws Exception {
        var file = new org.springframework.mock.web.MockMultipartFile("file", "lista.txt", "text/plain",
                "Nome\tNº\tPosição\nEva\t1\tGuarda-redes\n".getBytes(java.nio.charset.StandardCharsets.UTF_8));
        try (var wb = SportsTeamRosterImportService.pastedList(file)) {
            var parsed = SportsTeamRosterImportService.parse(wb.getSheetAt(0));
            assertEquals(1, parsed.rows().size());
            assertEquals("Guarda-redes", parsed.rows().get(0).position());
            assertEquals(1, parsed.rows().get(0).shirtNumber());
        }
    }

    @Test
    void readsAnOpenDocumentSpreadsheet() throws Exception {
        String xml = "<?xml version=\"1.0\"?><office:document-content xmlns:office=\"urn:oasis:names:tc:opendocument:xmlns:office:1.0\" "
                + "xmlns:table=\"urn:oasis:names:tc:opendocument:xmlns:table:1.0\" xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\">"
                + "<office:body><office:spreadsheet><table:table table:name=\"Plantel\">"
                + "<table:table-row><table:table-cell><text:p>Nome</text:p></table:table-cell><table:table-cell><text:p>Número</text:p></table:table-cell>"
                + "<table:table-cell table:number-columns-repeated=\"1000\"/></table:table-row>"
                + "<table:table-row><table:table-cell><text:p>Ana Silva</text:p></table:table-cell><table:table-cell><text:p>9</text:p></table:table-cell></table:table-row>"
                + "<table:table-row table:number-rows-repeated=\"1048570\"><table:table-cell table:number-columns-repeated=\"1024\"/></table:table-row>"
                + "</table:table></office:spreadsheet></office:body></office:document-content>";
        var bytes = new java.io.ByteArrayOutputStream();
        try (var zip = new java.util.zip.ZipOutputStream(bytes)) {
            zip.putNextEntry(new java.util.zip.ZipEntry("content.xml"));
            zip.write(xml.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            zip.closeEntry();
        }
        try (var wb = CommunityImportWorkbookReader.openOds(bytes.toByteArray())) {
            var parsed = SportsTeamRosterImportService.parse(wb.getSheetAt(0));
            assertEquals(1, parsed.rows().size());
            assertEquals("Ana Silva", parsed.rows().get(0).name());
            assertEquals(9, parsed.rows().get(0).shirtNumber());
        }
    }
}
