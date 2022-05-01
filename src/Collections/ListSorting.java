package Collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

//Comparator is a functional Interface

/*
class MySort implements Comparator<String>{

	@Override
	public int compare(String o1, String o2) {
		// TODO Auto-generated method stub
		return -o1.compareTo(o2);
	}
	
}
*/
// (s1,s2)->-s1.compareTo(s2);

public class ListSorting {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<String> ar1 = new ArrayList();
		ar1.add("India");
		ar1.add("Japan");
		ar1.add("China");
		ar1.add("Nepal");
		System.out.println(ar1);
		// Collections.sort(collObject)
		Collections.sort(ar1);
		System.out.println("After sorting");
		System.out.println(ar1);
		
		//METHOD 2 : REVERSE ORDER
		// Collections.sort(collObject, ComparatorObject)
		//System.out.println("Reverse sorted order is :");
		//MySort m = new MySort();
		//Collections.sort(ar1,m);
		//System.out.println(ar1);
		
		//Same method using lambda expression
		// using - for reverse order
		//this shortens the code
		Collections.sort(ar1,(s1,s2)->-s1.compareTo(s2));
		System.out.println("Reverse sorted order is :");
		System.out.println(ar1);
		

	}

}