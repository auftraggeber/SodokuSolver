package me.langner.jonas.sudoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Field extends UpdateAble implements Comparable<Field> {

    private static int lastID = -1;

    private List<Integer> possibilities, notPossible;
    private boolean fixed = false;
    private int value;
    private int id = ++lastID;
    private Group[] groups;
    private int lastTriedIndex = -1;

    public Field(int size) {
        possibilities = new ArrayList<>();
        notPossible = new ArrayList<>();

        for (int i = 1; i <= size; i++) {
            possibilities.add(i);
        }
    }

    public Field(int size, int fixedInt) {
        this(size);
        this.fixed = true;
        value = fixedInt;
    }

    /**
     * Entfernt Einsatzmöglichkeiten und aktualisiert den Baum.
     * @param ints Die zu entfernenden Einsatzmöglichkeiten.
     */
    public void removePossibility(boolean bcFixed, int ... ints) {
        if (fixed)
            return;

        beforeUpdate();

        for (int i : ints)
            possibilities.remove(new Integer(i));

        if (bcFixed) {
            for (int i : ints)
                notPossible.add(i);
        }

        updated();
    }

    public void addPossibility(int ... ints) {
        if (fixed)
            return;

        beforeUpdate();

        for (int i : ints) {
            if (!possibilities.contains(i) && !notPossible.contains(i)) {
                boolean canAdd = true;

                for (Group g : groups) {
                    if (g.getValues().contains(i))
                        canAdd = false;
                }

                if (canAdd)
                    possibilities.add(i);
            }

        }

        updated();
    }

    protected void updateOthers(boolean start) {
        if (groups == null)
            return;

        //System.out.println("UP: \t" + getID());


        /* Gruppen erinnern */

        // testen, ob festgelegt
        if (fixed) {
            for (Group g : groups) {
                g.getFields().forEach(field -> {

                    field.removePossibility(start && fixed,value);
                });
            }
        }
        else if (value > 0){
            for (Group g : groups) {
                g.getFields().forEach(field -> {

                    field.addPossibility(value);
                });
            }
        }

    }

    public void setGroups(Group[] groups) {
        this.groups = groups;

        for (Group group : groups) {
            group.addField(this);
        }
    }

    public void setFixedValue(int value) {
        if (fixed)
            return;

        removeFromTree();
        /*System.out.println("removed: " + getID());
        System.out.println("still existing?: " + getSet().contains(this));*/

        this.value = value;
        this.fixed = true;

        for (Group g : groups)
            g.addValue(value);

        updateOthers(false);
    }

    public void setValue(int value) {}

    public void resetValue(boolean resetTries) {

        removeFromTree();
        updated();

        this.fixed = false;

        for (Group g : groups)
            g.removeValue(value);

        updateOthers(false);

        if (resetTries)
            this.lastTriedIndex = -1;
        this.value = -1;
    }

    public int incrementLastTriedIndex() {
        return ++lastTriedIndex;
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

    public int getLastTriedIndex() {
        return lastTriedIndex;
    }

    public int compareTo(Field o) {

       /* System.out.print(getID() + " - " + o.getID() + ": ");
        System.out.println(possibilities.size() + " --- " + o.possibilities.size()); */

        if (getID() == o.getID())
            return 0;

        if (possibilities.size() < o.possibilities.size()) {
            return -1;
        }

        else if (possibilities.size() > o.possibilities.size()) {
            return 1;
        }

        else if (getID() < o.getID()) {
            return -1;
        }

        else if (getID() > o.getID()) {
            return 1;
        }


        return 0;
    }
}
