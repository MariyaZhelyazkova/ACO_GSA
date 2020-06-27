package com.company.core;

public class Sequence {
    private String header;
    private String sequence;

    public Sequence(String header, String sequence) {
        this.header = header;
        this.sequence = sequence;
    }

    public Sequence(String sequence) {
        this.sequence = sequence;
    }

    public Sequence(Sequence sequence) {
        header = sequence.header;
        this.sequence = sequence.sequence;
    }

    public int getLength() {
        return sequence.length();
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    @Override
    public String toString() {
        return sequence;
    }
}
