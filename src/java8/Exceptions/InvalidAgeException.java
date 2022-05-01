package java8.Exceptions;

public class InvalidAgeException extends RuntimeException {
	
	public InvalidAgeException() {
		super("Age is either >100 or < 0");
	}

}