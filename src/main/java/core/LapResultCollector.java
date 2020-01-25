package core;


import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class LapResultCollector<T>
        implements Collector<LapResult<T>,
        HashMap<Contender<T>, Integer>, HashMap<Contender<T>, Integer>> {



    @Override
    public Supplier<HashMap<Contender<T>, Integer>> supplier() {
        return HashMap::new;
    }

    @Override
    public BiConsumer<HashMap<Contender<T>, Integer>, LapResult<T>> accumulator() {
        return (acc, result) -> {
            Contender<T> winner = result.getWinner();
            assert acc.containsKey(winner);

            acc.computeIfPresent(
                    result.getWinner(), // key
                    (key, oldValue) -> oldValue + 1 // remapping function
            );
        };
    }

    @Override
    public BinaryOperator<HashMap<Contender<T>, Integer>> combiner() {
        return (map1, map2) -> {
            map1.keySet().forEach(key -> {
                assert map1.containsKey(key) && map2.containsKey(key);

                map1.computeIfPresent(key, (k, value) -> value + map2.get(key));
            });

            return map1;
        };
    }

    @Override
    public Function<HashMap<Contender<T>, Integer>,HashMap<Contender<T>, Integer>> finisher() {
        return hashMap -> hashMap;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return new HashSet<>();
    }

//    static <T> Comparator<Lap<T>> byElapsed() {
//        return (lap1, lap2) -> Long.compareUnsigned(lap1.elapsed, lap2.elapsed);
//    }

//    static <T> Collector asCollector(int runID) {
//        return Collector.of(
//            Judge<Lap<T>>::new,
//            Judge<Lap<T>>::accept,
//            Judge<Lap<T>>::combine
//        )
//    }


//    LapResult<T> judge() {
//        return new LapResult<T>(laps, runID);
//    }

//    Map<Contender<T>, Long> winMap;
//
//    Judge(Contender<T>[] contenders) {
//        winMap = new HashMap<>();
//        for (Contender<T> c : contenders) {
//            winMap.put(c, 0L);
//        }
//    }
//
//    void accept(Lap result) {
//        Long newVal = winMap.computeIfPresent(result.getWinner(),
//                (key, oldValue) -> ++oldValue
//        );
//
//        if (newVal == null) throw new RuntimeException("Invalid judge config, " +
//                "missing Contender: " + result.getWinner());
//    }
//
//    void combine(Judge<T> o) {
//        if (!winMap.keySet().equals(o.winMap.keySet())) {
//            throw new RuntimeException("Cannot combine different judges");
//        }
//
//        winMap.forEach((k, v) -> this.winMap.put(k, v + o.winMap.get(k)));
//    }
//
//    SortedMap<Contender<T>, Long> judge() {
//        return winMap
//            .entrySet()
//            .stream()
//            .sorted(
//                (e1, e2) -> Long.compareUnsigned(e1.getValue(), e2.getValue()))
//            .
//    }

}
