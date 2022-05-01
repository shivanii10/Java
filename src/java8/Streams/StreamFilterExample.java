package java8.Streams;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamFilterExample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<Integer> data = new ArrayList();
		Predicate<Integer> p1= x-> x%4==0; //test and then apply
		Predicate<Integer> p2= x-> x>=10;
		data.add(10);
		data.add(4);
		data.add(3);
		data.add(12);
		data.add(26);
		System.out.println(data);
		Stream  s = data.stream();
		System.out.println(s);
		List<Integer> result = (List<Integer>) s.filter(p1.and(p2)).collect(Collectors.toList());
		System.out.println(result);
	}

}