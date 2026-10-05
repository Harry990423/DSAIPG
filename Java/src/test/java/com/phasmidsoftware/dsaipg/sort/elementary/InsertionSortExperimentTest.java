package com.phasmidsoftware.dsaipg.sort.elementary;

import com.phasmidsoftware.dsaipg.util.benchmark.Benchmark_Timer;
import com.phasmidsoftware.dsaipg.util.config.Config;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;

public class InsertionSortExperimentTest {

    @Test
    public void runInsertionSortExperiment() throws IOException {
        Config config = Config.load(InsertionSortExperimentTest.class);

        int repetitions = 10;
        int[] sizes = {200, 400, 800, 1600, 3200};

        System.out.println(
                "n,random_ms,ordered_ms,partially_ordered_ms,reverse_ordered_ms"
        );

        for (int n : sizes) {
            double randomTime = measure(
                    "Random insertion sort",
                    makeRandomArray(n),
                    config,
                    repetitions
            );

            double orderedTime = measure(
                    "Ordered insertion sort",
                    makeOrderedArray(n),
                    config,
                    repetitions
            );

            double partiallyOrderedTime = measure(
                    "Partially ordered insertion sort",
                    makePartiallyOrderedArray(n),
                    config,
                    repetitions
            );

            double reverseOrderedTime = measure(
                    "Reverse ordered insertion sort",
                    makeReverseOrderedArray(n),
                    config,
                    repetitions
            );

            System.out.printf(
                    Locale.US,
                    "%d,%.6f,%.6f,%.6f,%.6f%n",
                    n,
                    randomTime,
                    orderedTime,
                    partiallyOrderedTime,
                    reverseOrderedTime
            );
        }
    }

    private static double measure(
            String description,
            Integer[] input,
            Config config,
            int repetitions
    ) throws IOException {
        try (InsertionSortComparator<Integer> sorter =
                     new InsertionSortComparator<>(
                             Integer::compareTo,
                             input.length,
                             repetitions,
                             config
                     )) {
            Benchmark_Timer<Integer[]> benchmark =
                    new Benchmark_Timer<>(
                            description,
                            config,
                            values -> sorter.sort(values)
                    );

            return benchmark.run(input, repetitions);
        }
    }

    private static Integer[] makeOrderedArray(int n) {
        Integer[] values = new Integer[n];

        for (int i = 0; i < n; i++) {
            values[i] = i;
        }

        return values;
    }

    private static Integer[] makeReverseOrderedArray(int n) {
        Integer[] values = new Integer[n];

        for (int i = 0; i < n; i++) {
            values[i] = n - 1 - i;
        }

        return values;
    }

    private static Integer[] makeRandomArray(int n) {
        Integer[] values = makeOrderedArray(n);
        Collections.shuffle(Arrays.asList(values), new Random(42));
        return values;
    }

    private static Integer[] makePartiallyOrderedArray(int n) {
        Integer[] values = makeOrderedArray(n);
        Random random = new Random(42);

        for (int k = 0; k < n / 10; k++) {
            int i = random.nextInt(n);
            int j = random.nextInt(n);

            Integer temp = values[i];
            values[i] = values[j];
            values[j] = temp;
        }

        return values;
    }
}