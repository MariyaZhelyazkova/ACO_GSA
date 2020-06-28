package com.company.utils.typeparser;

import com.company.utils.typeparser.exceptions.TypeParseException;
import com.company.utils.typeparser.interfaces.ITypeParser;

public class IntegerTypeParser implements ITypeParser<Integer> {
    @Override
    public Integer parse(String value) throws TypeParseException {
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException e) {
            throw new TypeParseException("Unable to cast value to Boolean.", e);
        }
    }
}
