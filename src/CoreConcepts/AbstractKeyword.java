package CoreConcepts;

//abstract classes are classes that contains at least 1 abstract method.

abstract class AbstractClass{ // abstract class
	
	public abstract void order(); // abstract method
	public abstract void delivery(); // abstract method
	
	public void demand()
	{
		System.out.println("we need supplies.");
	}
}

class Groceries extends AbstractClass{
	
	public void order() {
		System.out.println("Place order for groceries.");
	}

	public void delivery() {
		System.out.println("Items will be delivered in a day.");
	}
	
}


class Furniture extends AbstractClass{
	
	public void order() {
		System.out.println("Place order for Furniture required.");
	}

	public void delivery() {
		System.out.println("Items will be delivered in a 4-5 day.");
	}
	
}


public class AbstractKeyword {

		public static void main(String[] args) {
		// TODO Auto-generated method stub
		Furniture f=new Furniture();
		f.order();
		f.delivery();
		
		Groceries g =new Groceries();
		g.order();
		g.delivery();
			
	}

}
