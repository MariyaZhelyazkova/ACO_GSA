package com.company;

import com.company.core.*;
import com.company.io.FastaReader;
import com.company.io.exceptions.FastaReaderException;

public class Main {

    public static void main(String[] args) {
        SequenceList sequenceList = new SequenceList();

        FastaReader reader = new FastaReader();

        try {
            sequenceList.addSequence(reader.read("1.txt"));
            sequenceList.addSequence(reader.read("2.txt"));
            sequenceList.addSequence(reader.read("3.txt"));
            sequenceList.addSequence(reader.read("4.txt"));
        } catch (FastaReaderException e) {
            e.printStackTrace();
            return;
        }

        AntMap antMap = new AntMap();

        Ant ant = new Ant(sequenceList, antMap);

        for (int i = 0; i < 10000; i++)
            ant.go();

        AlignedList alignedList = ant.getBestList();

        System.out.println("Best Score: " + alignedList.getScore());
        System.out.println("Best: \n" + alignedList);
    }
}
