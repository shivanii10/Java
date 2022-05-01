package CoreConcepts;

public class Loops {
	
	public static void main(String[] args) {
		int nums[]= {45,78,54,32};
		int a=0;
		System.out.println("WHILE LOOP");
		while (a<5) {
			a++;
			System.out.println("a = "+a+" inside while loop.");
		}
		
		System.out.println("FOR LOOP");
		for (int i=1;i<=5;i++)
		{
			System.out.println("i = "+i+" inside for loop.");
		}
		
		System.out.println("DO-WHILE LOOP");
		do
		{
			a++;
			System.out.println(a+" inside do-while loop.");
	
		}while(a<=5);
		
		System.out.println("FOR-EACH LOOP");
		for (int x: nums)
		{
			System.out.println(x);
		}
	}
}