package CoreConcepts;

public class StaticAndNonStaticExecution {

	//this (main) is a static method, it doesn't need object for it's execution.
	public static void main(String[] args) 
	{ 
		// TODO Auto-generated method stub
		StaticAndNonStatic se1= new StaticAndNonStatic() ;
		se1.name="ss";
		se1.college="JU";
		se1.displayData();
		StaticAndNonStatic se2= new StaticAndNonStatic() ;
		se2.name="sk";
		se2.displayData();
		StaticAndNonStatic se3= new StaticAndNonStatic() ;
		se3.college="JNU"; //all college values will have same data.
		se1.displayData();
		se2.displayData();
		se3.displayData();
		// static statement is present only once as only once a .class file 
		//can be created for a java program where as non-static is block is 
		// executed after creation of each object.
		//This is a static method does't need object for it's execution.
		StaticAndNonStatic.Data(); 
	}

}
