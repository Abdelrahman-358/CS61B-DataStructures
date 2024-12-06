package deque;

import org.junit.Test;

import java.util.Optional;

import static org.junit.Assert.*;

public class ArrayDequeTest {

    @Test
    public void testEmptySize() {
        ArrayDeque<Integer> L = new ArrayDeque<>();
        assertEquals(0, L.size());
    }

    @Test
    public void testAddAndSize() {
        ArrayDeque<Integer> L = new ArrayDeque<>();
        L.addLast(99);
        L.addLast(99);
        assertEquals(2, L.size());
    }


    @Test
    public void testAddAndGetLast() {
        ArrayDeque<Integer> L = new ArrayDeque<>();
        L.addLast(99);
        assertEquals(Optional.of(99), L.getLast());
        L.addLast(36);
        assertEquals(Optional.of(36), L.getLast());
    }


    @Test
    public void testGet() {
        ArrayDeque<Integer> L = new ArrayDeque<>();

        L.addLast(99);
        assertEquals(Optional.of(99), L.get(0));
        L.addLast(36);
        assertEquals(Optional.of(99), L.get(0));
        assertEquals(Optional.of(36), L.get(1));
    }


    @Test
    public void testRemove() {
        ArrayDeque<Integer> L = new ArrayDeque<>();
        L.addLast(99);
        assertEquals(Optional.of(99), L.get(0));
        L.addLast(36);
        assertEquals(Optional.of(99), L.get(0));
        L.removeLast();
        assertEquals(Optional.of(99), L.getLast());
        L.addLast(100);
        assertEquals(Optional.of(100), L.getLast());
        assertEquals(2, L.size());
    }

    /**
     * Tests insertion of a large number of items.
     */
    @Test
    public void testMegaInsert() {
        ArrayDeque<Integer> L = new ArrayDeque<>();
        int N = 1000000;
        for (int i = 0; i < N; i += 1) {
            L.addLast(i);
        }
        for (int i = 0; i < N; i += 1) {
            L.addLast(L.get(i));
        }
    }

}
