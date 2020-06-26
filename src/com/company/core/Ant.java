package com.company.core;

import com.company.utils.Proxy;

import java.util.Arrays;
import java.util.Random;

public class Ant {
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

        String data = sourceSequenceList.getSequence(i).getSequence();
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

    private int evaluateSymbol(char a, char b) {
        if (a == '-' || b == '-') {
            return -5;
        }

        if (a == b) {
            return 10;
        }

        return -2;
    }

    private int columnScore(int columnIndex) {
        int score = 0;
        int seqCount = alignedList.getCount();

        for (int i = 0; i < seqCount - 1; i++) {
            for (int j = i + 1; j < seqCount; j++) {
                score += evaluateSymbol(alignedList.getSequence(i).getSequence().charAt(columnIndex),
                        alignedList.getSequence(j).getSequence().charAt(columnIndex));
            }
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
