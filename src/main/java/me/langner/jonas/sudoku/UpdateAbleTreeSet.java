package me.langner.jonas.sudoku;

import java.util.*;

public class UpdateAbleTreeSet<T extends UpdateAble> extends TreeSet<T> {

    private Set<UpdateListener<T>> listeners = new HashSet<>();

    public UpdateAbleTreeSet() {
    }

    public UpdateAbleTreeSet(Comparator<? super T> comparator) {
        super(comparator);
    }

    public UpdateAbleTreeSet(Collection<? extends T> c) {
        super(c);
    }

    public UpdateAbleTreeSet(SortedSet<T> s) {
        super(s);
    }

    public void addListener(UpdateListener<T> listener) {
        listeners.add(listener);
    }

    public void update(T t) {
        super.add(t);

        listeners.forEach(l -> {
            l.updated(t);
        });
    }

    @Override
    public boolean add(T t) {
        t.setTree(this);

        return super.add(t);
    }
}
