package org.jabref.logic.exporter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.jabref.logic.util.StandardFileType;
import org.jabref.model.database.BibDatabaseContext;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;

import org.jspecify.annotations.NonNull;

/// Exports bibliography entries to a JSON array format.
/// Each entry becomes a JSON object containing its citation key,
/// entry type, and all populated standard fields.
public class JsonExporter extends Exporter {

    public JsonExporter() {
        super("json", "JSON", StandardFileType.JSON);
    }

    /// @param databaseContext the database to export from
    /// @param file            the file to write to
    /// @param entries         a list containing all entries that should be exported
    @Override
    public void export(@NonNull BibDatabaseContext databaseContext,
                       @NonNull Path file,
                       @NonNull List<BibEntry> entries) throws IOException {
        if (entries.isEmpty()) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[\n");

        for (int i = 0; i < entries.size(); i++) {
            BibEntry entry = entries.get(i);
            List<String> fields = new ArrayList<>();

            entry.getCitationKey().ifPresent(key ->
                    fields.add("    \"citationkey\": " + toJsonString(key)));

            fields.add("    \"entrytype\": " +
                    toJsonString(entry.getType().getName()));

            for (StandardField field : StandardField.values()) {
                entry.getField(field).ifPresent(value ->
                        fields.add("    \"" + field.getName() + "\": " +
                                toJsonString(value)));
            }

            sb.append("  {\n");
            sb.append(String.join(",\n", fields));
            sb.append("\n  }");

            if (i < entries.size() - 1) {
                sb.append(",");
            }
            sb.append("\n");
        }

        sb.append("]\n");

        Files.writeString(file, sb.toString(), StandardCharsets.UTF_8);
    }

    private String toJsonString(String value) {
        return "\"" + value
                .replace("\\_", "_")
                .replace("\\&", "&")
                .replace("{", "")
                .replace("}", "")
                .replace("--", "-")
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", " ")
                .replace("\r", "")
                + "\"";
    }
}
