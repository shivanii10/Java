package CoreConcepts;

public class Constructor {
	int age;
	String name;

	/*//It is a STATIC block
		static
		{
			System.out.println("Static Block");
		}
		
		//It is a NON-STATIC block
		{
			System.out.println("Non Static Block");	
		}*/
		Constructor()
		{
			name="benaam";
			age=17; //default value
			System.out.println("Zero parameter Constructor ...");
		}
		
		Constructor(String name)
		{	this();
			name=name;
			age=17; //default value
			System.out.println("One parameter Constructor ...");
		}
		Constructor(String n,int age) 
		// if we give same name to class variable and method variable.
		// higher preference is of method variable, so it can't distinguish age = age, doesn't know which
		// which one is class one and which one is method one, because of preference method variable name has the value but while printing the values it takes value from class variable which is empty that is why it is displaying null.
		{
			name=n;
			age=age; 
			System.out.println("Two parameter Constructor ...");
		}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Constructor c3 = new Constructor();
		Constructor c1 = new Constructor("hema");
		Constructor c2 = new Constructor("sushma",88);
		
/*		System.out.println("Order of execution: ");
		System.out.println("Static Block -> Non-Static Block -> Costructor");
		c1.age=78;
		System.out.println(c1.age+" "+c1.name); 
		System.out.println(c2.age+" "+c2.name);
		System.out.println(c3.age+" "+c3.name); */
	}


}
