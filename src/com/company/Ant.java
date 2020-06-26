package com.company;

import java.util.Arrays;
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

    private final AntPath antPath;
    private final AntMap antMap;
    private final int alignLength;
    private final SequenceList sourceSequenceList;

    private final AlignedList alignedList = new AlignedList();

    private final AlignedList bestList = new AlignedList();

    public Ant(SequenceList sourceSequenceList, AntMap antMap) {
        this.sourceSequenceList = sourceSequenceList;
        this.alignLength = sourceSequenceList.maxSequenceLength();
        this.antPath = new AntPath(sourceSequenceList.missingGapsToLength(alignLength));
        this.antMap = antMap;
    }

    public AlignedList getBestList() {
        return bestList;
    }

    private void constructAlignment() {
        alignedList.clear();

        Proxy<Integer> pathIndex = new Proxy<>(0);

        for (int i = 0; i < sourceSequenceList.getCount(); i++) {
            alignedList.addSequence(alignSequence(i, pathIndex));
        }
    }

    private Sequence alignSequence(int i, Proxy<Integer> pathIndex) {
        StringBuilder alignment = new StringBuilder(alignLength);
        alignment.append(" ".repeat(alignLength));

        String data = sourceSequenceList.getSequence(i).getData();
        int dataLength = data.length();

        int gaps = alignLength - dataLength;
        int alignmentIndex = 0;


        for (int j = 0; j < dataLength; j++) {
            int idx = pathIndex.getValue();

            for (int gapIndex = 0; gapIndex < gaps; gapIndex++, idx++) {
                if (antPath.getValueAt(idx) == j) {
                    alignment.setCharAt(alignmentIndex, '-');
                    alignmentIndex++;
                }
            }

            alignment.setCharAt(alignmentIndex, data.charAt(j));
            alignmentIndex++;
        }

        int idx = pathIndex.getValue();

        for (int gapIndex = 0; gapIndex < gaps; gapIndex++, idx++) {
            if (antPath.getValueAt(idx) >= dataLength) {
                alignment.setCharAt(alignmentIndex, '-');
                alignmentIndex++;
            }
        }

        pathIndex.setValue(pathIndex.getValue() + gaps);

        return new Sequence(alignment.toString());
    }

    private int pickMove(int step) {
        int totalWeight = alignLength;

        int[] stepWeight = new int[alignLength];
        Arrays.fill(stepWeight, 1);

        for (int i = 0; i < antMap.getSize(); i++) {
            final AntPath antPath = antMap.getPath(i);

            int stepIndex = antPath.getValueAt(step);
            int pathWeight = antPath.getWeight();

            stepWeight[stepIndex] += pathWeight;
            totalWeight += pathWeight;
        }

        Random random = new Random();
        int rand = random.nextInt(totalWeight);

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

    private int columnScore(int columnIndex) {
        int score = 0;
        int seqCount = alignedList.getCount();

        for (int i = 0; i < seqCount - 1; i++) {
            Symbol symbolA =
                    evaluateSymbol(alignedList.getSequence(i).getData().charAt(columnIndex));

            score += symbolA.getScore();

            if (symbolA.getScore() < 0)
                continue;

            int matrixIndex = 0;

            for (int j = i; j < seqCount; j++) {
                Symbol symbolB =
                        evaluateSymbol(alignedList.getSequence(j).getData().charAt(columnIndex));

                score += symbolB.getScore();

                if (symbolB.getScore() < 0)
                    continue;

                int maxPos = Math.max(symbolA.getPosition(), symbolB.getPosition());
                int minPos = Math.min(symbolA.getPosition(), symbolB.getPosition());

                matrixIndex = (int) (((maxPos + 1) / 2.0) * (maxPos)) + minPos;
            }

            score += blosum62mt2[matrixIndex];
        }

        return score;
    }

    private int evaluateAlignmentScore() {
        int score = 0;

        for (int i = 0; i < alignLength; i++) {
            score += columnScore(i);
        }

        return score;
    }

    public void go() {
        int step = 0;

        for (int i = 0; i < sourceSequenceList.getCount(); i++) {
            int sourceLength = sourceSequenceList.getSequence(i).getLength();
            int seqDiff = alignLength - sourceLength;

            for (int j = 0; j < seqDiff; j++, step++) {
                int move = pickMove(step);
                antPath.setValueAt(step, move);
            }
        }

        constructAlignment();

        alignedList.setScore(evaluateAlignmentScore());

        if (alignedList.getScore() > bestList.getScore()) {
            bestList.assign(alignedList);
            antPath.setWeight(10);
            antMap.addPath(antPath);
        }
    }

}
