package java8.PredefinedFunctionalInterfaces;

import java.util.function.Predicate;

public class PredicateCombine {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Predicate<Integer> p1 = n->n%2==0;
		Predicate<Integer> p2 = n->n>10;
		
		// Predicate Combinations
		System.out.println(p1.and(p2).test(10)); //and 
		System.out.println(p1.or(p2).test(4));   //or
		System.out.println(p1.negate().test(8));// negate
		
		// normally how we combine expressions 
		System.out.println(p1.test(10) && (p2).test(10)); 
		

	}

}