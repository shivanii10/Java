package CoreConcepts;
class A{
	int x;
	int y;
	
	A(int x, int y ){
		System.out.println("Inside the A constructor");
		this.x=x;
		this.y=y;
		
	}
	public void show() {
		System.out.println("X = "+x+" Y = "+y);
	}
}

class B extends A{
	int x;
	int y;
	B(int x, int y){
		super(10,20); // used to call class A constructor
		System.out.println("Inside the B constructor");
		this.x=x;
		this.y=y;
		
	}
	public void show() {
		System.out.println("X = "+x+"Y = "+y);
	}
}

class C extends B{
	int x;
	int y;
	C(){
		super(50,60); // used to call class B constructor
		System.out.println("Inside the C constructor");
		x=50;
		y=60;
	}
	public void show() {
		System.out.println("X = "+x+"Y = "+y);
	}
}


public class SuperMethod {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		A a = new A(23,78);
		a.show();
		
		B b = new B(12,67);
		b.show();
		
		C c = new C();
		c.show();

	}

}