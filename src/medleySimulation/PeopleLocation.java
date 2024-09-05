//M. M. Kuttel 2024 mkuttel@gmail.com
// Class for storing  locations of people (swimmers only for now, but could add other types) in the simulation

package medleySimulation;

import java.awt.Color;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class PeopleLocation  { // this is made a separate class so don't have to access thread
	
	public static final GridBlock Entrance = null;
	private final int ID; //each person has an ID
	private Color myColor; //colour of the person
	
	private AtomicBoolean inStadium; //are they here?
	private AtomicBoolean  arrived; //have they arrived at the event?
	private GridBlock location; //which GridBlock are they on?
	
	//constructor
	PeopleLocation(int ID , Color c) {
		// Random rand = new Random();
		// float t = rand.nextFloat();
		// myColor = new Color(t, rand.nextFloat(), t);
		myColor = c;
		inStadium = new AtomicBoolean(false); //not in pool
		arrived = new AtomicBoolean(false); //have not arrived
		this.ID=ID;
	}
	
	//setter
	public synchronized void setInStadium(boolean in) {
		this.inStadium.set(in);
	}
	
	//getter and setter
	public synchronized boolean getArrived() {
		return arrived.get();
	}
	public synchronized void setArrived() {
		arrived.set(true);
	}

//getter and setter
	public synchronized GridBlock getLocation() {
		return location;
	}
	public synchronized void setLocation(GridBlock location) {
		this.location = location;
	}

	//getter
	public synchronized int getX() { return location.getX();}	
	
	//getter
	public synchronized int getY() {	return location.getY();	}
	
	//getter
	public synchronized int getID() {	return ID;	}

	//getter
	public synchronized boolean inPool() {
		return inStadium.get();
	}
	//getter and setter
	public synchronized Color getColor() { return myColor; }
	public synchronized void setColor(Color myColor) { this.myColor= myColor; }
}
