package com.company;

import com.company.core.AlignedList;
import com.company.core.Ant;
import com.company.core.AntMap;
import com.company.core.SequenceList;
import com.company.io.FastaReader;
import com.company.io.console.ConsoleOption;
import com.company.io.console.ConsoleParser;
import com.company.io.console.ParsedConsoleData;
import com.company.io.exceptions.FastaReaderException;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class Main {
    private static ParsedConsoleData parseInput() {
        var options = new Vector<ConsoleOption>(5);

        options.add(new ConsoleOption("input.filesCount", "Please enter the number of input files [Required]: ", Integer.class));
        options.add(new ConsoleOption("input.directory", "Please enter the directory where the input files are located [Optional]:", String.class, ""));
        options.add(new ConsoleOption("output.fileName", "Please enter the output file name [Required]: ", String.class));
        options.add(new ConsoleOption("output.directory", "Please enter the output directory where the output file should be located [Optional]: ", String.class, ""));
        options.add(new ConsoleOption("ant.count", "Please enter the number of ants [Required]: ", Integer.class));
        options.add(new ConsoleOption("ant.iterationsCount", "Please enter the number of iteration per ant [Required]: ", Integer.class));

        var parser = new ConsoleParser(options);

        return parser.parse();
    }

    private static SequenceList readSequences(int inputFilesCount, String inputDirectory) throws FastaReaderException {
        var inputFilePath = inputDirectory;

        if (!inputFilePath.isEmpty() && !inputDirectory.endsWith("\\")) {
            inputFilePath += "\\";
        }

        var sequenceList = new SequenceList();

        var reader = new FastaReader();

        for (int i = 1; i <= inputFilesCount; i++) {
            sequenceList.addSequence(reader.read(inputFilePath + i + ".txt"));
        }

        return sequenceList;
    }

    public static void main(String[] args) {
        try {
            var input = parseInput();
            var sequenceList = readSequences(input.get("input.filesCount", Integer.class), input.get("input.directory", String.class));

            var beginTime = System.currentTimeMillis();

            var antMap = new AntMap();
            var bestList = new AlignedList();

            var antsCount = input.get("ant.count", Integer.class);
            var threadCount = Runtime.getRuntime().availableProcessors() * 2;

            var latch = new CountDownLatch(antsCount);
            var executor = Executors.newFixedThreadPool(threadCount);

            IntStream.range(0, antsCount).forEach(thread -> executor.submit(() -> {
                try {
                    var ant = new Ant(sequenceList, antMap, bestList);
                    ant.go(input.get("ant.iterationsCount", Integer.class));
                } catch (Exception e) {
                    e.printStackTrace();
                    System.exit(1);
                } finally {
                    latch.countDown();
                }
            }));

            latch.await();

            var endTime = System.currentTimeMillis();
            var elapsedSeconds = (endTime - beginTime) * 0.001;

            System.out.println("\nExecution time: " + elapsedSeconds + " seconds");
            System.out.println("Best Score: " + bestList.getScore());
            System.out.println("Best: \n" + bestList);

            var outputFile = input.get("output.fileName", String.class);
            var outputDirectory = input.get("output.directory", String.class);

            createOutput(outputFile, outputDirectory, elapsedSeconds, bestList);
        } catch (Exception e) {
            System.out.println("An error occurred. Message: " + e.getMessage());
            System.exit(1);
        }
    }

    private static void createOutput(String outputFile, String outputDirectory, double elapsedSeconds, AlignedList bestList) throws IOException {
        try (FileWriter writer = new FileWriter(new File(outputDirectory, outputFile));
             BufferedWriter bw = new BufferedWriter(writer)) {
            bw.write("Execution time: " + elapsedSeconds + " seconds\n");
            bw.write("Best Score: " + bestList.getScore() + "\n");
            bw.write("Best: \n" + bestList + "\n");
        }
    }
}
