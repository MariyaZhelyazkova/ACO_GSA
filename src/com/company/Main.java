package com.company;

import com.company.core.AlignedList;
import com.company.core.Ant;
import com.company.core.AntMap;
import com.company.core.SequenceList;
import com.company.io.FastaReader;
import com.company.io.exceptions.FastaReaderException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

public class Main {
    private static final int NUMBER_OF_ITERATIONS = 1000;


    public static void main(String[] args) {
        var sequenceList = new SequenceList();

        var reader = new FastaReader();

        try {
            sequenceList.addSequence(reader.read("1.txt"));
            sequenceList.addSequence(reader.read("2.txt"));
            sequenceList.addSequence(reader.read("3.txt"));
            sequenceList.addSequence(reader.read("4.txt"));
        } catch (FastaReaderException e) {
            e.printStackTrace();
            return;
        }

        var beginTime = System.currentTimeMillis();

        var antMap = new AntMap();
        var bestList = new AlignedList();

        var threadCount = Runtime.getRuntime().availableProcessors() * 2;

        var latch = new CountDownLatch(threadCount);
        var executor = Executors.newFixedThreadPool(threadCount);

        var exitedAfterBreakLockObject = new Object();
        var exitedAfterBreak = new AtomicBoolean(false);

        var thrownException = new AtomicReference<Exception>();

        IntStream.range(0, threadCount).forEach(thread -> executor.submit(() -> {
            try {
                var ant = new Ant(sequenceList, antMap, bestList);

                if (ant.go(NUMBER_OF_ITERATIONS)) {
                    synchronized (exitedAfterBreakLockObject) {
                        exitedAfterBreak.set(true);
                    }
                }
            } catch (Exception e) {
                thrownException.set(e);
            } finally {
                latch.countDown();
            }
        }));

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }

        var endTime = System.currentTimeMillis();

        if (thrownException.get() != null) {
            thrownException.get().printStackTrace();
            return;
        }

        System.out.println("Execution time: " + (endTime - beginTime) * 0.001 + " seconds");
        System.out.println("Exited because of break: " + exitedAfterBreak.get());
        System.out.println("Best Score: " + bestList.getScore());
        System.out.println("Best: \n" + bestList);
    }
}
