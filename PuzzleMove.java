import java.io.Serializable;

/* Represents a move for the puzzle
 */
public class PuzzleMove implements Serializable {
    public final int ID;
    public final Point START, END;
    
    public PuzzleMove(int id, Point start, Point end) {
        ID = id;
        START = start;
        END = end;
    }
}