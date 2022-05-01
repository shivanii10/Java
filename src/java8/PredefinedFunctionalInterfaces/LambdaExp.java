package java8.PredefinedFunctionalInterfaces;

//functional interface : an interface that has only one abstract method
// for such interfaces we user lambda expressions.
//Eg. Runnable interface

@FunctionalInterface
interface addition{  
	
	public int add(int a,int b);
	
}

interface circle{  
	
	public double area(int r);
	
}

/*
//Runnable is an in built interface present in java,
//It contains only one function run()
class MyThread implements Runnable{

	@Override
	public void run() {
		// TODO Auto-generated method stub
		for(int i=0;i<11;i++)
			System.out.print(i);
		
	}
	
}*/
public class LambdaExp {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		addition obj=(a,b)->a+b;
		System.out.println(obj.add(12, 45));
		circle c=r->3.14*r*r;
		System.out.println(c.area(6));
		/*
		MyThread mt =new MyThread();
		mt.run();
		*/
		
		// directly using runnable interface without creating class and all
		//and overriding methods.
		Runnable r=()->{
			for(int i=0;i<11;i++)
			System.out.print(i);
			};
			// multi-threading concept
			Thread t =new Thread(r);
			t.start();

}
}
