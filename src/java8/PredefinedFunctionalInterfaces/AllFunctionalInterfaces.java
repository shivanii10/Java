package java8.PredefinedFunctionalInterfaces;

import java.util.function.*;


public class AllFunctionalInterfaces {
	public static void main(String[] args) {
		Predicate<Integer> p=n->n%2==0;
		Function<Integer,Double> f=r->3.14*r*r;
		Consumer<Integer> c = n-> System.out.println("Entered Number is: "+n);
		Supplier<String> s= ()->"hello";
		
		
		System.out.println("is 4 even: "+p.test(4));
		System.out.println("Area of circle with 4 radi: "+f.apply(4));
		c.accept(4);
		System.out.println(s.get());
		
	}
}
