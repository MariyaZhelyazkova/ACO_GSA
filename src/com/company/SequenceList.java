package com.company;

import java.util.List;
import java.util.Vector;

public class SequenceList {
    private List<Sequence> sequenceList = new Vector<>();

    @Override
    public String toString() {
        return "SequenceList{" +
                "sequenceList=" + sequenceList +
                '}';
    }

    public void addSequence(Sequence sequence) {
        this.sequenceList.add(sequence);
    }

    public Sequence getSequence(int i) throws Exception {
        if (i < 0 || i >= sequenceList.size())
            throw new Exception("Out Of Bounds");
        return sequenceList.get(i);
    }

    public int getSequenceCount(){
        return sequenceList.size();
    }

    public int maxSequenceLength() {
        int maxLength = 0;

        for (Sequence s : sequenceList){
            if(s.getDataLength() > maxLength)
                maxLength = s.getDataLength();
        }

        return maxLength;
    }

    public int missingGapsToLength(int seqLength) throws Exception {
        if (seqLength > maxSequenceLength())
            throw new Exception("Out of Bounds");

        int sum = 0;
        for (Sequence s : sequenceList){
            sum += seqLength - s.getDataLength();
        }

        return sum;
    }

    public void clear(){
        sequenceList.clear();
    }
}
