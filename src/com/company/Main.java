package com.company;

public class Main {

    public static void main(String[] args) {
        SequenceList sequenceList = new SequenceList();

        sequenceList.addSequence(new Sequence("GKGDPKKPRGKMDDYAFFVQTSREEHKKKHPDSSVNSEFSKKCSERWKTMSAKEKGKFEDMAKADKARYEREMKTYIPPKGE"));
        sequenceList.addSequence(new Sequence("MQDRVKRPMNAFIVWSRDRRKMALENNPRMRNSEISKQLGYQWKMLTEAEKWPFFQEAQKLQMHREKYPNYKYRPRRKAKMLPK"));
        sequenceList.addSequence(new Sequence("MKKLKKHPDDEPKKPLTPYFRFFMEKRAKYAKLHPEMSNLDLTKILSKKYKELPEKKKMKYIQDFQREKQEFERNLARFREDHPDLIQNAKK"));

        AntMap antMap = new AntMap();

        Ant ant = new Ant(sequenceList, antMap);

        for (int i = 0; i < 10000; i++)
            ant.go();

        AlignedList alignedList = ant.getBestList();

        System.out.println("Best Score: " + alignedList.getScore());
        System.out.println("Best: \n" + alignedList);
    }
}
