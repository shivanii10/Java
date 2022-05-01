package java8.Exceptions;

public class TryCatchFinally {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		int a = 10, b = 0, c;
		try {
			String s=null;
			s.equals("test");
			String str = "hai";
			str.charAt(8);
		}
		catch (NullPointerException e) {
			System.out.println("Inside the null pointer exception block...");
		} 
		catch (StringIndexOutOfBoundsException e) {
			System.out.println("Inside the stringindexout of bounds exception block...");
		} 
		
		try {
			c = a / b; // 10/0
			System.out.println("The value of c : " + c);
			System.out.println("After the division ");
			}
		catch (ArithmeticException e) {
			System.out.println("Inside the arthimetic exception block...");
		}
				
		finally {
			System.out.println("This finally block belongs to arthimetic ...");
		}
		System.out.println("After the finally block...");
	}

}