package java8.PredefinedFunctionalInterfaces;

import java.util.function.Consumer;

class Emp{
	int empid;
	String name;
	public Emp() {
		
	}
	public Emp(int empid, String name) {
		super();
		this.empid = empid;
		this.name = name;
	}
}

public class ConsumerTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Consumer<Integer> c = n-> System.out.println("The square is  :"+n*n);
		c.accept(9);
		
		Consumer<Emp> c1 = e1-> {
			System.out.println("Employee Id : "+e1.empid);
			System.out.println("Name : "+e1.name);
		};
			
		
		Emp e1 = new Emp(1001,"Kumar");
		Emp e2 = new Emp(1002,"Sunil");
		Emp e3 = new Emp(1003,"Anil");
		
		
		c1.accept(e3);
		
		
		
		

	}

}