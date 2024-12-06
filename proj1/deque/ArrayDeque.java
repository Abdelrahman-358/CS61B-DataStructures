package deque;

import org.junit.Test;

import deque.Deque;

public class ArrayDeque<T> implements Deque<T> {
    private int nextFirst;
    private int nextLast;
    private int size;
    private T[] deque;

    /**
     * Creates an empty list.
     */
    public ArrayDeque() {
        deque = (T[]) new Object[9];
        nextFirst = 0;
        nextLast = 1;
        size = 0;
    }

    /**
     * return the next index
     */
    int next(int i) {
        return (i + 1) % deque.length;
    }

    /**
     * return previous index
     */
    int prev(int i) {
        return (i - 1 + deque.length) % deque.length;
    }

    /**
     * Inserts X into the back of the list.
     */
    @Override
    public void addLast(T x) {
        if (size == deque.length) {
            resize(deque.length * 2);
        }
        deque[nextLast] = x;
        nextLast = next(nextLast);
        size++;
    }
    @Override
    public void addFirst(T x) {
        if (size == deque.length) {
            resize(deque.length * 2);
        }
        deque[nextFirst] = x;
        nextFirst = prev(nextFirst);
        size++;
    }

    /**
     * Returns the item from the back of the list.
     */
    public T getLast() {
        if (size == 0) {
            return null;
        }
        return deque[prev(nextLast)];
    }

    /**
     * Gets the ith item in the list (0 is the front).
     */
    @Override
    public T get(int i) {
        if (i >= size || size == 0) return null;
        int current = (nextFirst + i + 1) % deque.length;

        return deque[current];
    }

    /**
     * Returns the number of items in the list.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * resize the deque
     */
    void resize(int newSize) {
        T[] newDeque = (T[]) new Object[newSize];
        int cur = next(nextFirst);
        int i = 0;
        while (i < size) {
            newDeque[i] = deque[cur];
            cur = next(cur);
            i++;
        }
        deque = newDeque;
        nextFirst = newSize - 1;
        nextLast = i;
    }

    /**
     * Deletes item from back of the list and
     * returns deleted item.
     */
    @Override
    public T removeLast() {
        if (size == 0) return null;
        if (size < deque.length / 4) {
            resize(deque.length / 4);
        }
        int current = prev(nextLast);
        size--;
        nextLast = prev(nextLast);
        return deque[current];
    }

    /**
     * Deletes item from front of the list and
     * returns deleted item.
     */
    @Override
    public T removeFirst() {
        if (size == 0) return null;
        if (size < deque.length / 4) {
            resize(deque.length / 4);
        }

        int current = next(nextFirst);
        size--;
        nextFirst = next(nextFirst);
        return deque[current];
    }

    /**
     * print the deque
     */
    @Override
    public void printDeque() {
        int current = next(nextFirst);
        while (current != nextLast) {
            System.out.print(deque[current] + " ");
            current = next(current);
        }
    }

}
