package Collections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;

public class TraversingArrayListExample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
	Consumer<Integer> cn  = n->System.out.println(n*n);
	
		ArrayList<Integer> ar = new ArrayList();
		ar.add(10);
		ar.add(1);
		ar.add(3);
		ar.add(5);
		System.out.println(ar);
	
		LinkedList<String> ar1 = new LinkedList();
		ar1.add("India");
		ar1.add("Japan");
		ar1.add("China");
		ar1.add("Nepal");
		ar1.add("Japan");
		System.out.println(ar1);
		ar1.add(null);
		System.out.println(ar1);

		System.out.println("Iteration using iterator class");
		Iterator itr = ar1.iterator();
		while (itr.hasNext())
			System.out.println(itr.next());
		
		System.out.println("Iteration using for loop");
		for(String c: ar1)
			System.out.println(c);
		
		System.out.println("Iteration using forEach");
		ar1.forEach(System.out::println);
		
		ar.forEach(cn);
		
		
	}

}