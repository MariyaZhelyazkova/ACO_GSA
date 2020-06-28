package com.company.utils.typeparser.interfaces;

import com.company.utils.typeparser.exceptions.TypeParseException;

public interface ITypeParser<T> {
    T parse(String value) throws TypeParseException;
}
