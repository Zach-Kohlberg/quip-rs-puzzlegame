import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.io.PrintStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.EOFException;

/* Allows us to watch a recorded session play out in real time
 */
public class PlaybackGUI implements Runnable, ActionListener, ChangeListener, WindowListener {
    
    //Private variables
    private boolean running, paused;
    private String[] names;
    private Puzzle original, puzzle;
    private PlaybackWindow playbackWindow;
    private ArrayList<Long> times;
    private ArrayList<Object> objects;
    private long time, end;
    private int place, speed;
    private String fileName;
    
    //Constructor
    public PlaybackGUI(String fileName) {
        running = true;
        paused = false;
        names = null;
        puzzle = null;
        playbackWindow = null;
        times = new ArrayList<Long>();
        objects = new ArrayList<Object>();
        time = 0;
        end = 0;
        speed = 1;
        place = 0;
        this.fileName = fileName;
    }
    
    //Processes objects from recording
    public void processObject(Object input) {
        String className = "";
        try {
            className = input.getClass().getName();
        }
        catch (Exception e) {
            return;
        }
        
        if (className.equals("java.lang.String")) { //It's a text message
            try {
                playbackWindow.updateChat((String)input);
            }
            catch (Exception e) {
                System.err.println(e);
            }
        }
        else if (className.equals("Puzzle")) { //It's a new puzzle
            puzzle = ((Puzzle)input).copy();
            playbackWindow.setPuzzle(puzzle);
        }
        else if (className.equals("PuzzleMove")) { //It's a puzzle move
            puzzle.makeMove((PuzzleMove)input);
        }
        else {
            System.err.println("ERROR: Invalid data type: " + className);
        }
    }
    
    //Ends the thread and all subthreads
    public void end() {
        paused = false;
        running = false;
    }
    
    //Sets paused to true
    public void pause() {
        paused = true;
        playbackWindow.setPlay(">");
    }
    
    //Sets pause to false
    public void play() {
        paused = false;
        playbackWindow.setPlay("||");
    }
    
    //Change speed
    public void incSpeed() {
        speed = speed*2;
        playbackWindow.updateSpeed(speed);
    }
    public void decSpeed() {
        if (speed > 1) {
            speed = speed/2;
            playbackWindow.updateSpeed(speed);
        }
    }
    
    //Sleeps for 1/10th of a second
    public void sleep() {
        sleep(100);
    }
    
    //Sleeps for s milliseconds
    public void sleep(int s) {
        try {
            Thread.sleep(s);
        }
        catch (InterruptedException e) {}
    }
    
    //Moves to a certain point in the recording
    public void setTime(long t) {
        pause();
        sleep(10);
        place = 0;
        time = t;
        puzzle = original.copy();
        playbackWindow.setPuzzle(puzzle);
        playbackWindow.clearLog();
        while (place < times.size() && times.get(place).longValue() < time) {
            processObject(objects.get(place));
            place++;
        }
        playbackWindow.updateTimer(time);
        playbackWindow.redraw();
    }
    
    //Reacts to button presses
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("|<")) {
            setTime(0);
        }
        else if (e.getActionCommand().equals("<<")) {
            decSpeed();
        }
        else if (e.getActionCommand().equals("||")) {
            pause();
        }
        else if (e.getActionCommand().equals(">")) {
            play();
        }
        else if (e.getActionCommand().equals(">>")) {
            incSpeed();
        }
        else if (e.getActionCommand().equals(">|")) {
            setTime(end);
        }
    }
    
    //Set time when playback slider is moved
    public void stateChanged(ChangeEvent e) {
        int t = ((JSlider)e.getSource()).getValue();
        int max = ((JSlider)e.getSource()).getMaximum();
        setTime(t*end/max);
    }
    
    //Ends when window is closed
    public void windowClosed(WindowEvent e) {
        System.exit(0);
    }
    
    //Unused window listener methods
    public void windowActivated(WindowEvent e) {}
    public void windowClosing(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowOpened(WindowEvent e) {}
    
    //Runs as a thread
    public void run() {
        //Load all the stuff into a pair of arrays
        try {
            ObjectInputStream objectReader = new ObjectInputStream(new FileInputStream(fileName));
            objectReader.readLong();
            names = ((String[])objectReader.readObject());
            objectReader.readLong();
            original = ((Puzzle)objectReader.readObject());
            puzzle = original.copy();
            while (true) {
                long l = objectReader.readLong();
                Object o = objectReader.readObject();
                times.add(new Long(l));
                objects.add(o);
            }
        }
        catch (EOFException e) {}
        catch (Exception e) {
            System.err.println(e);
            System.exit(-1);
        }
        
        //Make the puzzle window
        end = times.get(times.size()-1);
        playbackWindow = new PlaybackWindow(this,names,puzzle,end);
        
        //Run the playback
        while (running) {
            while (paused) {
                sleep();
            }
            
            long t = System.currentTimeMillis();
            sleep();
            time += (System.currentTimeMillis()-t)*speed;
            
            //Update timer
            playbackWindow.updateTimer(time);
            
            //Check for next event
            if (place < times.size()) {
                while (place < times.size() && times.get(place).longValue() < time) {
                    processObject(objects.get(place));
                    place++;
                }
            }
            else {
                pause();
            }
            
            playbackWindow.redraw();
        }
    }
    
    //Main method to start program
    public static void main(String[] args) {
        String s;
        if (args.length == 0) {
            InputWindow in = new InputWindow();
            while (in.getText() == "") {
                try {
                    Thread.sleep(100);
                }
                catch (Exception e) {}
            }
            s = in.getText();
            ((Window)in).dispose();
        }
        else {
            s = args[0];
        }
        PlaybackGUI p = new PlaybackGUI(s);
        Thread t = new Thread(p);
        t.start();
    }
}