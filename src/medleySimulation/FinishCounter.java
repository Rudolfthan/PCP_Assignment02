// Simple class to record when someone has crossed the line first and wins
package medleySimulation;

import java.util.ArrayList;
import java.util.List;

public class FinishCounter {
    private int numberOfWinners = 3; // How many winning teams to track
    private List<Integer> winners; // List to store winning swimmer IDs
    private List<Integer> winningTeams; // List to store winning team IDs
    
    FinishCounter() { 
        winners = new ArrayList<>();
        winningTeams = new ArrayList<>();
    }
        
    // This is called by a swimmer when they touch the finish line
    public synchronized void finishRace(int swimmer, int team) {
        // If we haven't reached the number of winners we're tracking
        if (winners.size() < numberOfWinners) {
            winners.add(swimmer);
            winningTeams.add(team);
        }
    }
    
    // Check if the race is complete (all winning slots are filled)
    public boolean isRaceWon() {
        return winners.size() >= numberOfWinners;
    }

    // Get the list of winning swimmers
    public synchronized List<Integer> getWinners() { 
        return new ArrayList<>(winners); 
    }
    
    // Get the list of winning teams
    public synchronized List<Integer> getWinningTeams() { 
        return new ArrayList<>(winningTeams); 
    }
}
