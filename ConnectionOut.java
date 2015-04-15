import java.net.Socket;
import java.io.ObjectOutputStream;

/* Sends objects to client/server
 * Pings the client/server every half-second to verify that it is still connected
 */
public class ConnectionOut implements Runnable {
    
    //Private variables
    private boolean running;
    private Socket socket;
    private ObjectOutputStream out;
    
    //Constructor
    public ConnectionOut(Socket socket) {
        running = true;
        this.socket = socket;
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
        }
        catch (Exception e) {
            System.out.println("Error: Could not establish output stream!");
        }
    }
    
    //Pings the client/server
    public void ping() {
        send(0, "");
    }
    
    //Sends an object to the client/server
    public synchronized void send(int flag, Object o) {
        try {
            out.writeInt(flag);
            out.flush();
            out.writeObject(o);
            out.flush();
        }
        catch (Exception e) {
            System.err.println(e);
        }
    }
    
    //Ends the thread
    public void end() {
        running = false;
    }
    
    //Runs as a thread
    public void run() {
        while (running) {
            try {
                Thread.sleep(500);
            }
            catch (InterruptedException e) {}
            ping();
        }
    }
}