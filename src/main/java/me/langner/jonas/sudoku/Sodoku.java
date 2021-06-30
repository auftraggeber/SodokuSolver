package me.langner.jonas.sudoku;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class Sodoku {

    private int size;
    private UpdateListener<Field> listener;
    private UpdateAbleTreeSet<Field> treeSet = new UpdateAbleTreeSet<>();
    private Group[] lineGroups, columnGroups, boxGroups;
    private List<Field> fieldList = new LinkedList<>(); // zur möglichen Darstellung notwendig
    private Stack<Field> lastFields = new Stack<>(), lastMultipleFields = new Stack<>();

    /**
     * Erstellt ein neues Sodoku-Rätsel, welches gelöst werden muss.
     * @param size Die Größe des Rätsels (wenn 3x3, dann 3 angeben)
     * @param fields Die Inhalte der Felder (wenn kein Inhalt, dann -1).
     * @throws IllegalArgumentException Wirft Exception, wenn falsches Format angegeben wurde.
     */
    public Sodoku(int size, int[][] fields) throws IllegalArgumentException {

        if (fields.length > 0 && fields[0].length == fields.length && fields.length == size*size) {
            this.size = size;

            lineGroups = new Group[size*size];
            columnGroups = new Group[size*size];
            boxGroups = new Group[size*size];

            for (int l = 0; l < fields.length; l++) {
                int[] line = fields[l];

                for (int c = 0; c < fields.length; c++) {
                    int current = (line.length > c) ? line[c] : -1;

                    Field field = null;
                    // neues Feld erstellen -> wenn current > 0 und kleiner als max. ist, dann ist es fixed
                    if (current > 0 && current <= fields.length) {
                        field = new Field(fields.length, current);
                    }
                    else field = new Field(fields.length);

                    field.setGroups(getGroups(l,c));

                    fieldList.add(field);
                }
            }
        }
        else throw new IllegalArgumentException("Fields has to be a mxm matrix.");

        /* nacheinander hinzufügen */
        for (Field field : fieldList) {
            treeSet.add(field);
        }

        for (Field field : fieldList) {
            field.updateOthers(true);
        }

        listener = new UpdateListener<Field>() {
            @Override
            public void updated(Field element) {
                solve(element);
            }
        };

       // treeSet.addListener(listener);

        solve(treeSet.first());

    }

    public String possibleString(Field field) {
        String str = "";

        for (int i = 0; i < field.getPossible().size(); i++) {

            str += field.getPossible().get(i);

            if (i + 1 < field.getPossible().size())
                str += ", ";

        }

        return str;
    }


    public void show() {


        for (int i = 0; i < 100; i++) {
            if (i == 50 && !lastFields.isEmpty()) {
                String lastMult = (lastMultipleFields.isEmpty()) ? "" : " | " + lastMultipleFields.peek().getID() + " (" + possibleString(lastMultipleFields.peek()) + ") ";

                System.out.print("[ NEU: " + lastFields.peek().getID() + "(" + possibleString(lastFields.peek()) + ")" + lastMult + "]");
            }
            System.out.print("-");
        }

        System.out.println();

        int i = 0;

        for (Field f : fieldList) {
            String value = (f.getValue() > 0) ? f.getValue() + " " : "  ";

            if (!lastFields.isEmpty() && lastFields.peek().getID() == f.getID())
                System.out.print(">" + value + "<");
            else
            System.out.print(value + "  ");
            i++;

            if (i % size == 0) {
                System.out.print("\t");
            }

            if (i % (size * size) == 0)
                System.out.println();

            if (i % (size* size * size) == 0)
                System.out.println();
        }
    }

    public void solve(Field field) throws IllegalStateException {

        /*
         *
         *  3   _   _   _
         *  _   _   2   _
         *  _   1   _   _
         *  _   _   _   2
         *
         *
         * LÖSUNG DURCH ALGORITHMUS:
         *  3   2   4   1
         *  1   4   2   3
         *  2   1   3   4
         *  4   3   1   2
         *
         *
         *  0   1   2   3
         *  4   5   6   7
         *  8   9   10  11
         *  12  13  14  15
         *
         *
         *  _   _   _   _   _   _   _   _   _
         *  _   _   _   _   _   _   _   _   _
         *  _   _   _   _   _   _   _   _   _
         *  _   _   _   _   _   _   _   _   _
         *  _   _   _   _   _   _   _   _   _
         *  _   _   _   _   _   _   _   _   _
         *  _   _   _   _   _   _   _   _   _
         *  _   _   _   _   _   _   _   _   _
         *  _   _   _   _   _   _   _   _   _
         *
         *
         *  _   _   _   _   _   _   _   _   _
         *  _   _   _   _   _   _   _   _   _
         *  _   _   _   _   _   _   _   _   _
         *  _   _   _   _   _   _   _   _   _
         *  _   _   _   _   _   _   _   _   _
         *  _   _   _   _   _   _   _   _   _
         *  _   _   _   _   _   _   _   _   _
         *  _   _   _   _   _   _   _   _   _
         *  _   _   _   _   _   _   _   _   _
         *
         *
         *  0   1   2   3   4   5   6   7   8
         *  9   10  11  12  13  14  15  16  17
         *  18  19  20  21  22  23  24  25  26
         *  27  28  29  30  31  32  33  34  35
         *  36  37  38  39  40  41  42  43  44
         *  45  46  47  48  49  50  51  52  53
         *  54  55  56  57  58  59  60  61  62
         *  63  64  65  66  67  68  69  70  71
         *  72  73  74  75  76  77  78  79  80
         *
         *
         */

        //System.out.print("Möglichkeiten für Feld " + field.getID() + ": " + field.getPossible().size() + " \tLetzter Versuch: " + field.getLastTriedIndex() + "\n");

        show();

        if (field.getPossible().size() >= 1 && !field.isFixed()) {
            field.setFixedValue(field.getPossible().get(field.incrementLastTriedIndex()));
            //System.out.println("SETZE" + field.getID() + ": \t" + field.getValue());;

            lastFields.push(field);

            if (field.getPossible().size() >= 2)
                lastMultipleFields.push(field);

            if (!treeSet.isEmpty()) {
                try {
                    solve(treeSet.first());
                }
                catch (IllegalStateException ex) {
                    System.out.print("\n\nWeg nicht erfolgreich!\t");
                    Field last = lastFields.pop();
                    Field lastMultiple = lastMultipleFields.pop();

                    while (lastMultiple.getPossible().size() <= lastMultiple.getLastTriedIndex() + 1)
                        lastMultiple = lastMultipleFields.pop();

                    System.out.print("Ziel: " + lastMultiple.getID() + " (" + possibleString(lastMultiple) + ")" + "\tZurück:");

                    while (!last.equals(lastMultiple)) {

                        System.out.print(last.getID() + " -> ");

                        last.resetValue(true);
                        last = lastFields.pop();


                       // System.err.println("FALSCH: " + last.getID() + ": \t" + last.getValue());
                    }

                    System.out.println(last.getID());

                    last.resetValue(false);

                    solve(last);

                }
            }


        }
        else if (field.getPossible().size() <= 0)
            throw new IllegalStateException("Field #" + field.getID() + " has 0 possibilities.");
        // TODO: keine eindeutige Möglichkeit
    }

    /**
     * Ermittelt die Gruppen eines Feldes.
     * @param line Die Zeile des Feldes.
     * @param column Die Spalte des Feldes.
     * @return Alle Gruppen, zu denen das Feld gehört.
     */
    public Group[] getGroups(int line, int column) {
        Group[] groups = new Group[3];

        /* erstmal spalten und zeilengruppen setzen */
        if (lineGroups[line] == null) {
            lineGroups[line] = new Group();
        }
        groups[0] = lineGroups[line];

        if (columnGroups[column] == null) {
            columnGroups[column] = new Group();
        }
        groups[1] = columnGroups[column];

        /* ermitteln, welche Box
        *
        *
        * Anordnung:
        *
        *   0   1   2
        *
        *   3   4   5
        *
        *   6   7   8
        * */

        int boxLine = (line / size);
        int boxColumn = (column / size);

        int boxIndex = boxLine * size + boxColumn;

        if (boxGroups[boxIndex] == null) {
            boxGroups[boxIndex] = new Group();
        }

        groups[2] = boxGroups[boxIndex];

        return groups;
    }

}
