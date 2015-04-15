/* Increments delay every second
 * If delay is incremented to 3, sets status to SLOW
 * If delay is incremented to 10, sets status to DISCONNECTED
 */
public class ConnectionTracker implements Runnable {
    
    //Public constants
    public final int CONNECTED=0, SLOW=1, DISCONNECTED=2;
    
    //Private variables
    private boolean running;
    private int status, delay;
    
    //Constructor
    public ConnectionTracker() {
        running = true;
        status = CONNECTED;
        delay = 0;
    }
    
    //Used to check status
    public int getStatus() {
        return status;
    }
    
    //Registers a ping
    public synchronized void ping() {
        delay = 0;
        status = CONNECTED;
    }
    
    //Ends the thread
    public void end() {
        running = false;
    }
    
    //Runs as its own thread
    public void run() {
        while (running) {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {}
            delay++;
            if (delay > 2)
                status = SLOW;
            else if (delay > 9)
                status = DISCONNECTED;
        }
    }
}