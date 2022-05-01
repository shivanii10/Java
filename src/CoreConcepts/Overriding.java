package CoreConcepts;

//Method Overriding:
//process of supressing bas class over derived class when they have same methods.
// also called as run-time polymorphism.

class GrandFather{  
	String Surname;
	String name;
	GrandFather(){
		this.Surname="Kumar";
		this.name="Kishore";
	}
	
	public void displayData() {
		System.out.println("The Grandfather's name is "+name+" "+Surname);
	}
}

class Father extends GrandFather{  
	String Surname;
	String name;
	Father(){
		this.Surname="kapur";
		this.name="abhay";
	}
	
	public void displayData() {
		System.out.println("The Father's name is "+name+" "+Surname);
		System.out.println("The Father's name is "+super.name+" "+super.Surname);
	}
}

class Son extends Father{  
	String Surname;
	String name;
	Son(){
		this.Surname="kapoor";
		this.name="ajay";
	}
	
	public void displayData() {
		System.out.println("The son's name is "+name+" "+Surname);
	//using super keyword to get immediate parent class values if variable name are same.
		System.out.println("The son's name is "+super.name+" "+super.Surname);
}
}
public class Overriding {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		GrandFather gf =new GrandFather();
		gf.displayData();
		
		Father f =new Father();
		f.displayData();
		
		Son s = new Son();
		s.displayData();  // here it will access derived class values i.e. it's own values.
	}

}