import java.util.Scanner;
public class Neon {

	public static void main(String[] args) {
		
		Scanner sc=new Scanner(System.in);
		System.out.println("ENTER A NUMBER:");
		int n=sc.nextInt();
		int square=n*n;
		int sum=0,a=0;
		
		while(square!=0) {
			a=square%10;
			sum+=a;
			square/=10;
		}
		
		if(sum==n)
			System.out.println(n+" IS NEON number");
		else
			System.out.println(n+" IS NOT NEON number");

	}

}

