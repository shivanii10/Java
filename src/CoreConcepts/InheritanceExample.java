package CoreConcepts;
// Is-a Relationship is provided in this code

class Area{  // Base Class
	int length;
	int breadth;
	Area(){
		length=1;
		breadth=2;
	}
	
	public void calArea() {
		System.out.println("The area is : "+(length*breadth));
	}
}

class Volume extends Area{  // Derived Class
	int height;
	Volume(){
		height=3;
	}
	public void calVolume() {
		System.out.println("The volume is : "+(length*breadth*height));
	}
}
/*
class Volume{
	int length;
	int breadth;
	int height;
	Volume(){
		length=1;
		breadth=2;
		height=3;
	}
	public void calArea() {
		System.out.println("The area is : "+(length*breadth));
	}
	public void calVolume() {
		System.out.println("The volume is : "+(length*breadth*height));
	}
}
*/
public class InheritanceExample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Area a =new Area();
		a.calArea();
		
		Volume v = new Volume();
		v.calArea();  // is called from Area
		v.calVolume();  // is from its own method called Volume
	}

}