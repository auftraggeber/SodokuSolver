package me.langner.jonas.sudoku;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Ein Lösungsfeld eines Sudokus.
 * @author Jonas Langner
 * @version 1.0
 * @since 1.0
 */
public class Field extends UpdateAble implements Comparable<Field> {

    private static int lastID = -1;

    private List<Integer> possibilities, notPossible;
    private boolean fixed = false;
    private int value, posX, posY;
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

    public void setPosition(int x, int y) {
        posX = x;
        posY = y;
    }

    /**
     * Entfernt Einsatzmöglichkeiten und aktualisiert den Baum.
     * @param bcFixed Gibt an, ob der aufrufende die Methode gewählt hat, weil die Werte schon fest vergeben sind.
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

    /**
     * Informiert Nachbarn über aktuellen Wert.
     * @param start Gibt an, ob diese Methode von der Initialisierung eines Sudokus aufgerufen wird.
     */
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

    /**
     * Setzt einen festen Wert. Legt sich auf einen Wert fest.
     * @param value Der Wert.
     */
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

    /**
     * Setzt den Wert wieder zurück.
     * @param resetTries Gibt an, ob es nur den Wert zurücksetzen soll oder auch den nächsten Wert, den es probiert.
     */
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

    /**
     * Entscheidung, ob das Feld vor einem anderen abgearbeitet werden soll.
     * @param o Das andere Feld.
     * @return Die Priorität.
     */
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

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }
}
