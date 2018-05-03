package org.flaxo.examples;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public interface Range {

    int lowerBound();

    int upperBound();

    default boolean isBefore(final Range other) {
        return this.upperBound() < other.lowerBound();
    }

    default boolean isAfter(final Range other) {
        return this.lowerBound() > other.upperBound();
    }

    default boolean isConcurrent(final Range other) {
        return !other.isBefore(this) && !other.isAfter(this);
    }

    default boolean contains(final int value) {
        return (this.lowerBound() <= value && this.upperBound() >= value);
    }

    default List<Integer> asList() {
        int listSize = this.upperBound() - this.lowerBound() + 1;
        List<Integer> list = new ArrayList<>(listSize);

        for (int i = this.lowerBound(); i <= this.upperBound(); i++) {
            list.add(i);
        }

        return list;
    }

    default Iterator<Integer> asIterator() {
        return this.asList().iterator();
    }

    static Range bounds(final int lower, final int upper) {
        if (lower > upper) {
            throw new IllegalArgumentException("lower border cannot exceed upper border");
        }
        return new Range() {
            @Override
            public int lowerBound() {
                return lower;
            }

            @Override
            public int upperBound() {
                return upper;
            }
        };
    }
}