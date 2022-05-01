package CoreConcepts;

import java.util.Scanner;

public class If_example {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner sc = new Scanner(System.in);
		int a,b;
		a =sc.nextInt();
		b =sc.nextInt();
		sc.close();
		if (a>b) System.out.println("a is bigger");
		else System.out.println("b is bigger");
	}
}
