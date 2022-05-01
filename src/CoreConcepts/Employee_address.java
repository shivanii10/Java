package CoreConcepts;

public class Employee_address {
	int Street_no;
	String House;
	
	Employee_address(String House,int Street_no)

	{
		this.Street_no=Street_no;
		this.House=House;
	}
	public void Display()
	{
		System.out.println(Street_no+" "+House);
	}

}
