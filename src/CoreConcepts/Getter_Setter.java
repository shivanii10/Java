package CoreConcepts;

public class Getter_Setter {
	
	private int SecondNumber;
	private int FirstNumber;
	
	//we  use getter and setter methods for private variables of a class.
	
	public int getFirstNumber() {
		return FirstNumber;
	}
	public void setFirstNumber(int firstNumber) {
		FirstNumber = firstNumber;
	}
	public int getSecondNumber() {
		return SecondNumber;
	}
	public void setSecondNumber(int secondNumber) {
		SecondNumber = secondNumber;
	}
	
	// normal methods
	
	public void getData()
	{
		
	}
	
	public int getRemainder()
	{
		return 0;
	}

}
