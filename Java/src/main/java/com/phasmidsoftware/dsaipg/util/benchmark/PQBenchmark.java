package com.phasmidsoftware.dsaipg.util.benchmark;

import com.phasmidsoftware.dsaipg.adt.pq.PQException;
import com.phasmidsoftware.dsaipg.adt.pq.PriorityQueue;
import com.phasmidsoftware.dsaipg.adt.pq.PriorityQueue_4AryHeap;
import com.phasmidsoftware.dsaipg.adt.pq.PriorityQueue_BinaryHeap;
import com.phasmidsoftware.dsaipg.util.config.Config;

import java.io.IOException;
import java.util.Random;

public class PQBenchmark {

    private static final int MAX_SIZE = 4095;
    private static final int INSERT_COUNT = 16_000;
    private static final int REMOVE_COUNT = 4_000;
    private static final int REPETITIONS = 50;

    private final Config config;

    public PQBenchmark(Config config) {
        this.config = config;
    }

    public static void main(String[] args) throws IOException {
        Config config = Config.load(PQBenchmark.class);
        PQBenchmark benchmark = new PQBenchmark(config);

        int[] values = benchmark.createValues();

        Integer binarySpilled =
                benchmark.findHighestSpilled(values, false);
        Integer fourArySpilled =
                benchmark.findHighestSpilled(values, true);

        System.out.println(
                "Highest spilled (binary): " + binarySpilled);
        System.out.println(
                "Highest spilled (4-ary): " + fourArySpilled);

        double basicTime =
                benchmark.runBenchmark(values, false, false);
        double floydTime =
                benchmark.runBenchmark(values, true, false);
        double fourAryTime =
                benchmark.runBenchmark(values, false, true);
        double fourAryFloydTime =
                benchmark.runBenchmark(values, true, true);

        System.out.println("Basic binary heap: " + basicTime + " ms");
        System.out.println("Floyd binary heap: " + floydTime + " ms");
        System.out.println("Basic 4-ary heap: " + fourAryTime + " ms");
        System.out.println("Floyd 4-ary heap: " + fourAryFloydTime + " ms");
    }

    private int[] createValues() {
        Random random = new Random(6205);
        int[] values = new int[INSERT_COUNT];

        for (int i = 0; i < values.length; i++) {
            values[i] = random.nextInt(INSERT_COUNT);
        }

        return values;
    }

    /**
     * Records the highest element overwritten when the heap is full.
     */
    private Integer findHighestSpilled(int[] values, boolean fourAry) {
        PriorityQueue<Integer> pq = fourAry
                ? new PriorityQueue_4AryHeap<>(
                        MAX_SIZE, true, Integer::compare, false)
                : new PriorityQueue_BinaryHeap<>(
                        MAX_SIZE, true, Integer::compare, false);

        Integer highestSpilled = null;

        for (int value : values) {
            if (pq.size() == MAX_SIZE) {
                int spilled = pq.peek(pq.size());

                if (highestSpilled == null || spilled > highestSpilled) {
                    highestSpilled = spilled;
                }
            }

            pq.give(value);
        }

        return highestSpilled;
    }

    private double runBenchmark(
            int[] values, boolean floyd, boolean fourAry) {

        String heapName = fourAry ? "4-ary heap" : "Binary heap";
        String methodName = floyd ? " with Floyd" : "";

        Benchmark<Boolean> timer = new Benchmark_Timer<>(
                heapName + methodName,
                config,
                null,
                ignored -> runWorkload(values, floyd, fourAry),
                null);

        return timer.run(true, REPETITIONS);
    }

    private void runWorkload(
            int[] values, boolean floyd, boolean fourAry) {

        PriorityQueue<Integer> pq = fourAry
                ? new PriorityQueue_4AryHeap<>(
                        MAX_SIZE, true, Integer::compare, floyd)
                : new PriorityQueue_BinaryHeap<>(
                        MAX_SIZE, true, Integer::compare, floyd);

        for (int value : values) {
            pq.give(value);
        }

        for (int i = 0; i < REMOVE_COUNT; i++) {
            try {
                pq.take();
            } catch (PQException e) {
                throw new IllegalStateException(
                        "The priority queue became empty too early.", e);
            }
        }
    }
}