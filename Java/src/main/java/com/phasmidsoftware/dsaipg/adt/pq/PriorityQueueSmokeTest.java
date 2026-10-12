package com.phasmidsoftware.dsaipg.adt.pq;

import java.util.Comparator;

public class PriorityQueueSmokeTest {
    public static void main(String[] args) throws Exception {
    Comparator<Integer> comparator = Integer::compare;
        PriorityQueue_BinaryHeap<Integer> pq =
        new PriorityQueue_BinaryHeap<>(3, true, comparator, false);

int[] values = {9, 7, 8, 5};

for (int value : values) {
    if (pq.size() == 3) {
        System.out.println("即将被覆盖：" + pq.peek(3));
    }
    pq.give(value);
}

while (!pq.isEmpty()) {
    System.out.println("take: " + pq.take());
}
    }
}