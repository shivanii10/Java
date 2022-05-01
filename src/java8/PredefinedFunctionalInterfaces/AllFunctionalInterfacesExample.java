package java8.PredefinedFunctionalInterfaces;

import java.util.Scanner;
import java.util.function.*;

class Student1{
	String name;
	int marks;
	String result;
	public Student1(String name, int marks) {
		
		this.name = name;
		this.marks = marks;
	}
	public Student1(Student1 apply) {
		// TODO Auto-generated constructor stub
	}
	
}

public class AllFunctionalInterfacesExample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Function<Student1,Student1> f = student1->{
			student1.result=null;
			if( student1.marks >=60)
				student1.result="Passed";
			else
				student1.result="Failed";
			
			return student1;
		};
		
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter student name: ");
		String name = sc.nextLine();
		System.out.println("Enter the student marks: ");
		int marks = sc.nextInt();
		Student1 s = new Student1(name,marks);
		
		Predicate<Integer> p = n->n>60; 
		Boolean result=p.test(s.marks);
		
		if (result)
		{
			Student1 s1= f.apply(s);
			Consumer<Student1> c = (e)->
			{
				System.out.println(e.name);
				System.out.println(e.marks);
				System.out.println(e.result);
			};
			c.accept(s1);
			
		}
		
		else 
			System.out.println("marks less than 60");
			
		
	}

}