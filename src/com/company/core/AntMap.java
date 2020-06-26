package com.company.core;

import java.util.List;
import java.util.Vector;

public class AntMap {
    List<AntPath> map = new Vector<>(100);

    public synchronized void addPath(AntPath antPath) {
        map.add(new AntPath(antPath));
    }

    public synchronized AntPath getPath(int i) {
        return map.get(i);
    }

    public synchronized int getScoreOfPath(int i) {
        return map.get(i).getWeight();
    }

    public synchronized int getSize(){
        return map.size();
    }
}
