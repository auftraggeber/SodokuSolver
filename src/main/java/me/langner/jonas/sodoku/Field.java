package me.langner.jonas.sodoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Field extends UpdateAble implements Comparable<Field> {

    private static int lastID = -1;

    private List<Integer> possibilities;
    private boolean fixed = false;
    private int value;
    private int id = ++lastID;

    public Field(int size) {
        possibilities = new ArrayList<>();

        for (int i = 1; i <= size; i++) {
            possibilities.add(i);
        }
    }

    public Field(int size, int fixedInt) {
        this(size);
        this.fixed = true;
        value = fixedInt;
    }

    public void removePossibility(int ... ints) {

        beforeUpdate();

        for (int i : ints) {
            possibilities.remove(i);
        }

        updated();
    }

    public int getID() {
        return id;
    }

    public List<Integer> getPossible() {
        return Collections.unmodifiableList(possibilities);
    }

    public boolean isFixed() {
        return fixed;
    }

    public int getValue() {
        return value;
    }

    public int compareTo(Field o) {

        if (possibilities.size() < o.possibilities.size())
            return -1;
        else if (possibilities.size() > o.possibilities.size())
            return 1;
        else if (getID() < o.getID())
            return -1;
        else if (getID() > o.getID())
            return 1;

        return 0;
    }
}
