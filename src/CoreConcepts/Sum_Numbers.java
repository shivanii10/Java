package CoreConcepts;
import java.util.*;
public class Sum_Numbers {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int a;
		float b,c;
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter a number");
		a=sc.nextInt();
		System.out.println("Enter a number");
		b=sc.nextFloat();
		c=a+b;
		sc.close();
		System.out.println("Sum : "+c);
	}
}
