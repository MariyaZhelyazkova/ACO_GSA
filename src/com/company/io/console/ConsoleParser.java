package com.company.io.console;

import com.company.utils.typeparser.StringToTypeParser;
import com.company.utils.typeparser.exceptions.TypeParseException;

import java.util.Scanner;
import java.util.Vector;

public class ConsoleParser {
    private final Vector<ConsoleOption> options;

    public ConsoleParser(Vector<ConsoleOption> options) {
        this.options = options;
    }

    public ParsedConsoleData parse() {
        var data = new ParsedConsoleData();
        var in = new Scanner(System.in);

        for (var option : options) {
            System.out.print(option.getText());

            data.add(option.getName(), readValue(in, option));
        }

        return data;
    }

    private String readValue(Scanner in, ConsoleOption option) {
        do {
            var line = in.nextLine().trim();

            if (line.isEmpty()) {
                if (!option.isRequired()) {
                    return option.getDefaultValue();
                }

                continue;
            }

            var parser = new StringToTypeParser();

            try {
                parser.parse(line, option.getType());
                return line;
            } catch (TypeParseException e) {
                System.out.println("The entered value must be of type: " + option.getType().toString());
                System.out.print(option.getText());
            }
        } while (true);
    }
}
