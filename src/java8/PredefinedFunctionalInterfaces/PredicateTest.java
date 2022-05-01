package java8.PredefinedFunctionalInterfaces;

import java.util.function.Predicate;

//Predicate Interface Concept : -Function Interface (applying lambda exp.)

/*
interface Prime {
	public boolean isEven(int n);
}
class PrimeImpl implements Prime{
	public boolean isEven(int n) {
		if(n%2==0)
			return true;
		else
			return false;
	}
	
	public boolean isPrime(int a) {
		boolean flag=true;
		for(int i=2;i<=a-1;i++) {
			if(a%i==0) {
				flag=false;
				break;
			}
		}
		return flag;
	}
	
	public boolean checkStringLength(String string) {
		if(string.length()>=10)
			return true;
		else
			return false;
	}
}
*/
public class PredicateTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Predicate<Integer> p1= n->n%2==0;
	
		
		Predicate<Integer> p2 = a-> {
				boolean flag=true;
				for(int i=2;i<=a-1;i++) {
					if(a%i==0) {
						flag=false;
						break;
					}
				}
				return flag;
			};
		
			Predicate<String> p3 = str->str.length()>10;
		
		//PrimeImpl p = new PrimeImpl();
		//System.out.println(p.isEven(10));
		//System.out.println(p.isEven(17));
		System.out.println("is 5 even: "+p1.test(5));
		System.out.println("is 9 prime: "+p2.test(9));
		System.out.println("length is >10 "+p3.test("hello"));
		System.out.println("length is >10 "+p3.test("this is java program"));
	}

}