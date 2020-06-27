package com.company.core;

public class AlignedList extends SequenceList {
    private long score = Long.MIN_VALUE;

    public synchronized void assign(AlignedList other) {
        if (this == other) {
            return;
        }

        clear();

        score = other.getScore();

        for (var i = 0; i < other.getCount(); i++) {
            addSequence(other.getSequence(i));
        }
    }

    public synchronized long getScore() {
        return score;
    }

    public synchronized void setScore(long score) {
        this.score = score;
    }
}
