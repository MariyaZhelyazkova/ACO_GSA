package com.company.core;

import java.util.List;
import java.util.Vector;

public class SequenceList {
    private final List<Sequence> sequenceList = new Vector<>(20);

    public void addSequence(Sequence sequence) {
        this.sequenceList.add(new Sequence(sequence));
    }

    public Sequence getSequence(int i) {
        return sequenceList.get(i);
    }

    public int getCount() {
        return sequenceList.size();
    }

    public int maxSequenceLength() {
        var maxLength = 0;

        for (var sequence : sequenceList) {
            var length = sequence.getLength();

            if (length > maxLength)
                maxLength = length;
        }

        return maxLength;
    }

    public int missingGapsToLength(int sequenceLength) {
        var sum = 0;

        for (var sequence : sequenceList) {
            sum += sequenceLength - sequence.getLength();
        }

        return sum;
    }

    public void clear() {
        sequenceList.clear();
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();

        for (var sequence : sequenceList) {
            builder.append(sequence.getSequence());
            builder.append('\n');
        }

        return builder.toString();
    }
}
