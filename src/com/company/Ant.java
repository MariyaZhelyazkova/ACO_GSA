package com.company;

import java.util.Random;

public class Ant {
    private final static String AMINO_ACID_ORDER = "ABCDEFGHIKLMNPQRSTVWXYZ";
    private final static int[] blosum62mt2 = {
            8,
            -4, 8,
            0, -6, 18,
            -4, 8, -6, 12,
            -2, 2, -8, 4, 10,
            -4, -6, -4, -6, -6, 12,
            0, -2, -6, -2, -4, -6, 12,
            -4, 0, -6, -2, 0, -2, -4, 16,
            -2, -6, -2, -6, -6, 0, -8, -6, 8,
            -2, 0, -6, -2, 2, -6, -4, -2, -6, 10,
            -2, -8, -2, -8, -6, 0, -8, -6, 4, -4, 8,
            -2, -6, -2, -6, -4, 0, -6, -4, 2, -2, 4, 10,
            -4, 6, -6, 2, 0, -6, 0, 2, -6, 0, -6, -4, 12,
            -2, -4, -6, -2, -2, -8, -4, -4, -6, -2, -6, -4, -4, 14,
            -2, 0, -6, 0, 4, -6, -4, 0, -6, 2, -4, 0, 0, -2, 10,
            -2, -2, -6, -4, 0, -6, -4, 0, -6, 4, -4, -2, 0, -4, 2, 10,
            2, 0, -2, 0, 0, -4, 0, -2, -4, 0, -4, -2, 2, -2, 0, -2, 8,
            0, -2, -2, -2, -2, -4, -4, -4, -2, -2, -2, -2, 0, -2, -2, -2, 2, 10,
            0, -6, -2, -6, -4, -2, -6, -6, 6, -4, 2, 2, -6, -4, -4, -6, -4, 0, 8,
            -6, -8, -4, -8, -6, 2, -4, -4, -6, -6, -4, -2, -8, -8, -4, -6, -6, -4, -6, 22,
            0, -2, -4, -2, -2, -2, -2, -2, -2, -2, -2, -2, -2, -4, -2, -2, 0, 0, -2, -4, -2,
            -4, -6, -4, -6, -4, 6, -6, 4, -2, -4, -2, -2, -4, -6, -2, -4, -4, -4, -2, 4, -2, 14,
            -2, 2, -6, 2, 8, -6, -4, 0, -6, 2, -6, -2, 0, -2, 6, 0, 0, -2, -4, -6, -2, -4, 8};

    private AntPath antPath;
    private AntMap antMap = new AntMap();
    private int alignLength;
    private SequenceList sourceSequenceList;
    private AlignedList alignedList = new AlignedList();
    private AlignedList bestList;
    private int pathIndex = 0;
    private Random random = new Random();

    public Ant(SequenceList sourceSequenceList) throws Exception {
        this.sourceSequenceList = sourceSequenceList;
        this.alignLength = sourceSequenceList.maxSequenceLength();
        this.antPath = new AntPath(sourceSequenceList.missingGapsToLength(alignLength));
        this.bestList = alignedList;
    }

    public AntPath getAntPath() {
        return antPath;
    }

    public AntMap getAntMap() {
        return antMap;
    }

    public AlignedList getAlignedList() {
        return alignedList;
    }

    public AlignedList getBestList() {
        return bestList;
    }

    private void constructAlignment() throws Exception {

        pathIndex = 0;
        char[] alignment;

        alignedList.clear();

        for (int i = 0; i < sourceSequenceList.getSequenceCount(); i++) {
            Sequence sequence = new Sequence();
            alignment = alignSequence(i);
            sequence.setData(alignment);
            alignedList.addSequence(sequence);
        }

    }

    private char[] alignSequence(int i) throws Exception {
        char[] data;
        int datalength;
        char[] alignment = new char[alignLength];
        int gaps, ai;
        int idx;

        data = sourceSequenceList.getSequence(i).getData();
        datalength = sourceSequenceList.getSequence(i).getDataLength();
        gaps = alignLength - datalength;
        ai = 0;

        idx = pathIndex;
        for (int j = 0; j < datalength; j++) {
            for (int gapIndex = 0; gapIndex < gaps; gapIndex++) {
                if (antPath.getValueAt(idx) == j) {
                    alignment[ai] = '-';
                    ai++;
                }
            }

            alignment[ai] = data[j];
            ai++;
            idx++;
        }

        idx = pathIndex;
        for (int gapIndex = 0; gapIndex < gaps; gapIndex++) {
            if (antPath.getValueAt(idx) >= datalength) {
                alignment[ai] = '-';
                ai++;
            }
        }

        pathIndex += gaps;
        return alignment;
    }

    private int pickMove(int step) throws Exception {
        int numPaths = antMap.getMapSize();
        int[] stepWeight = new int[alignLength];
        int totalWeight = 0;
        int pathWeight, stepIndex;

        for (int i = 0; i < alignLength; i++) {
            totalWeight += stepWeight[i];
        }

        for (int i = 0; i < numPaths; i++) {
            stepIndex = antMap.getAntPath(i).getValueAt(step);
            pathWeight = antMap.getAntPath(i).getWeight();
            stepWeight[stepIndex] += pathWeight;
            totalWeight += pathWeight;
        }

        int rand = random.nextInt() % (totalWeight + 1);
        int stopSum = 0;
        for (int i = 0; i < alignLength; i++) {
            stopSum += stepWeight[i];
            if (stopSum >= rand)
                return i;
        }
        return 0;
    }

    private Symbol evaluateSymbol(char a) {
        if (a == '-')
            return new Symbol(-5, 0);

        return new Symbol(0, AMINO_ACID_ORDER.indexOf(a));
    }

    private int columnScore(int columnIndex) throws Exception {
        int score = 0;
        int seqCount = alignedList.getSequenceCount();
        int maxPos, minPos;
        int matrixIndex = 0;
        Symbol symbolA, symbolB;

        for (int i = 0; i < seqCount - 1; i++) {
            symbolA = evaluateSymbol(alignedList.getSequence(i).getData()[columnIndex]);
            score += symbolA.getScore();
            if (symbolA.getScore() < 0)
                continue;

            for (int j = 0; j < seqCount; j++) {
                symbolB = evaluateSymbol(alignedList.getSequence(j).getData()[columnIndex]);
                score += symbolB.getScore();
                if (symbolB.getScore() == 0)
                    continue;

                maxPos = Math.max(symbolA.getPosition(), symbolB.getPosition());
                minPos = Math.min(symbolA.getPosition(), symbolB.getPosition());
                matrixIndex = (int) (((maxPos + 1) / 2.0) * (maxPos)) + minPos;
            }

            score += blosum62mt2[matrixIndex];
        }

        return score;
    }

    private int evaluateAlignment() throws Exception {
        int score = 0;
        int colScore;
        for (int i = 0; i < alignLength; i++) {
            colScore = columnScore(i);
            score += colScore;
        }

        alignedList.setScore(score);
        return score;
    }

    public void go() throws Exception {
        int seqDiff;
        int sourceLenght;
        int step = 0;
        int move;
        for (int i = 0; i < sourceSequenceList.getSequenceCount(); i++) {
            sourceLenght = sourceSequenceList.getSequence(i).getDataLength();
            seqDiff = alignLength - sourceLenght;
            for (int j = 0; j < seqDiff; j++, step++) {
                move = pickMove(step);
                antPath.setValueAt(step, move);
            }
        }
        constructAlignment();
        evaluateAlignment();
        if (alignedList.getScore() > bestList.getScore()) {
            bestList = alignedList;
            antPath.setWeight(10);
            antMap.addPAth(antPath);
        }
    }

}
