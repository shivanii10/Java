package CoreConcepts;
/* QUES3
 * Take an array from user and remove duplicates 
 * if elements are even calculate their sum.
 * if all are odd return -1
 */
import java.util.Scanner;

public class Ques3 {

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
		int sum=0;
		sc.close();
		for(int i=0;i<n;i++)
		{ 
			if(nums[i]%2==0)
				sum+=nums[i];
		}
		
		System.out.println("sum is "+sum);
	}
	
}
