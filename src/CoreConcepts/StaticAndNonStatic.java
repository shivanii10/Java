package CoreConcepts;

public class StaticAndNonStatic {

	String name;
	
	// static variable - it contains same value for all the objects created for a class.
	static String college;  

	//It is a STATIC block
	static
	{
		System.out.println("Static Block");
		System.out.println(" ");
	}
	
	//It is a NON-STATIC block
	{
		System.out.println("Non Static Block");	
	}
	
	public void displayData()
	{
		System.out.println(" ");
		System.out.println("Name: "+name);
		System.out.println("College: "+college);
	}

	public static void Data()
	{
		System.out.println(" ");
		System.out.println("Static Block : ");
		System.out.println("It is executed whenever .class file is created.");
		System.out.println("Non Static Block : ");
		System.out.println("It is executed whenever object of a class is created.");
	}

}
