package deque;

import org.junit.Test;
import java.util.Comparator;

import static org.junit.Assert.assertEquals;

public class MaxArrayDequeTest {
    @Test
    public void testMax() {
        Comparator<String> byLength = (a, b) -> a.length() - b.length();
        MaxArrayDeque<String> maxArrayDeque = new MaxArrayDeque<>(byLength);
        maxArrayDeque.addFirst("a");
        maxArrayDeque.addFirst("aa");
        maxArrayDeque.addFirst("aaa");
        maxArrayDeque.addFirst("aaaa");
        maxArrayDeque.addFirst("aaaaa");
        Comparator<String> rev = (a, b) -> b.length() - a.length();

        // Check if max element is "aaaaa"
        assertEquals("aaaaa", maxArrayDeque.max());
    }

    @Test
    public void testMaxWithCustomComparator() {
        Comparator<Integer> byValue = (a, b) -> a - b ;
        MaxArrayDeque<Integer> maxArrayDeque = new MaxArrayDeque<>(byValue);
        maxArrayDeque.addFirst(10);
        maxArrayDeque.addFirst(20);
        maxArrayDeque.addFirst(30);
        maxArrayDeque.addFirst(5);

        // Check if max element is 30
        assertEquals(Integer.valueOf(30), maxArrayDeque.max());
    }
    @Test
    public void testMaxWithComparator() {
        Comparator<Integer> byValue = (a, b) -> a - b;
        MaxArrayDeque<Integer> maxArrayDeque = new MaxArrayDeque<>(byValue);
        maxArrayDeque.addFirst(10);
        maxArrayDeque.addFirst(20);
        maxArrayDeque.addFirst(30);
        maxArrayDeque.addFirst(5);
        Comparator<Integer> reverse = (a, b) -> b - a;
        assertEquals(Integer.valueOf(30), maxArrayDeque.max());
        // Check with a comparator different from the default
        assertEquals(Integer.valueOf(5), maxArrayDeque.max(reverse));


    }
}
