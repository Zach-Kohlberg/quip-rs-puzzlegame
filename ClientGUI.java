import java.net.Socket;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/* Allows a user to interact with the client
 */
public class ClientGUI implements Runnable, Receiver, ActionListener, Gui {
    
    //Private variables
    private boolean running, set, serverReady, gameStarted;
    private ConnectionOut out;
    private ConnectionIn in;
    private ConnectionTracker tracker;
    private Socket socket;
    private String name;
    private String[] names;
    private Puzzle puzzle;
    private JFrame window;
    private int id;
    
    //Constructor
    public ClientGUI(Socket socket) {
        running = true;
        set = false;
        serverReady = false;
        gameStarted = false;
        out = null;
        in = null;
        tracker = null;
        this.socket = socket;
        name = "noName";
        names = null;
        puzzle = null;
        window = null;
        id = -1;
    }
    
    //Sets client components
    public void setComponents(ConnectionOut out, ConnectionIn in, ConnectionTracker tracker) {
        if (!set) {
            this.out = out;
            this.in = in;
            this.tracker = tracker;
            set = true;
        }
    }
    
    //Sends a text message to the server
    public void processMessage(String msg) {
        out.send(2,msg);
    }
    
    //Send a move to the server
    public void processMove(PuzzleMove move) {
        out.send(5,move);
    }
    
    //Processes objects from server
    public void receive(int flag, Object input) {
        String className = "";
        try {
            className = input.getClass().getName();
        }
        catch (Exception e) {
            return;
        }
        
        switch (flag) {
            case 0: //It's a ping from the server
                tracker.ping();
                break;
            case 1: //It's the client's ID assigned by the server
                if (className.equals("java.lang.Integer")) {
                    name = name + " (" + ((Integer)input) + ")";
                    id = ((Integer)input).intValue();
                }
                else
                    System.err.println("Protocol breach: flag=1\nExpected java.lang.Integer\nFound " + className);
                break;
            case 2: //It's a text message from the server or another client
                if (className.equals("java.lang.String")) {
                    try {
                        ((PuzzleWindow)window).updateChat((String)input);
                    }
                    catch (Exception e) {
                        System.err.println(e);
                    }
                }
                else
                    System.err.println("Protocol breach: flag=2\nExpected java.lang.String\nFound " + className);
                break;
            case 3: //It's the list of player names for the client
                if (className.equals("[Ljava.lang.String;")) {
                    //Store this for the creation of the puzzle window
                    names = ((String[])input);
                }
                else
                    System.err.println("Protocol breach: flag=3\nExpected [Ljava.lang.String;\nFound " + className);
                break;
            case 4: //It's a copy of the puzzle for the client
                if (className.equals("Puzzle")) {
                    if (gameStarted) { //Switch to new puzzle
                        puzzle = ((Puzzle)input);
                        ((PuzzleWindow)window).setPuzzle(puzzle);
                    }
                    else { //Store this for the creation of the puzzle window
                        puzzle = ((Puzzle)input);
                    }
                }
                else
                    System.err.println("Protocol breach: flag=4\nExpected Puzzle\nFound " + className);
                break;
            case 5: //It's a puzzle move from the server
                if (className.equals("PuzzleMove")) {
                    //Send this to the puzzle
                    puzzle.makeMove((PuzzleMove)input);
                }
                else
                    System.err.println("Protocol breach: flag=5\nExpected PuzzleMove\nFound " + className);
                break;
            case 6: //It's a signal to the client that the game is over
                //Disable the game
                ((PuzzleWindow)window).disableInteraction();
                break;
            case 7: //It's the client's name assigned by the server
                if (className.equals("java.lang.String")) {
                    if (name.equals("noName")) {
                        name = (String)input;
                    }
                    else {
                        System.err.println("Warning: Server attempted to set name after name was already set!");
                    }
                }
                else
                    System.err.println("Protocol breach: flag=7\nExpected java.lang.String\nFound " + className);
                break;
            case 8: //Time update from server
                if (className.equals("java.lang.Long")) {
                    if (gameStarted) {
                        ((PuzzleWindow)window).updateTimer((Long)input);
                    }
                }
                else
                    System.err.println("Protocol breach: flag=8\nExpected java.lang.Long\nFound " + className);
                break;
            case 9: //Time to close client
                end();
                break;
            default:
        }
    }
    
    //Gets text from the name prompt
    public void actionPerformed(ActionEvent e) {
        if (!((PopupNamePrompt)window).getText().trim().equals("")) {
            name = ((PopupNamePrompt)window).getText().trim();
            window.dispose();
        }
        else
            ((PopupNamePrompt)window).clear();
    }
    
    //Ends the thread and all subthreads
    public void end() {
        out.end();
        in.end();
        tracker.end();
        running = false;
        window.dispose();
    }
    
    //Sleeps for 1/10th of a second
    public void sleep() {
        try {
            Thread.sleep(100);
        }
        catch (InterruptedException e) {}
    }
    
    //Runs as a thread
    public void run() {
        //Wait for server to send name and ID
        out.send(1,"");
        while (name.equals("noName") || id == -1) {
            sleep();
        }
        
        //Display waiting screen until server sends the names, puzzle, and ready flag
        window = new PopupWaitingScreen(name.substring(0,name.indexOf(' ')));
        while (names == null || puzzle == null) {
            sleep();
        }
        window.dispose();
        
        //Display puzzle window & gui, switch to game mode
        window = new PuzzleWindow(this,names,puzzle,id);
        gameStarted = true;
        
        //Continue running in background
        while (running) {
            sleep();
            ((PuzzleWindow)window).redraw();
        }
    }
}