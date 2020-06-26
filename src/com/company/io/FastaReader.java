package com.company.io;

import com.company.core.Sequence;
import com.company.io.exceptions.FastaReaderException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class FastaReader {
    public Sequence read(String fileName) throws FastaReaderException {
        var data = new Object() {
            String header;
            String sequence;
        };

        try (Stream<String> stream = Files.lines(Paths.get(fileName))) {
            var firstLine = stream.findFirst();

            if (firstLine.isEmpty())
                throw new FastaReaderException("Unable to read fasta file. The file is empty.");

            data.header = firstLine.get();

            stream.forEach(line -> data.sequence += line.toUpperCase().trim());

        } catch (IOException e) {
            throw new FastaReaderException("Unable to read fasta file.", e);
        }

        return new Sequence(data.header, data.sequence);
    }
}
