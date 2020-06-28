package com.company.utils.typeparser;

import com.company.utils.typeparser.exceptions.TypeParseException;
import com.company.utils.typeparser.interfaces.ITypeParser;

public class StringTypeParser implements ITypeParser<String> {
    @Override
    public String parse(String value) throws TypeParseException {
        return value;
    }
}
