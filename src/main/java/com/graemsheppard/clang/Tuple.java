package com.graemsheppard.clang;

import lombok.Getter;

public class Tuple<T1, T2> {
    @Getter
    private final T1 first;

    @Getter
    private final T2 second;

    public Tuple(T1 first, T2 second) {
        this.first = first;
        this.second = second;
    }
}
