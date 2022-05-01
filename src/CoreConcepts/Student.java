package CoreConcepts;

public class Student {

 private int roll_no;
 private String name;
 private String p_name;
 int marks;
 String signature;
 
 public String getName() {
	return name;
}


public void setName(String name) {
	this.name = name;
}


public int getRoll_no() {
	return roll_no;
}


public void setRoll_no(int roll_no) {
	this.roll_no = roll_no;
}


 
 
 public void displayData()
 {
	 System.out.println("Name: "+name);
	 System.out.println("Roll no.: "+roll_no);
	 System.out.println("Marks: "+marks);
	 System.out.println("Signature: "+signature);
	 System.out.println("Parent's Name: "+p_name);
	 
 }
 
}
