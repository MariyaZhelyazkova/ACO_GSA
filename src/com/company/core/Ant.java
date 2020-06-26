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

    private final AlignedList bestList;

    public Ant(SequenceList sourceSequenceList, AntMap antMap, AlignedList bestList) {
        this.sourceSequenceList = sourceSequenceList;
        this.alignLength = sourceSequenceList.maxSequenceLength();
        this.antPath = new AntPath(sourceSequenceList.missingGapsToLength(alignLength));
        this.antMap = antMap;
        this.bestList = bestList;
    }

    private void constructAlignment() {
        alignedList.clear();

        Proxy<Integer> pathIndex = new Proxy<>(0);

        for (int i = 0; i < sourceSequenceList.getCount(); i++) {
            alignedList.addSequence(alignSequence(i, pathIndex));
        }
    }

    private Sequence alignSequence(int i, Proxy<Integer> pathIndex) {
        var alignment = new char[alignLength];
//        StringBuilder alignment = new StringBuilder(alignLength);
//        alignment.append(" ".repeat(alignLength));

        String data = sourceSequenceList.getSequence(i).getSequence();
        int dataLength = data.length();

        int gaps = alignLength - dataLength;

        var alignmentIndexObject = new Object() {
            int alignmentIndex = 0;
        };

        var pathIndexValue = pathIndex.getValue();

        for (int j = 0; j < dataLength; j++) {
            antPath.findValueInRange(pathIndexValue, pathIndexValue + gaps - 1, j, () -> {
                alignment[alignmentIndexObject.alignmentIndex] = '-';
//                alignment.setCharAt(alignmentIndexObject.alignmentIndex, '-');
                alignmentIndexObject.alignmentIndex++;
            });

            alignment[alignmentIndexObject.alignmentIndex] = data.charAt(j);
//            alignment.setCharAt(alignmentIndexObject.alignmentIndex, data.charAt(j));
            alignmentIndexObject.alignmentIndex++;
        }

        var idx = pathIndexValue;

        for (int gapIndex = 0; gapIndex < gaps; gapIndex++, idx++) {
            if (antPath.getValueAt(idx) >= dataLength) {
                alignment[alignmentIndexObject.alignmentIndex] = '-';
//                alignment.setCharAt(alignmentIndexObject.alignmentIndex, '-');
                alignmentIndexObject.alignmentIndex++;
            }
        }

        pathIndex.setValue(pathIndex.getValue() + gaps);
        return new Sequence(String.valueOf(alignment));
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

    public boolean go(int iterations) {
        int uselessIterations = 0;

        for (int iteration = 0; iteration < iterations; iteration++) {
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

            if (Math.abs(alignedList.getScore() - bestList.getScore()) < 100) {
                uselessIterations++;

                if (uselessIterations > 20)
                    return true;
            } else {
                uselessIterations = 0;
            }

            if (alignedList.getScore() > bestList.getScore()) {
                bestList.assign(alignedList);
                antPath.setWeight(10);
                antMap.addPath(antPath);
            }
        }

        return false;
    }
}
