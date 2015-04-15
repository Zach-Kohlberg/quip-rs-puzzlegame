import java.util.ArrayList;
import java.util.Arrays;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.PrintWriter;

/* Listens for connections from clients
 * When it receives a connection, it creates a ClientInterface for that client
 */
public class ServerConnectionListener implements Runnable {
    
    //Static variable for client names
    //private static ArrayList<String> names = new ArrayList<String>(Arrays.asList("Alfa", "Bravo", "Charlie", "Delta", "Echo", "Foxtrot", "Golf", "Hotel", "India", "Juliett", "Kilo", "Lima", "Mike", "November", "Oscar", "Papa", "Quebec", "Romeo", "Sierra", "Tango", "Uniform", "Victor", "Whiskey", "X-ray", "Yankee", "Zulu"));
    private static ArrayList<String> names = new ArrayList<String>(Arrays.asList("Alpha", "Beta", "Gamma", "Delta", "Epsilon", "Zeta", "Eta", "Theta", "Iota", "Kappa", "Lambda", "Mu", "Nu", "Xi", "Omicron", "Pi", "Rho", "Sigma", "Tau", "Upsilon", "Phi", "Chi", "Psi", "Omega"));
    
    //Private variables
    private boolean running, set;
    private int port;
    private ServerGUI gui;
    private int nextID;
    private int maxClients;
    
    //Constructor
    public ServerConnectionListener(int port, int maxClients) {
        running = true;
        set = false;
        gui = null;
        this.port = port;
        nextID = 0;
        this. maxClients = maxClients;
    }
    
    //Adds a reference to the gui
    public void setGUI(ServerGUI gui) {
        if (!set) {
            set = true;
            this.gui = gui;
        }
    }
    
    //Ends the thread
    public void end() {
        running = false;
    }
    
    //Creates a new ServerClientInterface and passes it to the server
    private synchronized void addClient(Socket s) {
        names.trimToSize();
        int n = (int)(Math.random()*names.size());
        ServerClientInterface scInterface = new ServerClientInterface(nextID, names.get(n), gui);
        names.remove(n);
        nextID++;
        ConnectionOut out = new ConnectionOut(s);
        ConnectionIn in = new ConnectionIn(s);
        ConnectionTracker tracker = new ConnectionTracker();
        
        scInterface.setComponents(out, in, tracker);
        in.setReceiver(scInterface);
        
        gui.addClient(scInterface);
        
        Thread t;
        t = new Thread(scInterface);
        t.start();
        t = new Thread(in);
        t.start();
        t = new Thread(out);
        t.start();
        t = new Thread(tracker);
        t.start();
        
        maxClients--;
    }
    
    //Runs as a thread
    public void run() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
        }
        catch (Exception e) {
            System.err.println("Error: Failed to initialize Connection Listener!");
        }
        while (running) {
            if (maxClients > 0) {
                try {
                    Socket s = serverSocket.accept();
                    addClient(s);
                }
                catch (Exception e) {
                    System.out.println(e);
                    System.err.println("Error: Error in Connection Listener!");
                }
            }
        }
    }
}