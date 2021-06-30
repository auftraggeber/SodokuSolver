package me.langner.jonas.sudoku;

import java.util.Collections;

public abstract class UpdateAble {

    private UpdateAbleTreeSet set;

    public void setTree(UpdateAbleTreeSet tree) {
        this.set = tree;
    }

    public void removeFromTree() {
        if (set != null) {
            set.remove(this);
        }
    }

    public void beforeUpdate() {
        removeFromTree();
    }

    public void updated() {
        if (set != null)
            set.update(this);
    }

    public UpdateAbleTreeSet getSet() {
        return set;
    }
}
