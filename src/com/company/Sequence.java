package com.company;

public class Sequence {
    private String header;
    private String comment;
    private String data;

    public Sequence(String data) {
        this.data = data;
    }

    public Sequence(Sequence sequence)
    {
        header = sequence.header;
        comment = sequence.comment;
        data = sequence.data;
    }

    public int getLength() {
        return data.length();
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return  data;
    }
}
