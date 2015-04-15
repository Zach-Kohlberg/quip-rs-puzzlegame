import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.text.MaskFormatter;

/* Makes a popup window that asks the user for their name
 */
public class PopupNamePrompt extends JFrame {
    
    //Private variables
    private JFormattedTextField textField;
    
    //Constructor
    public PopupNamePrompt(ActionListener actionListener) {
        super();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        JPanel panel = new JPanel();
        BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        MaskFormatter formatter;
        try {
            formatter = new MaskFormatter("************");
            formatter.setInvalidCharacters(":()");
        }
        catch (Exception e) {
            formatter = null;
        }
        textField = new JFormattedTextField(formatter);
        textField.setColumns(20);
        textField.addActionListener(actionListener);
        JTextArea textArea = new JTextArea("Please type your name and press Enter (12 character limit):", 4, 20);
        textArea.setEditable(false);
        panel.setLayout(layout);
        panel.add(textArea);
        panel.add(textField);
        
        add(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
        
        textField.requestFocus();
    }
    
    //Returns the name entered by the user
    public String getText() {
        return textField.getText();
    }
    
    //Clears the text field
    public void clear() {
        textField.setText("");
    }
}