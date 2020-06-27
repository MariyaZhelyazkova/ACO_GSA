package com.company.io;

import com.company.core.Sequence;
import com.company.io.exceptions.FastaReaderException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FastaReader {
    public Sequence read(String fileName) throws FastaReaderException {
        var data = new Object() {
            String header;
            String sequence;
        };

        try (var stream = Files.lines(Paths.get(fileName))) {
            stream.forEach(line -> {
                if (data.header == null) {
                    data.header = line;
                    data.sequence = "";
                } else {
                    data.sequence += line.toUpperCase().trim();
                }
            });
        } catch (IOException e) {
            throw new FastaReaderException("Unable to read fasta file.", e);
        }

        if (data.header.isEmpty() || data.sequence.isEmpty())
            throw new FastaReaderException("Unable to read fasta file. The file may be empty.");

        return new Sequence(data.header, data.sequence);
    }
}
