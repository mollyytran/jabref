package org.jabref.logic.exporter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.jabref.model.database.BibDatabase;
import org.jabref.model.database.BibDatabaseContext;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.entry.types.StandardEntryType;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class JsonExporterTest {

    private JsonExporter exporter;
    private BibDatabaseContext databaseContext;

    @BeforeEach
    void setUp() {
        exporter = new JsonExporter();
        databaseContext = new BibDatabaseContext(new BibDatabase());
    }

    @Test
    void exportsSingleEntryWithAllFields(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("export.json");

        BibEntry entry = new BibEntry(StandardEntryType.Article)
                .withCitationKey("Smith2020")
                .withField(StandardField.AUTHOR, "Smith, John")
                .withField(StandardField.TITLE, "A Great Paper")
                .withField(StandardField.YEAR, "2020")
                .withField(StandardField.DOI, "10.1234/abc");

        exporter.export(databaseContext, file, List.of(entry));

        String expected = """
                [
                  {
                    "citationkey": "Smith2020",
                    "entrytype": "article",
                    "author": "Smith, John",
                    "doi": "10.1234/abc",
                    "title": "A Great Paper",
                    "year": "2020"
                  }
                ]
                """;

        assertEquals(expected, Files.readString(file));
    }

    @Test
    void exportsMissingFieldsAreOmitted(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("export.json");

        BibEntry entry = new BibEntry(StandardEntryType.Article)
                .withCitationKey("Jones2019")
                .withField(StandardField.TITLE, "Another Paper");

        exporter.export(databaseContext, file, List.of(entry));

        String expected = """
                [
                  {
                    "citationkey": "Jones2019",
                    "entrytype": "article",
                    "title": "Another Paper"
                  }
                ]
                """;

        assertEquals(expected, Files.readString(file));
    }

    @Test
    void exportsMultipleEntriesAsArray(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("export.json");

        BibEntry entry1 = new BibEntry(StandardEntryType.Article)
                .withCitationKey("Smith2020")
                .withField(StandardField.TITLE, "First Paper");

        BibEntry entry2 = new BibEntry(StandardEntryType.Book)
                .withCitationKey("Jones2019")
                .withField(StandardField.TITLE, "Second Paper");

        exporter.export(databaseContext, file, List.of(entry1, entry2));

        String expected = """
                [
                  {
                    "citationkey": "Smith2020",
                    "entrytype": "article",
                    "title": "First Paper"
                  },
                  {
                    "citationkey": "Jones2019",
                    "entrytype": "book",
                    "title": "Second Paper"
                  }
                ]
                """;

        assertEquals(expected, Files.readString(file));
    }

    @Test
    void emptyEntryListWritesNoFile(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("export.json");

        exporter.export(databaseContext, file, List.of());

        assertFalse(Files.exists(file));
    }

    @Test
    void escapesDoubleQuotesInFieldValues(@TempDir Path tempDir) throws Exception {
        Path file = tempDir.resolve("export.json");

        BibEntry entry = new BibEntry(StandardEntryType.Article)
                .withCitationKey("Test2020")
                .withField(StandardField.TITLE, "A \"Quoted\" Title");

        exporter.export(databaseContext, file, List.of(entry));

        String expected = """
                [
                  {
                    "citationkey": "Test2020",
                    "entrytype": "article",
                    "title": "A \\"Quoted\\" Title"
                  }
                ]
                """;

        assertEquals(expected, Files.readString(file));
    }
}
