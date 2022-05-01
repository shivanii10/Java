package java8.Streams;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamSortExample {

	public static void main(String[] args) {
		
		Predicate<Student> p = stu -> stu.marks>60;
		Comparator<Student> c= (s1,s2)->s1.name.compareTo(s2.name);
		// TODO Auto-generated method stub
		Student s1 = new Student("Anil",60);
		Student s2 = new Student("Kishore",87);
		Student s3 = new Student("John",58);
		Student s4 =  new Student("Prakash",72);
		List<Student> students = new ArrayList();
		students.add(s1);
		students.add(s2);
		students.add(s3);
		students.add(s4);
		Stream s = students.stream();
		List<Student> results = 
				(List<Student>) s.filter(p)
		.sorted(c)
		.collect(Collectors.toList());
		System.out.println(results);

	}

}