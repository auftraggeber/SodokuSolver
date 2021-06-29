package me.langner.jonas.sodoku;

public abstract class UpdateAble {

    private UpdateAbleTreeSet set;

    public void setTree(UpdateAbleTreeSet tree) {
        this.set = tree;
    }

    public void beforeUpdate() {
        if (set != null)
            set.remove(this);
    }

    public void updated() {
        if (set != null)
            set.update(this);
    }

}
