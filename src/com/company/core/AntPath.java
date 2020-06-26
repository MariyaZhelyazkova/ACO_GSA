package com.company.core;

import java.util.Vector;

public class AntPath {
    private final Vector<Integer> path;
    private int weight = 1;

    public AntPath(int pathLength) {
        path = new Vector<>(pathLength);
        path.setSize(pathLength);
    }

    public AntPath(AntPath other) {
        path = new Vector<>(other.path);
        weight = other.weight;
    }

    public int getLength() {
        return path.size();
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;

        if (weight <= 0)
            this.weight = 1;
    }

    public int getValueAt(int pos) {
        return path.get(pos);
    }

    public void setValueAt(int pos, int value) {
        path.set(pos, value);
    }
}
