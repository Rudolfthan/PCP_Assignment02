//M. M. Kuttel 2024 mkuttel@gmail.com
//Class to represent a swim team - which has four swimmers
package medleySimulation;

import medleySimulation.Swimmer.SwimStroke;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.CountDownLatch;


public class SwimTeam extends Thread {
	
	public static StadiumGrid stadium; //shared 
	private Swimmer [] swimmers;
	private int teamNo; //team number
	private AtomicInteger order;
	private CountDownLatch startingLatch;

	private CyclicBarrier startBarrier;
	public static final int sizeOfTeam=4;
	
	SwimTeam( int ID, FinishCounter finish,PeopleLocation [] locArr, CyclicBarrier barrier, CountDownLatch startingLatch) {
		this.teamNo=ID;
		this.order = new AtomicInteger(0);
		this.startBarrier = barrier;
		this.startingLatch = startingLatch;
		swimmers= new Swimmer[sizeOfTeam];
	    SwimStroke[] strokes = SwimStroke.values();  // Get all enum constants
		stadium.returnStartingBlock(ID);

		for(int i=teamNo*sizeOfTeam,s=0;i<((teamNo+1)*sizeOfTeam); i++,s++) { //initialise swimmers in team
			locArr[i]= new PeopleLocation(i,strokes[s].getColour());
	      	int speed=(int)(Math.random() * (3)+30); //range of speeds
			swimmers[s] = new Swimmer(i,teamNo,locArr[i],finish,speed,strokes[s], order, startBarrier, startingLatch); //hardcoded speed for now
		}
	}
	
	
	public void run() {
		for(int s=0;s<sizeOfTeam; s++) { //start swimmer threads
			swimmers[s].start();
		}
	}
}
	