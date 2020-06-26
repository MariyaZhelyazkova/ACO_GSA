package com.company.core;

public class AlignedList extends SequenceList {
    private long score = Long.MIN_VALUE;

    public void assign(AlignedList other) {
        if (this == other) {
            return;
        }

        clear();

        score = other.getScore();

        for (int i = 0; i < other.getCount(); i++) {
            addSequence(other.getSequence(i));
        }
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }
}
