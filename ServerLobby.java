import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/* Allows user to select chat mode and start the game when all clients have joined
 */
public class ServerLobby extends JFrame implements ActionListener {
    
    //Private variables
    private JTextArea playerList;
    private JLabel chatOption;
    
    //Constructor
    public ServerLobby(ActionListener actionListener) {
        super();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        JPanel panel = new JPanel(), playerPanel = new JPanel(), optionPanel = new JPanel();
        BoxLayout layout = new BoxLayout(panel, BoxLayout.X_AXIS);
        panel.setLayout(layout);
        layout = new BoxLayout(playerPanel, BoxLayout.Y_AXIS);
        playerPanel.setLayout(layout);
        layout = new BoxLayout(optionPanel, BoxLayout.Y_AXIS);
        optionPanel.setLayout(layout);
        panel.add(playerPanel);
        panel.add(optionPanel);
        
        playerPanel.add(new JLabel("Connected Players:"));
        playerList = new JTextArea(6,25);
        playerList.setEditable(false);
        playerPanel.add(playerList);
        optionPanel.add(new JLabel("Server Options:"));
        chatOption = new JLabel("None");
        optionPanel.add(chatOption);
        JButton normalButton = new JButton("Chat = Normal"), cycleButton = new JButton("Chat = Cycle"), groupButton = new JButton("Chat = Group");
        normalButton.addActionListener(actionListener);
        normalButton.addActionListener(this);
        optionPanel.add(normalButton);
        cycleButton.addActionListener(actionListener);
        cycleButton.addActionListener(this);
        optionPanel.add(cycleButton);
        groupButton.addActionListener(actionListener);
        groupButton.addActionListener(this);
        optionPanel.add(groupButton);
        JButton startButton = new JButton("Start");
        startButton.addActionListener(actionListener);
        optionPanel.add(new JLabel("Start the game:"));
        optionPanel.add(startButton);
        
        add(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    //Changes chatOption to reflect the chosen option
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if (action.equals("Chat = Normal"))
            chatOption.setText("Normal");
        else if (action.equals("Chat = Cycle"))
            chatOption.setText("Cycle");
        else if (action.equals("Chat = Group"))
            chatOption.setText("Group");
    }
    
    //Updates player statuses
    public void updatePlayers(String[] names, String[] statuses) {
        playerList.replaceRange("",0,playerList.getText().length());
        for (int i = 0; i < names.length; i++)
            playerList.append(names[i] + " - " + statuses[i] + "\n");
    }
}