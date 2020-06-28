package com.company.io.console;

public class ConsoleOption {
    private final String name;
    private final String text;
    private final Class<?> type;

    private final boolean isRequired;
    private final String defaultValue;

    public ConsoleOption(String name, String text, Class<?> type) {
        this.name = name;
        this.text = text;
        this.type = type;

        this.isRequired = true;
        this.defaultValue = "";
    }

    public ConsoleOption(String name, String text, Class<?> type, String defaultValue) {
        this.name = name;
        this.text = text;
        this.type = type;

        this.isRequired = false;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        return text;
    }

    public Class<?> getType() {
        return type;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}
