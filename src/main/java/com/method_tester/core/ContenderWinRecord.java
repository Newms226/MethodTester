package com.method_tester.core;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ContenderWinRecord<T> implements Comparable<ContenderWinRecord<T>> {

    static <T> ContenderWinRecord<T> fromEntry(Map.Entry<Contender<T>, List<LapResult<T>>> entry) {
        return new ContenderWinRecord<T>(entry.getKey(), entry.getValue());
    }

    final Contender<T> contender;

    final List<LapResult<T>> winList;

    final long count;

    ContenderWinRecord(Contender<T> contender, List<LapResult<T>> winList) {
        this.contender = contender;
        this.winList = Collections.unmodifiableList(winList);
        this.count = winList.size();
    }

    @Override
    public String toString() {
        return "Contender " + contender + " won " + count + " time(s)";
    }

    public String toString(boolean deepInsight) {
        String str = winList.stream()
            .map(lapResult ->
                "  (" + ((int) (lapResult.difference() * 100)) +  "%) " + lapResult)
            .collect(Collectors.joining("\n"));
        return toString() + "\n" + str;
    }

    @Override
    public int compareTo(ContenderWinRecord<T> o) {
        return Long.compareUnsigned(count, o.count);
    }
}
