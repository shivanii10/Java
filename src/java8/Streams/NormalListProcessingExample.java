package java8.Streams;

import java.util.*;
public class NormalListProcessingExample {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<Integer> data = new ArrayList();
		List<Integer> result = new ArrayList();
		data.add(10);
		data.add(4);
		data.add(3);
		data.add(12);
		data.add(26);
		System.out.println(data);
		
		for(int x: data) {
			if(x%4==0) {
				result.add(x+1);
			}
		}
		System.out.println("After increment to multiples of 4 are:");
		System.out.println(result);
	}

}