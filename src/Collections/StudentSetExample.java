package Collections;

import java.util.*;

public class StudentSetExample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Student s1 = new Student(1001,"Kumar");
		Student s2 = new Student(4519,"Sunil");
		Student s3 = new Student(2765,"Amar");
		Set<Student> studs = new HashSet();
		studs.add(s1);
		studs.add(s2);
		studs.add(s3);
		System.out.println("HashSet:");
		System.out.println(studs);
		System.out.println();
		
		//TreeSet is sorting based on names,
		//Method created in "ArrayListStudentSorting"
		//where class Student is present
		TreeSet<Student> ts = new TreeSet();
		ts.add(s1);
		ts.add(s2);
		ts.add(s3);
		System.out.println("TreeSet:");
		System.out.println(ts);
	}
}
