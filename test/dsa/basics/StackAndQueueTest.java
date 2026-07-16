package dsa.basics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for the finished reference structures in {@link StackAndQueue}.
 *
 * These are in package {@code dsa.basics} on purpose: {@code IntStack} and
 * {@code IntQueue} are package-private nested classes with package-private
 * methods, so a same-package test can exercise them directly.
 */
class StackAndQueueTest {

    @Nested
    @DisplayName("IntStack (LIFO)")
    class StackTests {

        @Test
        @DisplayName("pops in last-in-first-out order")
        void lifoOrder() {
            StackAndQueue.IntStack s = new StackAndQueue.IntStack();
            s.push(1);
            s.push(2);
            s.push(3);
            assertEquals(3, s.pop());
            assertEquals(2, s.pop());
            assertEquals(1, s.pop());
            assertTrue(s.isEmpty());
        }

        @Test
        @DisplayName("peek returns the top without removing it")
        void peekDoesNotRemove() {
            StackAndQueue.IntStack s = new StackAndQueue.IntStack();
            s.push(7);
            s.push(9);
            assertEquals(9, s.peek());
            assertEquals(9, s.peek());
            assertEquals(2, s.size());
            assertEquals(9, s.pop());
            assertEquals(7, s.peek());
        }

        @Test
        @DisplayName("pop on an empty stack underflows with IllegalStateException")
        void popUnderflow() {
            StackAndQueue.IntStack s = new StackAndQueue.IntStack();
            assertThrows(IllegalStateException.class, s::pop);
        }

        @Test
        @DisplayName("peek on an empty stack underflows with IllegalStateException")
        void peekUnderflow() {
            StackAndQueue.IntStack s = new StackAndQueue.IntStack();
            assertThrows(IllegalStateException.class, s::peek);
        }

        @Test
        @DisplayName("grows past its initial capacity of 8 without losing data")
        void growsBeyondInitialCapacity() {
            StackAndQueue.IntStack s = new StackAndQueue.IntStack();
            for (int i = 0; i < 100; i++) {
                s.push(i);
            }
            assertEquals(100, s.size());
            for (int i = 99; i >= 0; i--) {
                assertEquals(i, s.pop());
            }
            assertTrue(s.isEmpty());
        }

        @Test
        @DisplayName("agrees with java.util.Deque on random push/pop sequences")
        void differentialAgainstDeque() {
            Random rng = new Random(11);
            for (int trial = 0; trial < 500; trial++) {
                StackAndQueue.IntStack mine = new StackAndQueue.IntStack();
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
            StackAndQueue.IntQueue q = new StackAndQueue.IntQueue();
            q.enqueue(1);
            q.enqueue(2);
            q.enqueue(3);
            assertEquals(1, q.dequeue());
            assertEquals(2, q.dequeue());
            assertEquals(3, q.dequeue());
            assertTrue(q.isEmpty());
        }

        @Test
        @DisplayName("peek returns the front without removing it")
        void peekDoesNotRemove() {
            StackAndQueue.IntQueue q = new StackAndQueue.IntQueue();
            q.enqueue(5);
            q.enqueue(6);
            assertEquals(5, q.peek());
            assertEquals(5, q.peek());
            assertEquals(2, q.size());
            assertEquals(5, q.dequeue());
            assertEquals(6, q.peek());
        }

        @Test
        @DisplayName("dequeue on an empty queue underflows with IllegalStateException")
        void dequeueUnderflow() {
            StackAndQueue.IntQueue q = new StackAndQueue.IntQueue();
            assertThrows(IllegalStateException.class, q::dequeue);
        }

        @Test
        @DisplayName("peek on an empty queue underflows with IllegalStateException")
        void peekUnderflow() {
            StackAndQueue.IntQueue q = new StackAndQueue.IntQueue();
            assertThrows(IllegalStateException.class, q::peek);
        }

        @Test
        @DisplayName("wraps around its circular buffer correctly under interleaved use")
        void circularWrapAround() {
            // Repeatedly fill and drain so head/tail march past the array end and
            // wrap to 0. FIFO order must survive the wrap.
            StackAndQueue.IntQueue q = new StackAndQueue.IntQueue();
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
        @DisplayName("grows past its initial capacity of 8 while preserving order")
        void growsBeyondInitialCapacity() {
            StackAndQueue.IntQueue q = new StackAndQueue.IntQueue();
            for (int i = 0; i < 100; i++) {
                q.enqueue(i);
            }
            assertEquals(100, q.size());
            for (int i = 0; i < 100; i++) {
                assertEquals(i, q.dequeue());
            }
            assertTrue(q.isEmpty());
        }

        @Test
        @DisplayName("agrees with java.util.Deque on random enqueue/dequeue sequences")
        void differentialAgainstDeque() {
            Random rng = new Random(22);
            for (int trial = 0; trial < 500; trial++) {
                StackAndQueue.IntQueue mine = new StackAndQueue.IntQueue();
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
                assertFalse(mine.size() < 0);
            }
        }
    }
}
