package com.company;

public class Main {

    public static void main(String[] args) throws Exception {
        // write your code here
        SequenceList sequenceList = new SequenceList();
        Sequence sequence = new Sequence();
        sequence.setData("ABBCAABBCC".toCharArray());
        sequenceList.addSequence(sequence);

        sequence = new Sequence();
        sequence.setData("ABCAABBBCA".toCharArray());
        sequenceList.addSequence(sequence);


        sequence = new Sequence();
        sequence.setData("BABCAABBAC".toCharArray());
        sequenceList.addSequence(sequence);

        Ant ant = new Ant(sequenceList);
        for (int i = 0; i < 10000; i++)
            ant.go();

        System.out.println("Best Score: " + ant.getBestList().getScore());
        System.out.println("Best: " + ant.getBestList());
    }
}
