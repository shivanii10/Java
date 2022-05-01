package java8.Streams;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MinMaxStream {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<Integer> l1 = new ArrayList<Integer>(); 
		l1.add(0); 
		l1.add(15); 
		l1.add(10); 
		l1.add(5); 
		l1.add(30); 
		l1.add(25); 
		l1.add(20); 
		System.out.println(l1);
		
		System.out.println("Sorted Array: "); 
		Stream s= l1.stream();
		ArrayList<Integer> result=(ArrayList<Integer>) s.sorted().collect(Collectors.toList());
		System.out.println(result); 
		
		Stream s1= l1.stream();
		Comparator<Integer> comp=(i1,i2)->i1.compareTo(i2);
		int min=l1.stream().min(comp).get();
		System.out.println("Min:"+min); 
		Integer max=l1.stream().max(comp).get();
		System.out.println("Max:"+max); 
	}
}