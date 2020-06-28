package com.company.utils.typeparser;

import com.company.utils.typeparser.exceptions.TypeParseException;
import com.company.utils.typeparser.interfaces.ITypeParser;

import java.util.HashMap;
import java.util.Map;

public class StringToTypeParser {
    private final Map<Class<?>, ITypeParser<?>> parserMap = new HashMap<>();

    public StringToTypeParser() {
        registerParser(Integer.class, new IntegerTypeParser());
        registerParser(String.class, new StringTypeParser());
    }

    public <T> void registerParser(Class<T> type, ITypeParser<T> parser) {
        parserMap.put(type, parser);
    }

    public void unregisterTypeParser(Class<?> type) {
        parserMap.remove(type);
    }

    @SuppressWarnings("unchecked")
    public <T> T parse(String value, Class<T> type) throws TypeParseException {
        var parser = parserMap.get(type);

        if (parser == null) {
            throw new TypeParseException("No parser registered for the given type");
        }

        return (T) parser.parse(value);
    }
}
