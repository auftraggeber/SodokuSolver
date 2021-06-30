package me.langner.jonas.sudoku;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FieldTest {

    @Test
    void getID() {

        Field f = new Field(9);

        assertEquals(0, f.getID());

    }

    @Test
    void tree() {
        UpdateAbleTreeSet<Field> set1 = new UpdateAbleTreeSet<>();


        Field field1 = new Field(9);
        Field field2 = new Field(9);
        Field field3 = new Field(9);
        Field field4 = new Field(9);

        set1.add(field2);
        set1.add(field1);
        set1.add(field4);
        set1.add(field3);

        assertEquals(field1, set1.first());
        assertEquals(field4, set1.last());
        assertEquals(4, set1.size());

        field3.removePossibility(false,4);

        assertEquals(field3, set1.first());
        assertEquals(4, set1.size());
    }
}