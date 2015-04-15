import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

/* Displays and allows user to interact with the chat and puzzle
 */
public class PlaybackWindow extends JFrame {
    
    //Private variables
    private PlaybackGUI gui;
    private JTextArea chatLog;
    private JLabel chatTimer, playbackSpeed;
    private JLabel[] chatNames;
    private JButton[] playbackButtons;
    private JSlider playbackSlider;
    private JPanel chatPanel, chatNamePanel, playbackButtonPanel;
    private PuzzlePanel puzzlePanel;
    private Puzzle puzzle;
    private Point start;
    private long time, end;
    
    //Constructor
    public PlaybackWindow(PlaybackGUI gui, String[] names, Puzzle puzzle, long end) {
        super();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new GridLayout(1,2));
        addWindowListener(gui);
        
        this.gui = gui;
        this.puzzle = puzzle;
        start = new Point(-1,-1);
        time = 0;
        this.end = end;
        
        Font font = new Font("Arial", Font.BOLD, 18);
        chatLog = new JTextArea(24,32);
        chatLog.setFont(font);
        chatLog.setEditable(false);
        chatLog.setLineWrap(true);
        chatLog.setWrapStyleWord(true);
        chatLog.setAutoscrolls(true);
        DefaultCaret caret = (DefaultCaret)chatLog.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        JScrollPane scroll = new JScrollPane(chatLog);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setAutoscrolls(true);
        chatTimer = new JLabel();
        chatTimer.setFont(font);
        chatNames = new JLabel[5];
        for (int i = 0; i < 5; i++) {
            String s = "noName (0)";
            if (i < names.length)
                s = names[i];
            chatNames[i] = new JLabel(s);
            chatNames[i].setFont(font);
        }
        playbackButtons = new JButton[5];
        playbackButtons[0] = new JButton("|<");
        playbackButtons[1] = new JButton("<<");
        playbackButtons[2] = new JButton("||");
        playbackButtons[3] = new JButton(">>");
        playbackButtons[4] = new JButton(">|");
        playbackSpeed = new JLabel("Speed: 1X");
        playbackSlider = new JSlider(0,30,0);
        playbackSlider.addChangeListener(gui);
        
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel,BoxLayout.Y_AXIS));
        chatNamePanel = new JPanel(new GridLayout(3,2));
        playbackButtonPanel = new JPanel();
        playbackButtonPanel.setLayout(new BoxLayout(playbackButtonPanel,BoxLayout.X_AXIS));
        
        chatPanel.add(scroll);
        chatPanel.add(chatNamePanel);
        chatPanel.add(playbackButtonPanel);
        chatNamePanel.add(chatTimer);
        for (int i = 0; i < 5; i++)
            chatNamePanel.add(chatNames[i]);
        for (int i = 0; i < 5; i++) {
            playbackButtonPanel.add(playbackButtons[i]);
            playbackButtons[i].addActionListener(gui);
        }
        playbackButtonPanel.add(playbackSpeed);
        playbackButtonPanel.add(playbackSlider);
        
        puzzlePanel = new PuzzlePanel(puzzle);
        
        getContentPane().add(chatPanel);
        getContentPane().add(puzzlePanel);
        pack();
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }
    
    //Change text on play/pause button
    public void setPlay(String s) {
        playbackButtons[2].setText(s);
    }
    
    //Changes the playback speed label
    public void updateSpeed(int i) {
        playbackSpeed.setText("Speed: " + i + "X");
    }
    
    //Updates the time
    public void updateTimer(long i) {
        time = i;
        playbackSlider.removeChangeListener(gui);
        playbackSlider.setValue((int)(playbackSlider.getMaximum()*(time/((double)end))));
        playbackSlider.addChangeListener(gui);
        chatTimer.setText(String.format("%02d:%02d", (i/60000), (i/1000%60)));
    }
    
    //Switch to a new puzzle
    public void setPuzzle(Puzzle p) {
        puzzle = p;
        puzzlePanel.setPuzzle(puzzle);
    }
    
    //Clears the chat log
    public void clearLog() {
        chatLog.setText("");
    }
    
    //Redraws the puzzlePanel
    public void redraw() {
        puzzlePanel.repaint();
    }
    
    //Adds a message to the chat log
    public void updateChat(String msg) {
        chatLog.append(msg + "\n");
        chatLog.setCaretPosition(chatLog.getText().length());
    }
}