package com.company;

import java.util.Arrays;

public class Sequence {
    private static final int ROW_LENGTH = 100;

    private char[] header;
    private char[] data;
    private char[] comment;
    private int dataLength = 0;
    private int dataSize = 0;

    public Sequence() {
        this.header = new char[ROW_LENGTH];
        this.comment = new char[ROW_LENGTH];
    }

    public int getDataLength() {
        return dataLength;
    }

    public char[] getHeader() {
        return header;
    }

    public void setHeader(char[] header) {
        this.header = header;
    }

    public char[] getData() {
        return data;
    }

    public void setData(char[] data) {
        this.data = data;
        this.dataSize = data.length;
        this.dataLength = this.dataSize;
    }

    public char[] getComment() {
        return comment;
    }

    public void setComment(char[] comment) {
        this.comment = comment;
    }

    public int getDataSize() {
        return dataSize;
    }

    @Override
    public String toString() {
        return "Sequence{" +
                "data=" + Arrays.toString(data) +
                '}';
    }
}
