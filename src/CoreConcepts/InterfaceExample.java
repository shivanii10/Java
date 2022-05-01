package CoreConcepts;

interface Shop {       // interface
	
	public void order(); // abstract method
	void delivery(); // abstract method .. by default public abstract
	public default void offer() // can only be called on inherited class.
	{
		System.out.println("special discount super saver ");
	}
	
	// static methods can be called directly from interface,
	//there is no object dependency.
	
	public static void opening()
	{
		System.out.println("Shop opens at 8 in the morning :) ");
	}
}

class Garments implements Shop{
	
	public void order() {
		System.out.println("Place order for Garments.");
	}

	public void delivery() {
		System.out.println("Items will be delivered in a day.");
	}
	
}


class Fruit implements Shop{
	
	public void order() {
		System.out.println("Place order for Fruits required.");
	}

	public void delivery() {
		System.out.println("Items will be delivered in a 4-5 day.");
	}
	
	@Override
	public void offer()
	{
		System.out.println("Save more on fruits");
	}
	
}

public class InterfaceExample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Shop.opening();
		Fruit f=new Fruit();
		f.order();
		f.delivery();
		f.offer();
		
		Garments g =new Garments();
		g.order();
		g.delivery();
	}

}

