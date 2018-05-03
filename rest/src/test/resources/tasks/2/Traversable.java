package org.flaxo.examples;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Traversable<T> {
    void forEach(final Consumer<T> consumer);

    default Traversable<T> filter(final Predicate<T> predicate) {
        throw new UnsupportedOperationException();
    }

    default <R> Traversable<R> map(final Function<T, R> mapper) {
        throw new UnsupportedOperationException();
    }

    default <R> Traversable<R> flatMap(final Function<T, List<R>> mapper) {
        throw new UnsupportedOperationException();
    }

    default List<T> toList() {
        throw new UnsupportedOperationException();
    }

    static <T> Traversable<T> from(final List<T> list) {
        throw new UnsupportedOperationException();
    }
}