package com.method_tester.core;

import java.util.function.Supplier;

public class RaceConfig<T> {

    final int runFor;

    final Supplier<T> genFunction;

    final Contender<T>[] contenders;

    RaceConfig(int runFor, Supplier<T> genFunction, Contender<T>[] contenders) {
        this.runFor = runFor;
        this.genFunction = genFunction;
        this.contenders = contenders;
    }
}
