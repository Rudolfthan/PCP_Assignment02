//M. M. Kuttel 2024 mkuttel@gmail.com
// MedleySimulation main class, starts all threads
package medleySimulation;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import javax.swing.*;



public class MedleySimulation {
    static final int numTeams = 10;

    static int frameX = 1500; // frame width
    static int frameY = 1500; // frame height
    static int yLimit = 400;
    static int max = 5;

    static int gridX = 50; // number of x grid points
    static int gridY = 120; // number of y grid points

    static SwimTeam[] teams; // array for team threads
    static PeopleLocation[] peopleLocations; // array to keep track of where people are
    static StadiumView stadiumView; // threaded panel to display stadium
    static StadiumGrid stadiumGrid; // stadium on a discrete grid

    static FinishCounter finishLine; // records who won
    static CounterDisplay counterDisplay; // threaded display of counter
    static CountDownLatch startingLatch;

    // Method to setup all the elements of the GUI
    public static void setupGUI(int frameX, int frameY) {
        // Frame initialize and dimensions
        JFrame frame = new JFrame("Swim medley relay animation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(frameX, frameY);

        JPanel g = new JPanel();
        g.setLayout(new BoxLayout(g, BoxLayout.Y_AXIS));
        g.setSize(frameX, frameY);

        stadiumView = new StadiumView(peopleLocations, stadiumGrid);
        stadiumView.setSize(frameX, frameY);
        g.add(stadiumView);

        // add text labels to the panel - this can be extended
        JPanel txt = new JPanel();
        txt.setLayout(new BoxLayout(txt, BoxLayout.LINE_AXIS));
        JLabel winner = new JLabel("");
        txt.add(winner);
        g.add(txt);
        // 
        JPanel winn2 = new JPanel();
        winn2.setLayout(new BoxLayout(winn2, BoxLayout.LINE_AXIS));
        JLabel win2 = new JLabel("");
        winn2.add(win2);
        g.add(winn2);

        //third winner
        JPanel winn3 = new JPanel();
        winn3.setLayout(new BoxLayout(winn3, BoxLayout.LINE_AXIS));
        JLabel win3 = new JLabel("");
        winn3.add(win3);
        g.add(winn3);

        // Video section - you can replace the path with your GIF or video sequence.
        JLabel videoLabel = new JLabel(new ImageIcon("sim.png"));
        videoLabel.setPreferredSize(new java.awt.Dimension(10, 200));
        g.add(videoLabel);

        counterDisplay = new CounterDisplay(winner, win2, win3, finishLine, Color.RED, Color.BLACK);

        // Add start and exit buttons
        JPanel b = new JPanel();
        b.setLayout(new BoxLayout(b, BoxLayout.LINE_AXIS));

        JButton startB = new JButton("Start");
		startB.setBackground(new Color(0,225,0));   
		startB.setForeground(Color.BLACK); // set text color
		startB.setFont(new Font("Arial", Font.BOLD, 16));
        
        startB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startingLatch.countDown();
                
            }
        });

        JButton endB = new JButton("Quit");
		endB.setBackground(Color.RED);
		endB.setFont(new Font("Arial", Font.BOLD, 16));
        endB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        b.add(startB);
        b.add(endB);
        g.add(b);

        frame.setLocationRelativeTo(null); // Center window on screen.
        frame.add(g); // add contents to window
        frame.setContentPane(g);
        frame.setVisible(true);
    }

    // Main method - starts it all
    public static void main(String[] args) throws InterruptedException {
        finishLine = new FinishCounter(); // counters for people inside and outside club
        startingLatch = new CountDownLatch(1);
        stadiumGrid = new StadiumGrid(gridX, gridY, numTeams, finishLine); // setup stadium with size
        SwimTeam.stadium = stadiumGrid; // grid shared with class
        Swimmer.stadium = stadiumGrid; // grid shared with class
        peopleLocations = new PeopleLocation[numTeams * SwimTeam.sizeOfTeam]; // four swimmers per team
        teams = new SwimTeam[numTeams];
        CyclicBarrier barrier = new CyclicBarrier(numTeams);
        for (int i = 0; i < numTeams; i++) {
            teams[i] = new SwimTeam(i, finishLine, peopleLocations, barrier, startingLatch);
        }
        setupGUI(frameX, frameY); // Start Panel thread - for drawing animation

        // start viewer thread
        Thread view = new Thread(stadiumView);
        view.start();

        // Start counter thread - for updating results
        Thread results = new Thread(counterDisplay);
        results.start();
        
        // start teams, which start swimmers.
        for (int i = 0; i < numTeams; i++) {
            teams[i].start();
        }
    }
}