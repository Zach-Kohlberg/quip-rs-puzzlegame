import java.io.IOException;
import java.net.Socket;
import java.io.PrintWriter;
import java.util.Scanner;
import java.io.File;

/* Attempts to connect to the server
 * If successful, creates a ClientGUI, ClientIn, ClientOut, and ConnectionTracker
 * If not successful, creates an error popup
 */
public class Client {

    public static void main(String[] args) {
        //Default ip and port number
        String host = "localhost";
        int port = 2332;
        
        //Use other than default ip and port number
        if (args.length == 2) {
            host = args[0];
            try {
                int t = Integer.parseInt(args[1]);
                if (t > 1023 && t < 65536) {
                    port = t;
                }
                else {
                    System.err.println("ERROR: Port must be between 1024 and 65535");
                    System.exit(-1);
                }
            }
            catch (NumberFormatException e) {
                System.err.println("USAGE: java Client [HOST] [PORT]");
                System.exit(-1);
            }
        }
        else {
            try {
                Scanner settings = new Scanner(new File("clientSettings.txt"));
                host = settings.nextLine();
                port = settings.nextInt();
                settings.close();
            }
            catch (Exception e) {
                host = "localhost";
                port = 2332;
            }
        }
        
        //Make connection and create Client components
        try {
            Socket s = new Socket(host, port);
            ClientGUI gui = new ClientGUI(s);
            ConnectionOut out = new ConnectionOut(s);
            ConnectionIn in = new ConnectionIn(s);
            ConnectionTracker tracker = new ConnectionTracker();
            
            gui.setComponents(out, in, tracker);
            in.setReceiver(gui);
            
            Thread t;
            t = new Thread(gui);
            t.start();
            t = new Thread(out);
            t.start();
            t = new Thread(in);
            t.start();
            t = new Thread(tracker);
            t.start();
        }
        catch (Exception e) {
            System.err.println("Couldn't connect to server!");
            System.exit(-1);
        }
    }
}