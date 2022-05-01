package java8.Exceptions;

class Ex2 {
	public void div(String s1, String s2) throws ArithmeticException {
		int n1 = Integer.parseInt(s1);
		int n2 = Integer.parseInt(s2);
		int n3 = n1 / n2;
		System.out.println("DIVISOIN = " + n3);
	}
}

public class ThrowsExample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Ex2 obj  = new Ex2();
		try {
		obj.div("10", "0");
		}
		catch(ArithmeticException e) {
			System.out.println(e);
		}

	}

}