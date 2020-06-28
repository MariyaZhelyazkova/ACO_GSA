package com.company.utils.typeparser.exceptions;

public class TypeParseException extends Exception {
    public TypeParseException(String message) {
        super(message);
    }

    public TypeParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
