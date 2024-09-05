//M. M. Kuttel 2024 mkuttel@gmail.com
// GridBlock class to represent a block in the grid.
// only one thread at a time "owns" a GridBlock - this must be enforced

package medleySimulation;


import java.util.concurrent.atomic.AtomicInteger;

public class GridBlock {

    private AtomicInteger isOccupied = new AtomicInteger(-1); // Use AtomicInteger for thread-safe operations
    private final boolean isStart;  //is this a starting block?
    private int[] coords; // the coordinate of the block.

    GridBlock(boolean startBlock) throws InterruptedException {
        isStart = startBlock;
    }

    GridBlock(int x, int y, boolean startBlock) throws InterruptedException {
        this(startBlock);
        coords = new int[]{x, y};
    }

    public int getX() {
        return coords[0];
    }

    public int getY() {
        return coords[1];
    }

    // Get a block
    public boolean get(int threadID) throws InterruptedException {
        if (isOccupied.compareAndSet(-1, threadID)) {
            return true; // Thread acquired the block
        } else {
            // Another thread already owns the block
            return false;
        }
    }

    // Release a block
    public void release() {
        isOccupied.set(-1);
    }

    // Is a block already occupied?
    public boolean occupied() {
        return isOccupied.get() != -1;
    }

    // Is a start block
    public boolean isStart() {
        return isStart;
    }
}