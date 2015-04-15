import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;

/* Displays and allows user to interact with the chat and puzzle
 */
public class PuzzleWindow extends JFrame implements ActionListener, MouseListener {
    
    //Private variables
    private Gui gui;
    private JTextArea chatLog;
    private JTextField chatInput;
    private JLabel chatTimer;
    private JLabel[] chatNames;
    private JPanel chatPanel, chatNamePanel;
    private PuzzlePanel puzzlePanel;
    private Puzzle puzzle;
    private Point start;
    private int id;
    private boolean disabled;
    
    //Constructor
    public PuzzleWindow(Gui gui, String[] names, Puzzle puzzle, int id) {
        super();
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        getContentPane().setLayout(new GridLayout(1,2));
        
        this.gui = gui;
        this.puzzle = puzzle;
        start = new Point(-1,-1);
        this.id = id;
        disabled = false;
        
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
        chatInput = new JTextField("Type messages to your teammates here...", 32);
        chatInput.setFont(font);
        chatInput.addActionListener(this);
        /*chatInput.addMouseListener(
            new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    chatInput.setText("");
                }
            }
        );*/
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
        
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel,BoxLayout.Y_AXIS));
        chatNamePanel = new JPanel(new GridLayout(3,2));
        
        chatPanel.add(scroll);
        chatPanel.add(chatInput);
        chatPanel.add(chatNamePanel);
        chatNamePanel.add(chatTimer);
        for (int i = 0; i < 5; i++)
            chatNamePanel.add(chatNames[i]);
        
        puzzlePanel = new PuzzlePanel(puzzle);
        puzzlePanel.addMouseListener(this);
        
        getContentPane().add(chatPanel);
        getContentPane().add(puzzlePanel);
        pack();
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setVisible(true);
    }
    
    //Disables the chat & puzzle movement
    public void disableInteraction() {
        disabled = true;
    }
    
    //Updates the time
    public void updateTimer(long i) {
        chatTimer.setText(String.format("%02d:%02d", (i/60000), (i/1000%60)));
    }
    
    //Switch to a new puzzle
    public void setPuzzle(Puzzle p) {
        puzzle = p;
        puzzlePanel.setPuzzle(puzzle);
    }
    
    //Sends instant message to server, provided it isn't blank
    public void actionPerformed(ActionEvent e) {
        if (!disabled && !chatInput.getText().trim().equals("")) {
            gui.processMessage(chatInput.getText());
            chatInput.setText("");
        }
    }
    
    //Receives a chat message from the server
    public void updateChat(String msg) {
        chatLog.append(msg + "\n");
        chatLog.setCaretPosition(chatLog.getText().length());
    }
    
    //Redraws the puzzlePanel
    public void redraw() {
        puzzlePanel.repaint();
    }
    
    /* Removing Click & Drag to replace with click & click again
    //Marks a puzzle piece as held
    public void mousePressed(MouseEvent e) {
        start = puzzle.getPoint(e.getX(),e.getY());
    }
    
    //Attempts to place a puzzle piece
    public void mouseReleased(MouseEvent e) {
        if (!disabled) {
            gui.processMove(new PuzzleMove(id,start,puzzle.getPoint(e.getX(),e.getY())));
            start = new Point(-1,-1);
        }
    }
    */
    public void mousePressed(MouseEvent e) {
        if (!disabled) {
            if (start.X == -1) {
                //Marks a puzzle piece as held
                start = puzzle.getPoint(e.getX(),e.getY());
                puzzlePanel.setHighlight(start);
                if (start.X < 0 || start.Y < 0 || start.X >= puzzle.getWidth()+4 || start.Y >= puzzle.getHeight()+4) {
                    start = new Point(-1,-1);
                    puzzlePanel.setHighlight(null);
                }
            }
            else {
                //Attempts to place a puzzle piece
                gui.processMove(new PuzzleMove(id,start,puzzle.getPoint(e.getX(),e.getY())));
                start = new Point(-1,-1);
                puzzlePanel.setHighlight(null);
            }
        }
    }
    public void mouseReleased(MouseEvent e) {
        if (!disabled) {
            Point p = puzzle.getPoint(e.getX(),e.getY());
            if (start.X != -1 && (start.X != p.X || start.Y != p.Y)) {
                //Attempts to place a puzzle piece
                gui.processMove(new PuzzleMove(id,start,p));
                start = new Point(-1,-1);
                puzzlePanel.setHighlight(null);
            }
        }
    }
    //Unused MouseListener methods
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}