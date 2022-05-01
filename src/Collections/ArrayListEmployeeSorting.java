package Collections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

class EmpNameSort implements Comparator<Employee>
{

	@Override
	public int compare(Employee e1, Employee e2) {
		// TODO Auto-generated method stub
		return e1.name.compareTo(e2.name);
	}
	
}

public class ArrayListEmployeeSorting {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Employee e1 = new Employee("sunil","clerk");
		Employee e2 = new Employee("mahesh","manager");
		Employee e3 = new Employee("ashok","salesman");
		
		ArrayList<Employee>emps = new ArrayList();
		emps.add(e1);
		emps.add(e2);
		emps.add(e3);
		System.out.println("Before Sorting");
		System.out.println(emps);
		
		//Using Functional Interface Comparator
		System.out.println("Sorted Based On Names");
		EmpNameSort n=new EmpNameSort();
		Collections.sort(emps,n);
		System.out.println(emps);
		
		//Using Lambda Expression
		System.out.println("Sorted Based On Designation");
		Collections.sort(emps,(s1,s2)-> s1.desg.compareTo(s2.desg));
		System.out.println(emps);
	}
	
}
	
