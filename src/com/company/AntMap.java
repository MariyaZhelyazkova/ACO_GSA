package com.company;

import java.util.List;
import java.util.Vector;

public class AntMap {
    List<AntPath> map = new Vector<>();

    public void addPAth(AntPath antPath) {
        map.add(antPath);
    }

    public AntPath getAntPath(int i) throws Exception {
        if (i < 0 || i >= map.size())
            throw new Exception("Out of bounds");

        return map.get(i);
    }

    public int getScoreOfPath(int i) throws Exception {
        if (i < 0 || i >= map.size())
            throw new Exception("Out of bounds");

        return map.get(i).getWeight();
    }

    public int getMapSize(){
        return map.size();
    }
}
