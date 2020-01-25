package com.method_tester.core;

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
        final int RUN_FOR = 100;
        final int SIZE = (int) 10;
        final int MAX = (int) 1e20;

        List<Contender<int[]>> contenders = new ArrayList<>();
        Supplier<int[]> genFn = () -> genRandom(SIZE, MAX);

        Consumer<int[]> hoareFn = HoareQuicksort::quickSort;
        contenders.add(new Contender<>("Hoare", hoareFn));

        Consumer<int[]> mergeFn = MergeSort::sort;
        contenders.add(new Contender<>("Merge", mergeFn));

//        Consumer<int[]> libFn = Arrays::sort;
//        contenders.add(new Contender<>("Library Implementation", libFn));

        final List<Contender<int[]>> finalContenders =
                Collections.unmodifiableList(contenders);
        final int contenderCount = contenders.size();

        System.out.println("Starting...");
        List<LapResult<int[]>> lapResults = IntStream
            .rangeClosed(1, RUN_FOR)
            .parallel()
            .mapToObj(run -> {
                System.out.println("Started running run " + run);
                int[] ranSeed = genFn.get();

                SortedSet<Lap<int[]>> laps = finalContenders.stream()
                    .parallel()
                    .map(contender -> {
                        int[] copy = ranSeed.clone();
                        long startTime, endTime;

                        startTime = System.nanoTime();
                        contender.invoke(copy);
                        endTime = System.nanoTime();

                        assertIsSorted(copy);

                        return new Lap<>(contender, endTime - startTime);})
                    .collect(Collectors.toCollection(TreeSet::new));

//                System.out.println("Finished run " + run);
                return new LapResult<int[]>(laps, run);})
            .collect(Collectors.toList());

//        System.out.print(lapResults + "\nFinished Running.\n\n");
        List<ContenderWinRecord<int[]>> winnerList = lapResults.stream()
            .collect(Collectors.groupingBy(LapResult<int[]>::getWinner))
            .entrySet()
            .stream()
            .map(ContenderWinRecord::fromEntry)
            .sorted(Comparator.reverseOrder())
            .collect(Collectors.toList());

        winnerList.forEach(record -> System.out.println(record.toString(true)));

        System.out.println(winnerList);

    }
}
