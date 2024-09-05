package medleySimulation;

import java.awt.Color;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

public class Swimmer extends Thread {

    public static StadiumGrid stadium; // shared 
    private FinishCounter finish; // shared
    private GridBlock currentBlock;
    private Random rand;
    private int movingSpeed;
    private PeopleLocation myLocation;
    private int ID; // thread ID 
    private int team; // team ID
    private GridBlock start;
    private static CyclicBarrier b = new CyclicBarrier(10);
    private static CountDownLatch l= new CountDownLatch(1);
    private static AtomicInteger allowedOrder = new AtomicInteger(1); // Shared across all Swimmer instances

    public enum SwimStroke { 
        Backstroke(1,2.5,Color.black),
        Breaststroke(2,2.1,new Color(255,102,0)),
        Butterfly(3,2.55,Color.magenta),
        Freestyle(4,2.8,Color.red);

        private final double strokeTime;
        private final int order; // in minutes
        private final Color colour;   

        SwimStroke( int order, double sT, Color c) {
            this.strokeTime = sT;
            this.order = order;
            this.colour = c;
        }
  
        public int getOrder() { return order; }
        public Color getColour() { return colour; }
    }

    private final SwimStroke swimStroke;

    // Constructor
    Swimmer(int ID, int t, PeopleLocation loc, FinishCounter f, int speed, SwimStroke s) {
        this.swimStroke = s;
        this.ID = ID;
        this.movingSpeed = speed; // range of speeds for swimmers
        this.myLocation = loc;
        this.team = t;
        this.start = stadium.returnStartingBlock(team);
        this.finish = f;
        this.rand = new Random();
    }

    // Getters
    public int getX() { return currentBlock.getX(); }	
    public int getY() { return currentBlock.getY(); }
    public int getSpeed() { return movingSpeed; }
    public SwimStroke getSwimStroke() { return swimStroke; }

    // Swimmer enters stadium area
    public void enterStadium() throws InterruptedException {
        currentBlock = stadium.enterStadium(myLocation);  
        sleep(200);  // wait a bit at the door, look around
    }

    // Move to starting blocks
    public void goToStartingBlocks() throws InterruptedException {		
        int x_st = start.getX();
        int y_st = start.getY();
        while (currentBlock != start) {
            sleep(movingSpeed * 3);  // not rushing 
            currentBlock = stadium.moveTowards(currentBlock, x_st, y_st, myLocation);
        }
        System.out.println("-----------Thread " + this.ID + " at start " + currentBlock.getX() + " " + currentBlock.getY());
    }

    // Dive into the pool
    private void dive() throws InterruptedException {
        int x = currentBlock.getX();
        int y = currentBlock.getY();
        currentBlock = stadium.jumpTo(currentBlock, x, y - 2, myLocation);
    }

    // Swim there and back
    private void swimRace() throws InterruptedException {
        int x = currentBlock.getX();
        while (currentBlock.getY() != 0) {
            currentBlock = stadium.moveTowards(currentBlock, x, 0, myLocation);
            sleep((int) (movingSpeed * swimStroke.strokeTime)); // swim
        }
        while (currentBlock.getY() != (StadiumGrid.start_y - 1)) {
            currentBlock = stadium.moveTowards(currentBlock, x, StadiumGrid.start_y, myLocation);
            sleep((int) (movingSpeed * swimStroke.strokeTime));  // swim
        }
    }

    // After finishing the race
    public void exitPool() throws InterruptedException {		
        int bench = stadium.getMaxY() - swimStroke.getOrder();  // they line up
        int lane = currentBlock.getX() + 1; // slightly offset
        currentBlock = stadium.moveTowards(currentBlock, lane, currentBlock.getY(), myLocation);
        while (currentBlock.getY() != bench) {
            currentBlock = stadium.moveTowards(currentBlock, lane, bench, myLocation);
            sleep(movingSpeed * 3);  // not rushing 
        }
    }

    public void run() {
        try {
            // Swimmer arrives
            sleep(movingSpeed + rand.nextInt(10)); // Arriving takes a while
            myLocation.setArrived();
            enterStadium(); // Enter the stadium

            // Wait until it's this swimmer's turn
            synchronized (stadium) {  // Use the shared stadium as a lock object
                while (allowedOrder.get() != swimStroke.order) {
                    stadium.wait(); // Wait until it's this swimmer's turn
                }
            }

            goToStartingBlocks(); // Move to starting blocks
            b.await();

            // Notify other threads that this swimmer is ready or has completed a stage
            synchronized (stadium) {
                allowedOrder.incrementAndGet();  // Increment the order for the next swimmer
                stadium.notifyAll();  // Notify all waiting threads
            }
            
            //l.await();

            //b.await();
            // l.countDown();

            dive(); // Swimmer dives into the pool
            swimRace(); // Swimmer starts the race

            if (swimStroke.order == 4) {
                finish.finishRace(ID, team); // If it's the last swimmer, finish the race
            } else {
                exitPool(); // If not the last swimmer, exit the pool
            }

        } catch (InterruptedException e) {
            // Handle interruption (do nothing in this case)
        } catch (BrokenBarrierException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
