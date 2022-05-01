package java8.Streams;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamMapExample {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<Integer> data = new ArrayList();
		Function<Integer,Integer> f = x->x+1; // applies to all
		data.add(10);
		data.add(4);
		data.add(3);
		data.add(12);
		data.add(26);
		System.out.println(data);
		Stream s= data.stream();
		List<Integer> result = (List<Integer>) s.map(f).collect(Collectors.toList());
		System.out.println(result);
		result.forEach(System.out::println);
	}

}
