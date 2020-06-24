package com.company;

public class AntPath {
    private int[] path;
    private int pathLength;
    private int weight;

    public AntPath(int pathLength) {
        this.pathLength = pathLength;
        path = new int[pathLength];
        weight = 1;
    }

    public int getPathLength() {
        return pathLength;
    }

    public void setPathLength(int pathLength) {
        path = new int[pathLength];
        this.pathLength = pathLength;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        if (weight > 0)
            this.weight = weight;
        else
            this.weight = 1;
    }

    public int getValueAt(int pos) throws Exception {
        if (pos < 0 || pos > pathLength)
            throw new Exception("Out of bounds");

        return path[pos];
    }

    public void setValueAt(int pos, int value) {

    }
}
