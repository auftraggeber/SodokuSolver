package me.langner.jonas.sudoku;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Behandelt die Lösung von Sudokus.
 * @author Jonas Langner
 * @version 1.0
 * @since 1.0
 */
public class Sudoku {

    private int size;
    private UpdateListener<Field> listener;
    private UpdateAbleTreeSet<Field> treeSet = new UpdateAbleTreeSet<>();
    private Group[] lineGroups, columnGroups, boxGroups;
    private List<Field> fieldList = new LinkedList<>(); // zur möglichen Darstellung notwendig
    private Stack<Field> lastFields = new Stack<>(), lastMultipleFields = new Stack<>();
    private int tries = 0;
    private long startTime, endTime;

    /**
     * Erstellt ein neues Sodoku-Rätsel, welches gelöst werden muss.
     * @param size Die Größe des Rätsels (wenn 3x3, dann 3 angeben)
     * @param fields Die Inhalte der Felder (wenn kein Inhalt, dann -1).
     * @throws IllegalArgumentException Wirft Exception, wenn falsches Format angegeben wurde.
     */
    public Sudoku(int size, int[][] fields) throws IllegalArgumentException {

        if (fields.length > 0 && fields[0].length == fields.length && fields.length == size*size) {
            this.size = size;

            initGroups();
            parseFields(fields);

        }
        else throw new IllegalArgumentException("Fields has to be a mxm matrix.");

        initAllPossibilities();
        addAllToTree();

        startSolving();
    }

    /**
     * Erstellt die Gruppen, zu denen bestimmte Felder gehören.
     */
    private synchronized void initGroups() {
        lineGroups = new Group[size*size];
        columnGroups = new Group[size*size];
        boxGroups = new Group[size*size];
    }

    /**
     * Wandelt das Feldarray (ints) in Objekte um.
     * @param fields Das Array der Werte der Felder.
     */
    private synchronized void parseFields(int[][] fields) {
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
                field.setPosition(c,l);

                fieldList.add(field);
            }
        }
    }

    /**
     * Fügt alle Felder in den Baum ein.
     */
    private synchronized void addAllToTree() {
        for (Field field : fieldList) {
            treeSet.add(field);
        }
    }

    /**
     * Setzt für jedes Feld die Möglichkeiten, die es besitz auf den aktuellen Stand.
     */
    private synchronized void initAllPossibilities() {
        for (Field field : fieldList) {
            field.updateOthers(true);
        }
    }

    /**
     * Startet den Lösungsalgorithmus.
     */
    private synchronized void startSolving() {
        Field current = treeSet.first();

        startTime = System.currentTimeMillis();

        while (current != null) {
            current = saveSolve(current);
        }

        endTime = System.currentTimeMillis();

        double diff = (double) (endTime - startTime) / 1000;

        System.out.println("FERTIG (" +tries + " Versuche; " + diff + " secs)");
    }

    /**
     * Erstellt einen formatierten Text, für die Möglichkeiten, die ein Feld hat.
     * @param field Das Feld, dessen Möglichkeiten dargestellt werden sollen.
     * @return Der generierte Text.
     */
    private synchronized String possibleString(Field field) {
        String str = "";

        for (int i = 0; i < field.getPossible().size(); i++) {

            str += field.getPossible().get(i);

            if (i + 1 < field.getPossible().size())
                str += ", ";

        }

        return str;
    }


    /**
     * Gibt den aktuellen Stand in der Konsole aus.
     */
    public void showConsole() {


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
            String value = (f.getValue() > 0) ? f.getValue() + "" : " ";
            if (value.length() <= 1)
                value += " ";

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

    /**
     * Startet eine sichere Rekursionsschleife zur Ermittlung der Lösung.
     * @param field Das aktuelle Feld, ab dem die Rekursionsschleife startet.
     * @return Das Startfeld, mit dem die nächste Rekursionsschleife beginnt.
     */
    private synchronized Field saveSolve(Field field) {
        try {
            return solve(field);
        }
        catch (IllegalStateException ex) {
            System.out.print("\n\nWeg nicht erfolgreich!\t");

            return fallBack();
        }
    }

    /**
     * Lösungsalgorithmus, welcher jedes Feld rekursiv löst.
     * @param field Das aktuelle Feld
     * @throws IllegalStateException Wird geworfen, falls das Feld keine Lösung besitzt. Wird durch Rekursion behandelt.
     * @return Gibt an, bei welchem Feld es weitermachen soll.
     */
    private synchronized Field solve(Field field) throws IllegalStateException {

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

        showConsole();
        ++tries;

        if (tries % 200 == 0) {
            return field;
        }

        if (field.getPossible().size() >= 1 && !field.isFixed()) {
            field.setFixedValue(field.getPossible().get(field.incrementLastTriedIndex()));
            //System.out.println("SETZE" + field.getID() + ": \t" + field.getValue());;

            lastFields.push(field);

            if (field.getPossible().size() >= 2)
                lastMultipleFields.push(field);

            if (!treeSet.isEmpty()) {
                return saveSolve(treeSet.first());

            }


        }
        else if (field.getPossible().size() <= 0)
            throw new IllegalStateException("Field #" + field.getID() + " has 0 possibilities.");

        return null;
    }

    /**
     * Geht den Entscheidungsweg bis zur nächsten Möglichkeit zurück.
     * @return Das Feld, das als nächster Startpunkt für eine neue Rekursionsschleife verwendet wird.
     */
    private synchronized Field fallBack() {
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

        return solve(last);
    }

    /**
     * Ermittelt die Gruppen eines Feldes.
     * @param line Die Zeile des Feldes.
     * @param column Die Spalte des Feldes.
     * @return Alle Gruppen, zu denen das Feld gehört.
     */
    public synchronized Group[] getGroups(int line, int column) {
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

    /**
     * Gibt den Weg zur Lösung aus (ohne zurückgehen).
     * @return Der Lösungsweg.
     */
    public List<Field> getSolutionPath() {
        List<Field> path = new LinkedList<>();

        for (Field e : lastFields) {
            path.add(e);
        }

        return path;
    }

    public int getTimeDifference() {
        return (int) ((int) endTime - startTime);
    }

}
