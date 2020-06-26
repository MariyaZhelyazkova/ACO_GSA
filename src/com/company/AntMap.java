package com.company;

import java.util.List;
import java.util.Vector;

public class AntMap {
    List<AntPath> map = new Vector<>(100);

    public void addPath(AntPath antPath) {
        map.add(new AntPath(antPath));
    }

    public AntPath getPath(int i) {
        return map.get(i);
    }

    public int getScoreOfPath(int i) {
        return map.get(i).getWeight();
    }

    public int getSize(){
        return map.size();
    }
}
