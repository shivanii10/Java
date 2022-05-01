package CoreConcepts;

public class Employee {
	Employee_data ed; // has-a relationship
	
	
	public Employee() {
		super();
		this.ed= new Employee_data(9899,"sanjay");
	}

	public void displayData(Employee_address Ea) //Uses-a relationship
	{
		Ea.Display();
		System.out.println(ed.phone_no+" "+ed.name);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Employee e = new Employee();
		Employee_address Ea= new Employee_address("B-10",3);
		e.displayData(Ea);

	}

}
