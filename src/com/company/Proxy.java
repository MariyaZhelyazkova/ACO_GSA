package com.company;

public class Proxy<T> {
    private T value;

    public Proxy() {
    }

    public Proxy(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
