package core;

import com.codepoetics.protonpack.StreamUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;
import java.util.stream.Collectors;

public class Judge<T> {

    Map<Contender<T>, Long> winMap;

    Judge(Contender<T>[] contenders) {
        winMap = new HashMap<>();
        for (Contender<T> c : contenders) {
            winMap.put(c, 0L);
        }
    }

    void accept(LapResult<T> result) {
        Long newVal = winMap.computeIfPresent(result.getWinner(),
                (key, oldValue) -> ++oldValue
        );

        if (newVal == null) throw new RuntimeException("Invalid judge config, " +
                "missing Contender: " + result.getWinner());
    }

    void combine(Judge<T> o) {
        if (!winMap.keySet().equals(o.winMap.keySet())) {
            throw new RuntimeException("Cannot combine different judges");
        }

        winMap.forEach((k, v) -> this.winMap.put(k, v + o.winMap.get(k)));
    }

    SortedMap<Contender<T>, Long> judge() {
        return winMap
            .entrySet()
            .stream()
            .sorted(
                (e1, e2) -> Long.compareUnsigned(e1.getValue(), e2.getValue()))
            .
    }

}
