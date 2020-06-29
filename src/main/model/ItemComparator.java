package main.model;

import java.util.Comparator;

public class ItemComparator implements Comparator<Item> {
    transient static final private ItemComparator comparator = new ItemComparator();

    public static ItemComparator getInstance() {
        return comparator;
    }

    @Override
    public int compare(Item o1, Item o2) {
        return o1.getName().compareTo(o2.getName());
    }
}
