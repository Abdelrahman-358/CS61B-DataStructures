package deque;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class testomo {
     @Test
  public void test(){
         Deque<Integer> deque = new ArrayDeque<>();
         deque.addFirst(10);
         deque.addFirst(20);
         deque.addFirst(30);
         deque.addFirst(40);
         assertEquals(4, deque.size());
         int x=deque.get(0);
         assertEquals(40,x);


     }
}
