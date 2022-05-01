package java8.Exceptions;

import java.util.Scanner;

class AgeExceptionTest {
	
	public void testAge(int age) {
		if(age < 0 || age > 100)
			throw new InvalidAgeException();
		else if(age >=0 && age <=17)
			throw new InAppropriateAgeException();
		else
			System.out.println("You are eligible for voting..");
			
	}

}
public class AgeExceptionTestImplementation {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		AgeExceptionTest t= new AgeExceptionTest();
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter the age: ");
		int age = sc.nextInt();
		try {
			t.testAge(age);
		}
		catch(InAppropriateAgeException e) {
			System.out.println(e);
		}
		catch(InvalidAgeException e) {
			System.out.println(e);
		}
	}

}