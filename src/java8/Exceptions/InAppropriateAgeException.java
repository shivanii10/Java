package java8.Exceptions;

public class InAppropriateAgeException extends RuntimeException{
	
	public InAppropriateAgeException() {
		super("Age is less than 18....");
	}

}