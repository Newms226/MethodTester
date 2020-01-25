package core;

import com.codepoetics.protonpack.StreamUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class Race {

    static int[] genRandom(int count, int max) {
        int[] values = new int[count];
        Random ran = new Random();

        for(int i = 0; i < count; i++) {
           values[i] = ran.nextInt(max);
        }
        return values;
    }

    static boolean isSorted(int[] arr) { // TODO replace with Apache Commons ArrayUtils
        for(int j = 1; j < arr.length; j++){
            if(arr[j-1] > arr[j]) return false;
        }

        return true;
    }

    static void assertIsSorted(int[] arr) throws RuntimeException {
        if (!isSorted(arr)) throw new RuntimeException("Array isn't sorted");
    }



    public static void main(String[] args) {
        final int RUN_FOR = 1000;
        final int SIZE = (int) 100;
        final int MAX = (int) 1e6;

        List<Contender<int[]>> contenders = new ArrayList<>();
        Supplier<int[]> genFn = () -> genRandom(SIZE, MAX);

        Consumer<int[]> hoareFn = HoareQuicksort::quickSort;
        contenders.add(new Contender<>("Hoare", hoareFn));

        Consumer<int[]> libFn = Arrays::sort;
        contenders.add(new Contender<>("Library Implementation", libFn));

        final List<Contender<int[]>> finalContenders =
                Collections.unmodifiableList(contenders);
        final int contenderCount = contenders.size();

        List<LapResult<int[]>> lapResults = IntStream
            .rangeClosed(1, RUN_FOR)
//            .parallel() TODO
            .mapToObj(run -> {
                System.out.println("Started running run " + run);
                int[] ranSeed = genFn.get();

                SortedSet<Lap<int[]>> laps = finalContenders.stream().map(contender -> {
                    int[] copy = ranSeed.clone();
                    long startTime, endTime;

                    startTime = System.nanoTime();
                    contender.invoke(copy);
                    endTime = System.nanoTime();

                    assertIsSorted(copy);

                    return new Lap<>(contender, endTime - startTime);
                }).collect(Collectors.toCollection(TreeSet::new));

                System.out.println("Finished run " + run);
                return new LapResult<int[]>(laps, run);})
            .collect(Collectors.toList());

        System.out.print(lapResults + "\nFinished Running.\n\n");
        Map<Contender<int[]>, Integer> winnerMap =
                lapResults.stream().collect(new LapResultCollector<int[]>());

        System.out.print(winnerMap);
        winnerMap.forEach((contender, count) -> {
            System.out.println(contender + " won " + count + " times.");
        });
    }
}
