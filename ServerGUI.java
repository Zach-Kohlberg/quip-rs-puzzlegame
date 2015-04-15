import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import java.io.PrintStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

/* Allows a user to interface with the server
 */
public class ServerGUI implements Runnable, ActionListener, Gui {
    
    //Static constants
    public static final int NONE=0, NORMAL=1, CYCLE=2, GROUP=3, LOBBY=1, PUZZLE=2;
    public static final String[] statusString = {"Connected", "Slow", "Disconnected"};
    
    //Private variables
    private boolean running, set;
    private ServerConnectionListener listener;
    private ArrayList<ServerClientInterface> clients;
    private int chatMode, serverMode, puzzleNumber;
    private JFrame window;
    private PuzzleWindow puzzleWindow;
    private String[] names;
    private Puzzle puzzle;
    private Random randy;
    private PrintStream fileStream;
    private ObjectOutputStream playbackRecorder;
    private long time, gameEnd;
    private boolean log, recording;
    
    //Constructor
    public ServerGUI() {
        running = true;
        set = false;
        listener = null;
        clients = new ArrayList<ServerClientInterface>();
        chatMode = NONE;
        serverMode = LOBBY;
        window = null;
        puzzleWindow = null;
        names = null;
        puzzle = null;
        randy = new Random();
        puzzleNumber = 0;
        gameEnd = 0;
        time = 0;
        
        String temp = (new Date(System.currentTimeMillis())).toString().replace(':','_');
        
        try {
            fileStream = new PrintStream(new File("log" + temp + ".txt"));
            record("Begin server log:");
            log = true;
        }
        catch (Exception e) {
            System.err.println("Failed to initialize log!");
            log = false;
        }
        
        try {
            playbackRecorder = new ObjectOutputStream(new FileOutputStream("record" + temp + ".rec2"));
            playbackRecorder.flush();
            recording = true;
        }
        catch (Exception e) {
            System.err.println("Failed to initialize recorder!");
            recording = false;
        }
    }
    
    //Adds a reference to the connection listener
    public void setListener(ServerConnectionListener listener) {
        if (!set) {
            this.listener = listener;
            set = true;
        }
    }
    
    //Adds a new client to the server
    public synchronized void addClient(ServerClientInterface client) {
        clients.add(client);
    }
    
    //Compatible with ClientPuzzleWindow, lets server send messages
    public void processMessage(String msg) {
        processMessage(-1,"Game: " + msg);
    }
    
    //Records object for playback
    public synchronized void recordObject(Object o) {
        try {
            playbackRecorder.writeLong(System.currentTimeMillis() - time);
            playbackRecorder.flush();
            playbackRecorder.writeObject(o);
            playbackRecorder.flush();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
    
    //Dispatches moves to the clients after they have been validated
    //Also allows server to make moves through its puzzle window and provides compatibility
    //Creates a new puzzle when the current one has been solved
    public void processMove(PuzzleMove move) {
        if (puzzle.makeMove(move)) {
            for (int i = 0; i < clients.size(); i++) {
                clients.get(i).send(5,move);
            }
            
            if (recording) 
                recordObject(move);
            
            if (puzzle.isSolved()) {
                //Inform players of their success, move on to new puzzle
                if (puzzleNumber == 0) {
                    processMessage("Good job, you've solved the practice puzzle! You will now have 20 minutes to solve as many 4x4 puzzles as possible. Good luck!");
                    gameEnd = System.currentTimeMillis()+1200000;
                    puzzleNumber++;
                }
                else {
                    processMessage("Puzzle " + puzzleNumber + " solved! Here's a new one.");
                    puzzleNumber++;
                }
                puzzle = new Puzzle(clients.size(),4,4,50);
                puzzleWindow.setPuzzle(puzzle);
                for (int i = 0; i < clients.size(); i++) {
                    clients.get(i).send(4,puzzle);
                }
                if (recording) 
                    recordObject(puzzle);
            }
        }
    }
    
    //Dispatches a message to all clients
    public synchronized void processMessage(int id, String msg) {
        switch (chatMode) {
            case NORMAL:
                for (int i = 0; i < clients.size(); i++) {
                    clients.get(i).send(2,msg);
                }
                break;
            case CYCLE:
                for (int i = 0; i < clients.size(); i++) {
                    if (id == -1 || id == i || (id+1)%clients.size() == i)
                        clients.get(i).send(2,msg);
                    else
                        clients.get(i).send(2,scramble(msg));
                }
                break;
            case GROUP:
                for (int i = 0; i < clients.size(); i++) {
                    if (id == -1 || (i <= clients.size()/2 && id <= clients.size()/2) || (i >= clients.size()/2 && id >= clients.size()/2))
                        clients.get(i).send(2,msg);
                    else
                        clients.get(i).send(2,scramble(msg));
                }
                break;
            default:
                //Do nothing, chat isn't on and this shouldn't happen
        }
        
        if (puzzleWindow != null)
            puzzleWindow.updateChat(msg);
        if (log)
            record(msg);
        if (recording) 
            recordObject(msg);
    }
    
    public String scramble(String msg) {
        String pre = msg.substring(0,msg.indexOf(":")+1);
        String suf = "";
        String jumble = "aaabbccddeeeeffgghhhiiijkllmmnnnoooppqrrrssstttuuvwwxyyz";
        for (int i = 0; i < msg.length(); i++) {
            if ((suf.length() == 0 || suf.charAt(suf.length()-1) != ' ') && Math.random() < .2) {
                suf = suf + " ";
            }
            else {
                suf = suf + jumble.charAt(randy.nextInt(jumble.length()));
            }
        }
        return pre+suf;
    }
    
    //Sleeps for 1/10th of a second
    public void sleep() {
        sleep(100);
    }
    
    //Sleeps for ms miliseconds
    public void sleep(int ms) {
        try {
            Thread.sleep(ms);
        }
        catch (InterruptedException e) {}
    }
    
    //Listens for the start button in the lobby window
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if (action.equals("Chat = Normal"))
            chatMode = NORMAL;
        else if (action.equals("Chat = Cycle"))
            chatMode = CYCLE;
        else if (action.equals("Chat = Group"))
            chatMode = GROUP;
        else if (action.equals("Start") && chatMode != NONE)
            serverMode = PUZZLE;
    }
    
    //Writes to the text log
    public synchronized void record(String s) {
        long t = System.currentTimeMillis() - time;
        long milli = t % 1000;
        long sec = t/1000 % 60;
        long min = t/60000 % 60;
        long hr = t/3600000;
        
        String o = s.format("[%02d:%02d:%02d.%03d] " + s, hr, min, sec, milli);
        fileStream.println(o);
    }
    
    //Ends the thread and all subthreads
    public void end() {
        if (set)
            listener.end();
        if (log)
            fileStream.close();
        if (recording) {
            try {playbackRecorder.close();}
            catch (Exception e) {}
        }
        for (int i = 0; i < clients.size(); i++)
            clients.get(i).end();
        running = false;
        window.dispose();
        puzzleWindow.dispose();
    }
    
    //Runs as a thread
    public void run() {
        //Wait for clients to connect, keep gui display up to date
        window = new ServerLobby(this);
        while (serverMode == LOBBY) {
            sleep();
            if (clients.size() > 0) {
                names = new String[clients.size()];
                String[] statuses = new String[clients.size()];
                for (int i = 0; i < names.length; i++) {
                    names[i] = clients.get(i).getName();
                    statuses[i] = statusString[clients.get(i).getStatus()];
                }
                ((ServerLobby)window).updatePlayers(names, statuses);
            }
        }
        
        //Start puzzle and send to all clients
        time = System.currentTimeMillis();
        puzzle = new Puzzle(names.length,3,3,50);
        record("Beginning experiment.");
        if (chatMode == NORMAL) {
            record("Chat Mode = NORMAL.");
        }
        else if (chatMode == CYCLE) {
            record("Chat Mode = CYCLE.");
        }
        else {
            record("Chat Mode = GROUP.");
        }
        for (int i = 0; i < clients.size(); i++) {
            record("Participant: " + clients.get(i).getName());
        }
        gameEnd = System.currentTimeMillis()+300000;
        for (int i = 0; i < clients.size(); i++) {
            clients.get(i).send(3,names);
            clients.get(i).send(4,puzzle);
        }
        
        if (recording) {
            recordObject(names);
            recordObject(puzzle);
        }
        
        listener.end();
        puzzleWindow = new PuzzleWindow(this,names,puzzle,-1);
        puzzleWindow.disableInteraction();
        
        sleep(1000);
        
        //Send instructions
        processMessage("Type into the text box at the bottom of the screen to send messages to the other players.");
        processMessage("You must work cooperatively with the other players to solve as many puzzles as you can.");
        processMessage("A puzzle is solved when all of the pieces form a square and the edges of adjacent pieces are the same color.");
        if (log) {
            record("Game: You control the pieces of the puzzle with your number on them. You are [Name] ([ID]).");
        }
        if (recording) {
            recordObject("Game: You control the pieces of the puzzle with your number on them. You are [Name] ([ID]).");
        }
        for (int i = 0; i < clients.size(); i++) {
            clients.get(i).send(2, "Game: You control the pieces of the puzzle with your number on them. You are " + clients.get(i).getName() + ".");
        }
        processMessage("You can move pieces you control by clicking and dragging them to an empty space on the grid.");
        processMessage("You cannot move the pieces of another player.");
        processMessage("You have five minutes to complete this practice puzzle, after which you will have twenty minutes to complete as many 4x4 puzzles as you can.");
        processMessage("Good luck!");
        
        //Continue to run and monitor stuff
        while (running) {
            sleep();
            puzzleWindow.redraw();
            long t = gameEnd-System.currentTimeMillis();
            puzzleWindow.updateTimer(t);
            //Respond to timer running out
            if (gameEnd < System.currentTimeMillis()) { //Time is up for the practice puzzle
                if (puzzleNumber == 0) {
                    processMessage("Sorry, you're out of time to solve the practice puzzle. You will now have 20 minutes to solve as many 4x4 puzzles as possible. Good luck!");
                    gameEnd = System.currentTimeMillis()+1200000;
                    puzzleNumber++;
                    puzzle = new Puzzle(clients.size(),4,4,50);
                    puzzleWindow.setPuzzle(puzzle);
                    for (int i = 0; i < clients.size(); i++) {
                        clients.get(i).send(4,puzzle);
                    }
                    if (recording) 
                        recordObject(puzzle);
                }
                else { //Time is up for the main event
                    processMessage("Time is up! You have cooperatively solved " + (puzzleNumber-1) + " 4x4 puzzle(s)!");
                    processMessage("Thank you for your participation.");
                    for (int i = 0; i < clients.size(); i++) {
                        clients.get(i).send(6,"");
                    }
                    record("Experiment terminated.");
                    sleep(10000);
                    for (int i = 0; i < clients.size(); i++) {
                        clients.get(i).send(9,"");
                    }
                    end();
                }
            }
            else {
                for (int i = 0; i < clients.size(); i++) {
                    clients.get(i).send(8,t);
                }
            }
            if (clients.size() > 0) {
                names = new String[clients.size()];
                String[] statuses = new String[clients.size()];
                for (int i = 0; i < names.length; i++) {
                    names[i] = clients.get(i).getName();
                    statuses[i] = statusString[clients.get(i).getStatus()];
                }
                ((ServerLobby)window).updatePlayers(names, statuses);
            }
        }
    }
}