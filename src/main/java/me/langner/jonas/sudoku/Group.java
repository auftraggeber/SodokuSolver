package me.langner.jonas.sudoku;

import java.util.ArrayList;
import java.util.List;

public class Group {

    private List<Field> fields;
    private List<Integer> values = new ArrayList<>();

    public Group() {
        fields = new ArrayList<>();
    }

    public void addField(Field field) {

        if (!fields.contains(field))
            fields.add(field);
    }

    public void addValue(int value) {
        values.add(value);
    }

    public void removeValue(int value) {
        values.remove(new Integer(value));
    }

    public List<Integer> getValues() {
        return values;
    }

    public List<Field> getFields() {
        return fields;
    }
}
