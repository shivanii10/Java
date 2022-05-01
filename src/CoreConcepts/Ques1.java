package CoreConcepts;
/* QUES1
 * Take an array and a number from user and 
 * if the elements of array are greater than the given number
 * calculate their sum and display the result in reverse order. eg: sum= 102
 * output should be 201.
 */
import java.util.Scanner;

public class Ques1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner sc = new Scanner(System.in);
		System.out.println("enter number of elements: ");
		int n=sc.nextInt();
		int[] nums=new int[n]; // declaring an array of size n

		System.out.println("enter "+n+" number : ");
		for(int i=0;i<n;i++)
		{ 		
			int a=sc.nextInt();
			nums[i]=a;
		}
		System.out.println("enter a number to check the larger ones and get sum : ");
		int larger=sc.nextInt();
		int sum=0;
		sc.close();
		for(int i=0;i<n;i++)
		{ 
			if(nums[i]>larger)
				sum+=nums[i];
		}
		
		System.out.println("sum is "+sum);
		// reversing sum
		int reverse=0,num=sum;
		 for(;num != 0; num /= 10) 
		 {
		      int digit = num % 10;
		      reverse = reverse * 10 + digit;
		 }
			System.out.println("Reversed sum is "+reverse);
	}
	
}
