package core;

import com.codepoetics.protonpack.StreamUtils;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;


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



    public static void main(String[] args) {
        final int RUN_FOR = 1000;
        final int SIZE = (int) 1e10;
        final int MAX = (int) 1e10;
        Consumer<int[]> hoareFn = HoareQuicksort::quickSort;
        Contender<int[]> hoare = new Contender<>("Hoare", hoareFn);

        Consumer<int[]> libFn = Arrays::sort;
        Contender<int[]> lib = new Contender<>("Library Implementation", libFn);

        Contender[] contenders = { hoare, lib };

        Supplier<int[]> genFn = () -> genRandom(SIZE, MAX);

        ArrayList<int[]> sampleData = new ArrayList<>(RUN_FOR);
        for(int i = 0; i < RUN_FOR; i++) {
            sampleData.add(genFn.get());
        }

        final int contenderCount = contenders.length;

        Judge<int[]> judge = StreamUtils.zipWithIndex(sampleData.stream())
            .map( indexed -> {
                LapResultBuilder<int[]> resultBuilder =
                        new LapResultBuilder<>( indexed.getIndex() );
                long startTime, endTime;

                for(Contender<int[]> con : contenders) {
                    startTime = System.nanoTime();
                    con.compete(indexed.getValue());
                    endTime = System.nanoTime();

                    if (!isSorted(indexed.getValue())) {
                        throw new RuntimeException(con + " failed to sort.");
                    }

                    resultBuilder.add(con, startTime, endTime);
                }

                return resultBuilder.build();
            }).collect(
                () -> {return new Judge<int[]>(contenders);},
                Judge::accept,
                Judge::combine
            );

    }
}
