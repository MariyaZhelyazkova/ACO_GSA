package com.company.io.console;

import com.company.utils.typeparser.StringToTypeParser;
import com.company.utils.typeparser.exceptions.TypeParseException;

import java.util.HashMap;

public class ParsedConsoleData {
    private final HashMap<String, String> data = new HashMap<>();

    void add(String key, String value) {
        data.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) throws TypeParseException {
        var value = data.get(key);

        assert value != null;

        var parser = new StringToTypeParser();
        return parser.parse(value, type);
    }
}
