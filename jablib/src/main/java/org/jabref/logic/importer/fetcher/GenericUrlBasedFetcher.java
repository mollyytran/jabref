package org.jabref.logic.importer.fetcher;

import java.util.List;

import org.jabref.logic.importer.UrlBasedFetcher;
import org.jabref.logic.util.strings.StringUtil;
import org.jabref.model.entry.BibEntry;
import org.jabref.model.entry.field.StandardField;
import org.jabref.model.entry.types.StandardEntryType;

public class GenericUrlBasedFetcher implements UrlBasedFetcher {

    @Override
    public String getName() {
        return "URL Fetcher";
    }

    @Override
    public List<BibEntry> performSearch(String url) {
        if (StringUtil.isBlank(url)) {
            return List.of();
        }

        BibEntry entry = new BibEntry(StandardEntryType.Misc)
                .withField(StandardField.URL, url.trim());

        return List.of(entry);
    }
}
