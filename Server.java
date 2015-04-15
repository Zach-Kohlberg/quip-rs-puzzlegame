/* Sets up the server
 * Creates a ServerGUI and a ServerConnectionListener
 */
public class Server {
    
    public static void main(String[] args) {
        //Default port number
        int port = 2332;
        
        //Use other than default port number
        if (args.length == 1) {
            try {
                port = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException e) {
                System.err.println("USAGE: java Server [PORT]");
                System.exit(-1);
            }
        }
        
        //Create the GUI and ConnectionListener
        ServerGUI gui = new ServerGUI();
        ServerConnectionListener listener = new ServerConnectionListener(port,5);
        
        gui.setListener(listener);
        listener.setGUI(gui);
        
        Thread t;
        t = new Thread(gui);
        t.start();
        t = new Thread(listener);
        t.start();
    }
}