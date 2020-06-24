package com.company;

public class Symbol {
    private int score;
    private int position;

    public Symbol(int score, int position) {
        this.score = score;
        this.position = position;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
