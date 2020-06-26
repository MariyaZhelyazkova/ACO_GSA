package com.company;

import com.company.core.*;
import com.company.io.FastaReader;
import com.company.io.exceptions.FastaReaderException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;

public class Main {
    private static final int NUMBER_OF_ITERATIONS = 6000;


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

        var antMap = new AntMap();
        var bestList = new AlignedList();

        var threadCount = Runtime.getRuntime().availableProcessors() * 2;

        var latch = new CountDownLatch(threadCount);
        var executor = Executors.newFixedThreadPool(threadCount);

        Object exitedAfterBreakLockObject = new Object();
        AtomicBoolean exitedAfterBreak = new AtomicBoolean(false);

        AtomicReference<Exception> thrownException = new AtomicReference<>();

        IntStream.range(0, threadCount).forEach(thread -> executor.submit(() -> {
            try {
                Ant ant = new Ant(sequenceList, antMap, bestList);

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

        if (thrownException.get() != null) {
            thrownException.get().printStackTrace();
            return;
        }

        System.out.println("Exited because of break: " + exitedAfterBreak.get());
        System.out.println("Best Score: " + bestList.getScore());
        System.out.println("Best: \n" + bestList);
    }
}
