import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/* Makes a popup window that asks the user for their name
 */
public class PopupWaitingScreen extends JFrame {
    
    //Constructor
    public PopupWaitingScreen(String name) {
        super();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        JPanel panel = new JPanel();
        JTextArea textArea = new JTextArea("Hello, you have been assigned the name \"" + name + ".\"\nPlease wait for the game to start...", 4, 20);
        textArea.setEditable(false);
        panel.add(textArea);
        
        add(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}