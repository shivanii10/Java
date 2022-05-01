package java8.PredefinedFunctionalInterfaces;
//Method referencing is used to refer already defined methods in any class,
//using ::  


class Test{
	public static void display() {
		for(int i=0;i<10;i++)
			System.out.println(i);
	}
}
public class MethodReferenceExample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// public void run(){
		// 
		// }
		/*Runnable r = ()-> {
			for(int i=0;i<10;i++)
				System.out.println(i);
		};*/
		
		 Test tt = new Test();
		// Runnable r = tt::display; // when the display method is non static 
		Runnable r = Test::display; // when the display method is static
		
		Thread t = new Thread(r);
		t.start();
	}

}
