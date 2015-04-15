/* Represents a single client to the server
 * Manages all interactions between that client and the server
 */
public class ServerClientInterface implements Runnable, Receiver {
    
    //Private variables
    private boolean running, set;
    private ConnectionOut out;
    private ConnectionIn in;
    private ConnectionTracker tracker;
    private String name;
    private final int ID;
    private ServerGUI gui;
    
    //Constructor
    public ServerClientInterface(int id, String name, ServerGUI gui) {
        running = true;
        set = false;
        out = null;
        in = null;
        tracker = null;
        this.name = name;
        this.ID = id;
        this.gui = gui;
    }
    
    //Adds references to interface components
    public void setComponents(ConnectionOut out, ConnectionIn in, ConnectionTracker tracker) {
        if (!set) {
            this.out = out;
            this.in = in;
            this.tracker = tracker;
            set = true;
        }
    }
    
    //Passes an object and flag along to the output process
    public void send(int flag, Object output) {
        out.send(flag, output);
    }
    
    //Return name
    public String getName() {
        return name;
    }
    
    //Return status
    public int getStatus() {
        return tracker.getStatus();
    }
    
    //Process objects from client
    public void receive(int flag, Object input) {
        String className = "";
        try {
            className = input.getClass().getName();
        }
        catch (Exception e) {
            return;
        }
        
        switch (flag) {
            case 0: //It's a ping from the client
                tracker.ping();
                break;
            case 1: //It's a request for the client's name
                //Send back the client's name and ID
                out.send(7,name);
                name = name + " (" + (ID+1) + ")";
                out.send(flag,new Integer(ID));
                break;
            case 2: //It's a text message from the client
                if (className.equals("java.lang.String")) {
                    //Send message to the gui
                    gui.processMessage(ID, name+": " + ((String)input));
                }
                else
                    System.err.println("Protocol breach: flag=2\nExpected java.lang.String\nFound " + className);
                break;
            case 5: //It's a puzzleMove from the client
                if (className.equals("PuzzleMove")) {
                    //Verify the move's ID and then send it on to the server
                    if (((PuzzleMove)input).ID == ID)
                        gui.processMove(((PuzzleMove)input));
                }
                else
                    System.err.println("Protocol breach: flag=5\nExpected PuzzleMove\nFound " + className);
                break;
            default:
                //Print out a protocol breach, the client shouldn't be sending this flag
        }
    }
    
    //Ends the thread and all subthreads
    public void end() {
        out.end();
        in.end();
        tracker.end();
        running = false;
    }
    
    //Runs as a thread
    public void run() {
        while (running) {
            
        }
    }
}