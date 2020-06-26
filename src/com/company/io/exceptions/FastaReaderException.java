package com.company.io.exceptions;

public class FastaReaderException extends Exception {
    public FastaReaderException(String message) {
        super(message);
    }

    public FastaReaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
