package CoreConcepts;

public class MethodOverloading {
	
	public void add(int a, int b)
	{
		int c=a+b;
		System.out.println("Int Sum : "+c);
	}
	
	
	public void add(int a, float b)
	{
		float c=a+b;
		System.out.println("Float Sum : "+c);
	}
	
	public void add(int a, int b, int c)
	{
		int d= a+b+c;
		System.out.println("Sum : "+d);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MethodOverloading m=new MethodOverloading();
		m.add(1,2);
		m.add(1,23.7f);
		m.add(1,2,3);

	}

}
