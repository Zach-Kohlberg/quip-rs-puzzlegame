import java.net.Socket;
import java.io.ObjectInputStream;

/* Receives objects for a receiver client or server
 */
public class ConnectionIn implements Runnable {
    
    //Private variables
    private boolean running, set;
    private Receiver receiver;
    private Socket socket;
    private ObjectInputStream in;
    
    //Constructor
    public ConnectionIn(Socket socket) {
        running = true;
        set = false;
        receiver = null;
        this.socket = socket;
        try {
            in = new ObjectInputStream(socket.getInputStream());
        }
        catch (Exception e) {
            System.err.println("Error: Could not establish input stream!");
        }
    }
    
    //Sets pointer to receiver
    public void setReceiver(Receiver receiver) {
        if (!set) {
            this.receiver = receiver;
            set = true;
        }
    }
    
    //Reads an object and passes it to the receiver
    public boolean read() {
        int flag;
        Object input;
        
        try {
            flag = in.readInt();
            input = in.readObject();
        }
        catch (Exception e) {
            flag = -1;
            input = null;
            System.err.println(e);
            return false;
        }
        
        receiver.receive(flag,input);
        return true;
    }
    
    //Ends the thread
    public void end() {
        running = false;
    }
    
    //Runs as a thread
    public void run() {
        while (running) {
            read();
        }
    }
}