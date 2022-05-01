package java8.PredefinedFunctionalInterfaces;

import java.util.function.*;
public class SupplierTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Supplier<Double> s1= ()->Math.random();
		Supplier<String> s2= ()->"hello";
		Supplier<Integer> s3= ()->4;
		System.out.println(s1.get());
		System.out.println(s2.get());
		System.out.println(s3.get());
		

	}

}