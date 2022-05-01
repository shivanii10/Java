package java8.PredefinedFunctionalInterfaces;

import java.util.function.*;

public class FunctionTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Function<Integer,Double> f1 = r-> 3.141*r*r;
		Function<String,Integer> f2 = str->str.length();
		
		
		System.out.println("The area of circle with 6 radius is : "+ f1.apply(6));
		System.out.println("The length of hello is "+ f2.apply("hello"));

	}

}