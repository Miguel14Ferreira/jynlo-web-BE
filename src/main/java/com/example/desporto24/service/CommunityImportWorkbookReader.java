package com.example.desporto24.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Normalizes spreadsheet exports to a Workbook for the existing
 * preview -> mapping -> validation -> import flow.
 * Text inputs are always kept as literal cells, never Excel formulas.
 */
final class CommunityImportWorkbookReader {

    private static final int MAX_ROWS = 20_000;
    private static final int MAX_COLUMNS = 128;
    private static final int MAX_CELL_LENGTH = 32_000;

    private CommunityImportWorkbookReader() {
    }

    static Workbook open(MultipartFile file) throws IOException {
        String filename = file.getOriginalFilename();
        String name = filename == null ? "" : filename.toLowerCase(Locale.ROOT);
        if (name.endsWith(".xls") || name.endsWith(".xlsx")) {
            try (var input = new BufferedInputStream(file.getInputStream())) {
                try {
                    return WorkbookFactory.create(input);
                } catch (RuntimeException malformedWorkbook) {
                    // POI may throw unchecked exceptions for malformed Excel bytes.
                    // Normalize them to IOException so all historical import previews
                    // return the existing localized HTTP 400 read-error contract.
                    throw new IOException("Ficheiro Excel inválido", malformedWorkbook);
                }
            }
        }
        if (name.endsWith(".ods")) {
            return openOds(file.getBytes());
        }
        if (!name.endsWith(".csv") && !name.endsWith(".tsv")) {
            throw new IllegalArgumentException("Formato de importação não suportado");
        }

        final String text;
        try {
            text = StandardCharsets.UTF_8.newDecoder()
                    .onMalformedInput(CodingErrorAction.REPORT)
                    .onUnmappableCharacter(CodingErrorAction.REPORT)
                    .decode(ByteBuffer.wrap(file.getBytes()))
                    .toString()
                    .replaceFirst("^\\uFEFF", "");
        } catch (CharacterCodingException exception) {
            throw new IllegalArgumentException(
                    "O ficheiro CSV/TSV deve estar codificado em UTF-8", exception);
        }
        if (text.indexOf('\0') >= 0) {
            throw new IllegalArgumentException("O ficheiro contém caracteres binários inválidos");
        }

        char separator = name.endsWith(".tsv") ? '\t' : detectSeparator(text);
        Workbook workbook = new XSSFWorkbook();
        try (CSVParser parser = CSVFormat.DEFAULT.builder()
                .setDelimiter(separator)
                .setIgnoreEmptyLines(true)
                .get().parse(new StringReader(text))) {
            Sheet sheet = workbook.createSheet("Dados");
            int rowIndex = 0;
            for (CSVRecord record : parser) {
                if (rowIndex >= MAX_ROWS) {
                    throw new IllegalArgumentException("O ficheiro excede 20 000 linhas");
                }
                if (record.size() > MAX_COLUMNS) {
                    throw new IllegalArgumentException("O ficheiro excede 128 colunas");
                }
                Row row = sheet.createRow(rowIndex++);
                for (int index = 0; index < record.size(); index++) {
                    String value = record.get(index);
                    if (value.length() > MAX_CELL_LENGTH) {
                        throw new IllegalArgumentException("Uma célula excede 32 000 caracteres");
                    }
                    // createCell(String) forces a literal value, so '=' from a CSV is never evaluated.
                    row.createCell(index).setCellValue(value);
                }
            }
            return workbook;
        } catch (IOException | RuntimeException exception) {
            workbook.close();
            throw exception;
        }
    }

    /**
     * OpenDocument spreadsheets (LibreOffice, Numbers/Google exports): reads the first
     * table of content.xml as literal text cells. DTDs and external entities are refused.
     */
    static Workbook openOds(byte[] bytes) throws IOException {
        byte[] content = null;
        try (var zip = new java.util.zip.ZipInputStream(new java.io.ByteArrayInputStream(bytes))) {
            java.util.zip.ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if ("content.xml".equals(entry.getName())) {
                    content = zip.readNBytes(20 * 1024 * 1024 + 1);
                    if (content.length > 20 * 1024 * 1024) {
                        throw new IllegalArgumentException("O ficheiro .ods é demasiado grande");
                    }
                    break;
                }
            }
        }
        if (content == null) throw new IOException("Ficheiro .ods inválido");
        Workbook workbook = new XSSFWorkbook();
        try {
            var factory = javax.xml.parsers.DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);
            var document = factory.newDocumentBuilder().parse(new java.io.ByteArrayInputStream(content));
            String table = "urn:oasis:names:tc:opendocument:xmlns:table:1.0";
            var tables = document.getElementsByTagNameNS(table, "table");
            if (tables.getLength() == 0) throw new IllegalArgumentException("O ficheiro está vazio");
            Sheet sheet = workbook.createSheet("Dados");
            int rowIndex = 0;
            var rows = ((org.w3c.dom.Element) tables.item(0)).getElementsByTagNameNS(table, "table-row");
            for (int r = 0; r < rows.getLength() && rowIndex < MAX_ROWS; r++) {
                var rowElement = (org.w3c.dom.Element) rows.item(r);
                java.util.List<String> values = new java.util.ArrayList<>();
                for (var node = rowElement.getFirstChild(); node != null; node = node.getNextSibling()) {
                    if (!(node instanceof org.w3c.dom.Element cell) || !table.equals(cell.getNamespaceURI())) continue;
                    if (!"table-cell".equals(cell.getLocalName()) && !"covered-table-cell".equals(cell.getLocalName())) continue;
                    String text = odsCellText(cell);
                    int repeat = parseRepeat(cell.getAttributeNS(table, "number-columns-repeated"));
                    // Trailing empty cells are often repeated thousands of times: never expand them.
                    if (text.isEmpty() && repeat > 1) repeat = Math.min(repeat, MAX_COLUMNS - values.size());
                    for (int i = 0; i < repeat && values.size() < MAX_COLUMNS; i++) values.add(text);
                }
                while (!values.isEmpty() && values.get(values.size() - 1).isEmpty()) values.remove(values.size() - 1);
                if (values.isEmpty()) continue;
                int repeatRows = Math.min(parseRepeat(rowElement.getAttributeNS(table, "number-rows-repeated")), 1000);
                for (int k = 0; k < repeatRows && rowIndex < MAX_ROWS; k++) {
                    Row row = sheet.createRow(rowIndex++);
                    for (int i = 0; i < values.size(); i++) {
                        String value = values.get(i);
                        if (value.length() > MAX_CELL_LENGTH) value = value.substring(0, MAX_CELL_LENGTH);
                        row.createCell(i).setCellValue(value);
                    }
                }
            }
            return workbook;
        } catch (javax.xml.parsers.ParserConfigurationException | org.xml.sax.SAXException exception) {
            workbook.close();
            throw new IOException("Ficheiro .ods inválido", exception);
        } catch (IOException | RuntimeException exception) {
            workbook.close();
            throw exception;
        }
    }

    private static String odsCellText(org.w3c.dom.Element cell) {
        String text = "urn:oasis:names:tc:opendocument:xmlns:text:1.0";
        var paragraphs = cell.getElementsByTagNameNS(text, "p");
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < paragraphs.getLength(); i++) {
            if (i > 0) out.append('\n');
            out.append(paragraphs.item(i).getTextContent());
        }
        return out.toString().trim();
    }

    private static int parseRepeat(String raw) {
        if (raw == null || raw.isBlank()) return 1;
        try {
            return Math.max(1, Math.min(Integer.parseInt(raw.trim()), 100_000));
        } catch (NumberFormatException ignored) {
            return 1;
        }
    }

    private static char detectSeparator(String text) {
        boolean quoted = false;
        int commas = 0;
        int semicolons = 0;
        int tabs = 0;
        for (int i = 0; i < text.length() && i < 16_384; i++) {
            char value = text.charAt(i);
            if (value == '"') {
                if (quoted && i + 1 < text.length() && text.charAt(i + 1) == '"') {
                    i++;
                } else {
                    quoted = !quoted;
                }
            } else if (!quoted) {
                if (value == '\n' || value == '\r') break;
                if (value == ',') commas++;
                if (value == ';') semicolons++;
                if (value == '\t') tabs++;
            }
        }
        if (tabs > commas && tabs > semicolons) return '\t';
        return semicolons > commas ? ';' : ',';
    }
}
