//M. M. Kuttel 2024 mkuttel@gmail.com
// Simple Thread class to update the display of a text field
package medleySimulation;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class CounterDisplay implements Runnable {

    private FinishCounter results;
    private JLabel win;
    private JLabel w2;
    private JLabel w3;
    private JLabel gifLabel;  // JLabel to display the GIF
    private Color winningColor1;
    private Color winningColor2;
    private boolean isFlashingRed = false; // Track current color state
    private Clip clip; // For playing the music
    private ImageIcon gifIcon; // For displaying the GIF

    CounterDisplay(JLabel w, JLabel w2, JLabel w3, FinishCounter score, Color color1, Color color2) {
        this.win = w;
        this.w2 = w2;
        this.w3 = w3;
        this.gifLabel = gifLabel;
        this.results = score;
        this.winningColor1 = color1;
        this.winningColor2 = color2;
        initMusic(); // Initialize the music when creating the object
       // initGIF();   // Initialize the GIF when creating the object
    }

    public void initMusic() {
        try {
            // Load the music file (replace with the path to your .wav file)
            File musicFile = new File("party.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            clip = AudioSystem.getClip();
            clip.open(audioStream);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void playMusic() {
        if (clip != null && !clip.isRunning()) {
            clip.setFramePosition(0); // Rewind to the beginning of the audio
            clip.start(); // Play the audio
        }
    }

    public void stopMusic() {
        if (clip != null && clip.isRunning()) {
            clip.stop(); // Stop the audio
        }
    }


    public void run() { // This thread updates the display with flashing colors, plays music
        while (true) {
            if (results.isRaceWon()) {
                playMusic(); // Play the music when the race is won
                if (isFlashingRed) {  // Switching between colors every iteration
                    win.setForeground(winningColor2);
                    w2.setForeground(winningColor2);  
                    w3.setForeground(winningColor2);
                } else {
                    win.setForeground(winningColor1);
                    w2.setForeground(winningColor1);  
                    w3.setForeground(winningColor1);
                }

                // Get the winning teams and swimmers
                List<Integer> winningTeams = results.getWinningTeams();
                List<Integer> winners = results.getWinners();

                // Create a display message with the first three winners
                String team1 = "Position 1: Team " + winningTeams.get(0);
                String team2 = "Position 2: Team " + winningTeams.get(1);
                String team3 = "Position 3: Team " + winningTeams.get(2);

                // Set the display text
                win.setText(team1 + "\n");
                w2.setText(team2 + "\n");
                w3.setText(team3 + "\n");

                isFlashingRed = !isFlashingRed; // Toggle color state
            } else {
                // print this while the race is not won
                win.setForeground(Color.BLUE);
                win.setText("Results loading.....");
                w2.setForeground(Color.BLUE);
                w2.setText("Race in progress.....");
                w3.setForeground(Color.BLUE);
                w3.setText("Enjoy the race.....");
            }

            try {
                Thread.sleep(500); // Control the flashing speed (adjust milliseconds)
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}