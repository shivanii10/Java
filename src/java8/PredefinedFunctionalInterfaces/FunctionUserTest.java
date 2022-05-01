package java8.PredefinedFunctionalInterfaces;

import java.util.Scanner;
import java.util.function.Function;

class Student{
	String name;
	int marks;
	String result;
	public Student(String name, int marks) {
		
		this.name = name;
		this.marks = marks;
		//this.result=null;
	}
	
}

public class FunctionUserTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Function<Student,Student> f = student->{
			 student.result =null;
			if( student.marks >=80)
				student.result="Distinction";
			else if(student.marks >=60 && student.marks <80)
				student.result="First class";
			else
				student.result="Second Class";
			
			return student;
		};
		
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter student name: ");
		String name = sc.nextLine();
		System.out.println("Enter the student marks: ");
		int marks = sc.nextInt();
		Student s = new Student(name,marks);
		Student s1 = f.apply(s);
		System.out.println(s1.name);
		System.out.println(s1.marks);
		System.out.println(s1.result);

	}

}