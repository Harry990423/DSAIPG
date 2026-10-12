package com.phasmidsoftware.dsaipg.adt.pq;

import java.util.Comparator;


public class PriorityQueue_4AryHeap<K> implements PriorityQueue<K> {

    private final K[] heap;
    private final Comparator<K> comparator;
    private final boolean max;
    private final boolean floyd;
    private int size;

    @SuppressWarnings("unchecked")
    public PriorityQueue_4AryHeap(
            int capacity,
            boolean max,
            Comparator<K> comparator,
            boolean floyd) {
        this.heap = (K[]) new Object[capacity + 1];
        this.max = max;
        this.comparator = comparator;
        this.floyd = floyd;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void give(K key) {
        // 保持和现有二叉堆相同的容量处理方式
        if (size == heap.length - 1) {
            size--;
        }

        heap[++size] = key;
        swimUp(size);
    }

    @Override
    public K take() throws PQException {
        if (isEmpty()) {
            throw new PQException("Priority queue is empty");
        }

        K result = heap[1];
        heap[1] = heap[size];
        heap[size] = null;
        size--;

        if (size > 0) {
            if (floyd) {
                snake(1);
            } else {
                sink(1);
            }
        }

        return result;
    }

    private void sink(int index) {
        while (firstChild(index) <= size) {
            int child = bestChild(index);

            if (!higherPriority(heap[child], heap[index])) {
                break;
            }

            swap(index, child);
            index = child;
        }
    }

    private void snake(int index) {
        while (firstChild(index) <= size) {
            int child = bestChild(index);
            swap(index, child);
            index = child;
        }

        swimUp(index);
    }

    private void swimUp(int index) {
        while (index > 1) {
            int parent = parent(index);

            if (!higherPriority(heap[index], heap[parent])) {
                break;
            }

            swap(index, parent);
            index = parent;
        }
    }

    private int bestChild(int index) {
        int first = firstChild(index);
        int last = Math.min(first + 3, size);
        int best = first;

        for (int child = first + 1; child <= last; child++) {
            if (higherPriority(heap[child], heap[best])) {
                best = child;
            }
        }

        return best;
    }

    private int firstChild(int index) {
        return 4 * index - 2;
    }

    private int parent(int index) {
        return (index - 2) / 4 + 1;
    }

    private boolean higherPriority(K a, K b) {
        int comparison = comparator.compare(a, b);
        return max ? comparison > 0 : comparison < 0;
    }

    private void swap(int i, int j) {
        K temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    @Override
    public void heapConstructor() {
        for (int i = parent(size); i >= 1; i--) {
            sink(i);
        }
    }

    @Override
    public K peek(int k) {
        return heap[k];
    }

    @Override
    public boolean getMax() {
        return max;
    }
}