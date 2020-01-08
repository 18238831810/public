package com.cf.crs.function;


import java.util.Objects;

@FunctionalInterface
public interface MyConsumer<T, U, M, N> {

    /**
     * Applies this function to the given arguments.
     *
     */
    void accept(T t, U u, M m, N n);

    default MyConsumer<T, U, M, N> andThen(MyConsumer<? super T, ? super U,? super M,? super N> after) {
        Objects.requireNonNull(after);

        return (t, u, m, n) -> {
            accept(t, u, m, n);
            after.accept(t, u, m, n);
        };
    }

}
