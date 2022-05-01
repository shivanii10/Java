package java8.Exceptions;

//Throw is used to explicitly call an exception.

public class ThrowExample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			int a=190,b=20;
			if( a < b)
				throw new NullPointerException();
			else
				throw new ArithmeticException();
		}
		catch(NullPointerException e) {
			System.out.println("Iniside the null pointer exception");
		}
		catch(ArithmeticException e) {
			System.out.println("Iniside the null pointer exception");
		}
		
		

	}

}