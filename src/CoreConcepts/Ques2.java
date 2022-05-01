package CoreConcepts;
/* QUES2
 * Take an array and a number from user and 
 * if the odd elements of array are less than the given number
 * calculate their sum.
 */
import java.util.Scanner;

public class Ques2 {

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
		System.out.println("enter a number to check the smaller and odd ones and get sum : ");
		int larger=sc.nextInt();
		int sum=0;
		sc.close();
		for(int i=0;i<n;i++)
		{ 
			if(nums[i]<=larger && nums[i]%2!=0)
				sum+=nums[i];
		}
		
		System.out.println("sum is "+sum);
	}
	
}
