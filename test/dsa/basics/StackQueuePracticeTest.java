package dsa.basics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for the learner-implemented structures in {@link StackQueuePractice}.
 *
 * The practice file ships with push/pop/peek and enqueue/dequeue/peek already
 * filled in, so these are genuine correctness tests, not stub checks. Same
 * package as the target so the package-private nested classes are reachable.
 */
class StackQueuePracticeTest {

    @Nested
    @DisplayName("IntStack (LIFO)")
    class StackTests {

        @Test
        @DisplayName("pops in last-in-first-out order")
        void lifoOrder() {
            StackQueuePractice.IntStack s = new StackQueuePractice.IntStack();
            s.push(10);
            s.push(20);
            s.push(30);
            assertEquals(30, s.pop());
            assertEquals(20, s.pop());
            assertEquals(10, s.pop());
            assertTrue(s.isEmpty());
        }

        @Test
        @DisplayName("peek returns the top without removing it")
        void peekDoesNotRemove() {
            StackQueuePractice.IntStack s = new StackQueuePractice.IntStack();
            s.push(1);
            s.push(2);
            assertEquals(2, s.peek());
            assertEquals(2, s.size());
        }

        @Test
        @DisplayName("pop / peek on an empty stack underflow")
        void underflow() {
            StackQueuePractice.IntStack s = new StackQueuePractice.IntStack();
            assertThrows(IllegalStateException.class, s::pop);
            assertThrows(IllegalStateException.class, s::peek);
        }

        @Test
        @DisplayName("agrees with java.util.Deque on random push/pop sequences")
        void differentialAgainstDeque() {
            Random rng = new Random(1);
            for (int trial = 0; trial < 500; trial++) {
                StackQueuePractice.IntStack mine = new StackQueuePractice.IntStack();
                Deque<Integer> ref = new ArrayDeque<>();
                int ops = rng.nextInt(80);
                for (int k = 0; k < ops; k++) {
                    if (ref.isEmpty() || rng.nextBoolean()) {
                        int v = rng.nextInt(1000);
                        mine.push(v);
                        ref.push(v);
                    } else {
                        assertEquals(ref.peek().intValue(), mine.peek());
                        assertEquals(ref.pop().intValue(), mine.pop());
                    }
                    assertEquals(ref.size(), mine.size());
                }
            }
        }
    }

    @Nested
    @DisplayName("IntQueue (FIFO)")
    class QueueTests {

        @Test
        @DisplayName("dequeues in first-in-first-out order")
        void fifoOrder() {
            StackQueuePractice.IntQueue q = new StackQueuePractice.IntQueue();
            q.enqueue(10);
            q.enqueue(20);
            q.enqueue(30);
            assertEquals(10, q.dequeue());
            assertEquals(20, q.dequeue());
            assertEquals(30, q.dequeue());
            assertTrue(q.isEmpty());
        }

        @Test
        @DisplayName("peek returns the front without removing it")
        void peekDoesNotRemove() {
            StackQueuePractice.IntQueue q = new StackQueuePractice.IntQueue();
            q.enqueue(1);
            q.enqueue(2);
            assertEquals(1, q.peek());
            assertEquals(2, q.size());
        }

        @Test
        @DisplayName("dequeue / peek on an empty queue underflow")
        void underflow() {
            StackQueuePractice.IntQueue q = new StackQueuePractice.IntQueue();
            assertThrows(IllegalStateException.class, q::dequeue);
            assertThrows(IllegalStateException.class, q::peek);
        }

        @Test
        @DisplayName("wraps around its circular buffer correctly under interleaved use")
        void circularWrapAround() {
            StackQueuePractice.IntQueue q = new StackQueuePractice.IntQueue();
            int next = 0;
            int expected = 0;
            for (int round = 0; round < 20; round++) {
                for (int i = 0; i < 5; i++) {
                    q.enqueue(next++);
                }
                for (int i = 0; i < 3; i++) {
                    assertEquals(expected++, q.dequeue());
                }
            }
            while (!q.isEmpty()) {
                assertEquals(expected++, q.dequeue());
            }
            assertEquals(next, expected);
        }

        @Test
        @DisplayName("agrees with java.util.Deque on random enqueue/dequeue sequences")
        void differentialAgainstDeque() {
            Random rng = new Random(2);
            for (int trial = 0; trial < 500; trial++) {
                StackQueuePractice.IntQueue mine = new StackQueuePractice.IntQueue();
                Deque<Integer> ref = new ArrayDeque<>();
                int ops = rng.nextInt(80);
                for (int k = 0; k < ops; k++) {
                    if (ref.isEmpty() || rng.nextBoolean()) {
                        int v = rng.nextInt(1000);
                        mine.enqueue(v);
                        ref.addLast(v);
                    } else {
                        assertEquals(ref.peekFirst().intValue(), mine.peek());
                        assertEquals(ref.removeFirst().intValue(), mine.dequeue());
                    }
                    assertEquals(ref.size(), mine.size());
                }
            }
        }
    }
}
