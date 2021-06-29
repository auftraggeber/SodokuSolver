package me.langner.jonas.sodoku;

import javax.swing.event.ChangeListener;
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
