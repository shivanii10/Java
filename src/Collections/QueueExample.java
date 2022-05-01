package Collections;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.function.Predicate;
public class QueueExample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Predicate<Integer> p = n->n%2==0;
		
		System.out.println("Priority Queue: ");
		Queue<Integer> q = new PriorityQueue();
		q.add(10);
		q.add(3);
		q.add(8);
		q.add(5);
		System.out.println(q);
		q.remove(); //removes one with least priority
		System.out.println("after removal of element with least priority: ");
		System.out.println(q);
		
		Deque dq = new ArrayDeque();
		dq.add(10);
		dq.add(3);
		dq.add(8);
		dq.add(5);
		dq.addFirst(19);
		System.out.println("Deque: ");
		System.out.println(dq);
		System.out.println("After removing multiples of 2 ");
		dq.removeIf(p);
		System.out.println(dq);
		

	}

}