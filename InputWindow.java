import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.MaskFormatter;
import java.util.ArrayList;
import java.io.File;

/* Makes a popup window that asks the user to choose a recording file
 */
public class InputWindow extends JFrame implements ActionListener {
    
    //Private variables
    private JComboBox<String> list;
    private String text;
    
    //Constructor
    public InputWindow() {
        super();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel panel = new JPanel();
        BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        
        File[] files = (new File(".")).listFiles();
        ArrayList<String> fNames = new ArrayList<String>();
        
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().length() > 5 && files[i].getName().substring(files[i].getName().length()-5).equals(".rec2"))
                fNames.add(files[i].getName());
        }
        
        JLabel label = new JLabel("Choose recording to play:");
        list = new JComboBox<>(fNames.toArray(new String[0]));
        JButton submit = new JButton("Play");
        submit.addActionListener(this);
        
        text = "";
        
        panel.setLayout(layout);
        panel.add(label);
        panel.add(list);
        panel.add(submit);
        
        add(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    //Gets the text
    public String getText() {
        return text;
    }
    
    //Submits the chosen file to the player
    public void actionPerformed(ActionEvent e) {
        if (list.getSelectedItem() != null) {
            text = (String)list.getSelectedItem();
        }
    }
}