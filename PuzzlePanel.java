import javax.swing.*;
import java.awt.Graphics;
import java.awt.Color;

/* This panel holds and draws the puzzle and allows the user to interact with the puzzle
 * The puzzle window or the client will be responsible for listening to the mouse activity in this panel
 */
public class PuzzlePanel extends JPanel {
    
    //Private variables
    private Puzzle puzzle;
    private Point highlight;
    
    public PuzzlePanel(Puzzle puzzle) {
        super();
        this.puzzle = puzzle;
        highlight = null;
        //setSize((puzzle.getWidth()+1)*puzzle.getScale(),(puzzle.getHeight()+1)*puzzle.getScale());
    }
    
    //Sets the new puzzle
    public void setPuzzle(Puzzle p) {
        puzzle = p;
    }
    
    //Sets the highlight
    public void setHighlight(Point p) {
        highlight = p;
    }
    
    //Draws on this panel
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        //Draw the puzzle
        puzzle.draw(g);
        
        //Draw highlight if it exists
        if (highlight != null) {
            g.setColor(Color.WHITE);
            int s = puzzle.getScale(), x = s/2+s*highlight.X, y = s/2+s*highlight.Y;
            g.drawRect(x,y,s,s);
            g.drawRect(x+1,y+1,s-2,s-2);
            g.drawRect(x-1,y-1,s+2,s+2);
        }
    }
}