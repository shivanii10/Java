package Collections;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;
class Employee extends Object{
	String name;
	String desg;
	
	@Override
	public String toString() {
		return "Employee [name=" + name + ", desg=" + desg + "]";
	}
	
	public Employee(String name, String desg) {
		super();
		this.name = name;
		this.desg = desg;
	}
	
	
	
}
public class ArrayListEmployeeExample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Employee e1 = new Employee("sunil","clerk");
		Employee e2 = new Employee("mahesh","manager");
		Employee e3 = new Employee("ashok","salesman");
		
		Consumer<Employee> con = emp->{
			System.out.println("Name: "+emp.name);
			System.out.println("Desig: "+emp.desg);
		};
		
		ArrayList<Employee> emps = new ArrayList();
		emps.add(e1);
		emps.add(e2);
		emps.add(e3);
		System.out.println(emps);
		
		// by using iterator class
		Iterator itr = emps.iterator();
		//System.out.println(itr.next());
		while(itr.hasNext()) {
			Employee e = (Employee) itr.next();
			System.out.println(e.name+" "+e.desg);
		}
		
		// using for loop
		System.out.println("traversing using for loop");
		for(Employee t: emps)
			System.out.println(t.name+" "+t.desg);
		
		// using forEach loop
		System.out.println("Traversing using forceach method.");
		emps.forEach(con);

	}

	
}