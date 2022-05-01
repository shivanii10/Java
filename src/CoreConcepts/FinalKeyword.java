package CoreConcepts;


//final class Parent can't inherit final class.
class Parent
{
	final int x=100;  //constant
	//x=90; this will generate an error.
	
	//final public void status() 
	//if it was made final, can't have same method in derived class.
	public void status()
	{
		System.out.println("Inside parent class");
	}
}

class Child extends Parent
{
	
	public void status()
	{
		System.out.println("Inside Child class");
	}
}

public class FinalKeyword {


	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Parent p= new Parent();
		p.status();
		
		Child c= new Child();
		c.status();
		
	}

}
