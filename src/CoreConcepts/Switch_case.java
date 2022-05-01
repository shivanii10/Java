package CoreConcepts;

import java.util.Scanner;

public class Switch_case {

	public static void main(String[] args) {
		int a;
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter a number: ");
		a=sc.nextInt();
		sc.close();
		switch (a)
		{
		case 18: System.out.println("eligible to vote");
		break;
		case 14: System.out.println("not eligible to vote");
		break;
		case 8: System.out.println("not eligible to vote");
		break;
		default: System.out.println("not in list");
		}
		System.out.println("Enter a String: ");
		String s=sc.next();
		switch (s)
		{
		case "h": System.out.println("heyyyy");
		break;
		case "b": System.out.println("byee");
		break;
		case "hi": System.out.println("hiiiiiiiii");
		break;
		default: System.out.println("not in list");
		}
		
	}

}
