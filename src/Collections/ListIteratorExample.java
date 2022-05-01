package Collections;

import java.util.LinkedList;
import java.util.ListIterator;

public class ListIteratorExample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		LinkedList<String> ar1 = new LinkedList();
		ar1.add("India");
		ar1.add("Japan");
		ar1.add("China");
		ar1.add("Nepal");
		ListIterator ltr = ar1.listIterator();
		
		// hasNext, hasPrevious, next, previous
		System.out.println("Elements in forward direction");
		while(ltr.hasNext())
			System.out.println(ltr.next());
		
		System.out.println("Elements in reverse direction");
		while(ltr.hasPrevious())
			System.out.println(ltr.previous());
		
		

	}

}