package me.langner.jonas.sodoku;

import java.util.ArrayList;
import java.util.List;

public class Sodoku {

    private UpdateListener<Field> listener;
    private UpdateAbleTreeSet<Field> treeSet = new UpdateAbleTreeSet<>();

    public Sodoku(int[][] fields) throws IllegalArgumentException {

        List<Field> fieldList = new ArrayList<>();

        if (fields.length > 0 && fields[0].length != fields.length) {

            for (int l = 0; l < fields.length; l++) {
                int[] line = fields[l];

                for (int c = 0; c < fields.length; c++) {
                    int current = (line.length > c) ? line[c] : -1;

                    // neues Feld erstellen -> wenn current > 0 und kleiner als max. ist, dann ist es fixed
                    if (current > 0 && current < fields.length) {
                        fieldList.add(new Field(fields.length, current));
                    }
                    else fieldList.add(new Field(fields.length));
                }
            }
        }
        else throw new IllegalArgumentException("Fields has to be a mxm matrix.");

        treeSet.addAll(fieldList);

        listener = field -> {
            if (field.getPossible().size() <= 1 && !field.isFixed())
                System.out.println("START BEI " + field.getID());
        };

    }

}
