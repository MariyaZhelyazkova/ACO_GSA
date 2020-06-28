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

        var pathIndex = new Proxy<>(0);

        for (var i = 0; i < sourceSequenceList.getCount(); i++) {
            alignedList.addSequence(alignSequence(i, pathIndex));
        }
    }

    private Sequence alignSequence(int i, Proxy<Integer> pathIndex) {
        var alignment = new char[alignLength];
        Arrays.fill(alignment, ' ');

        var data = sourceSequenceList.getSequence(i).getSequence();
        var dataLength = data.length();

        var gaps = alignLength - dataLength;

        var alignmentIndexObject = new Object() {
            int alignmentIndex = 0;
        };

//        var pathIndexValue = pathIndex.getValue();

        for (var j = 0; j < dataLength; j++) {
            int idx = pathIndex.getValue();

            for (int gapIndex = 0; gapIndex < gaps; gapIndex++, idx++) {
                if (antPath.getValueAt(idx) == j) {
                    alignment[alignmentIndexObject.alignmentIndex] = '-';
                    alignmentIndexObject.alignmentIndex++;
                }
            }

//            antPath.notifyForValuesInRange(pathIndexValue, pathIndexValue + gaps - 1, j, () -> {
//                alignment[alignmentIndexObject.alignmentIndex] = '-';
//                alignmentIndexObject.alignmentIndex++;
//            });

            alignment[alignmentIndexObject.alignmentIndex] = data.charAt(j);
            alignmentIndexObject.alignmentIndex++;
        }

        var idx = pathIndex.getValue();

        for (var gapIndex = 0; gapIndex < gaps; gapIndex++, idx++) {
            if (antPath.getValueAt(idx) >= dataLength) {
                alignment[alignmentIndexObject.alignmentIndex] = '-';
                alignmentIndexObject.alignmentIndex++;
            }
        }

        pathIndex.setValue(pathIndex.getValue() + gaps);

        return new Sequence(String.valueOf(alignment));
    }

    private int pickMove(int step) {
        var totalWeight = alignLength;

        var stepWeight = new int[alignLength];
        Arrays.fill(stepWeight, 1);

        for (var i = 0; i < antMap.getSize(); i++) {
            final var antPath = antMap.getPath(i);

            var stepIndex = antPath.getValueAt(step);
            var pathWeight = antPath.getWeight();

            stepWeight[stepIndex] += pathWeight;
            totalWeight += pathWeight;
        }

        var random = new Random();
        var rand = random.nextInt(totalWeight);

        var stopSum = 0;

        for (var i = 0; i < alignLength; i++) {
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
        var score = 0;
        var seqCount = alignedList.getCount();

        for (var i = 0; i < seqCount - 1; i++) {
            for (var j = i + 1; j < seqCount; j++) {
                score += evaluateSymbol(alignedList.getSequence(i).getSequence().charAt(columnIndex),
                        alignedList.getSequence(j).getSequence().charAt(columnIndex));
            }
        }

        return score;
    }

    private int evaluateAlignmentScore() {
        var score = 0;

        for (var i = 0; i < alignLength; i++) {
            score += columnScore(i);
        }

        return score;
    }

    public void go(int iterations) {
        for (var iteration = 0; iteration < iterations; iteration++) {
            var step = 0;

            for (var i = 0; i < sourceSequenceList.getCount(); i++) {
                var sourceLength = sourceSequenceList.getSequence(i).getLength();
                var seqDiff = alignLength - sourceLength;

                for (var j = 0; j < seqDiff; j++, step++) {
                    var move = pickMove(step);
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
}
