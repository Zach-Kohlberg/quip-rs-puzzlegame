import java.io.Serializable;

/* Represents a piece on the puzzle board
 */
public class PuzzlePiece implements Serializable {
    public final int ID;
    private int x, y;
    //private int imgNo;
    private int[] colors;
    
    public PuzzlePiece(int id, int x, int y, int[] colors) {
        this.ID = id;
        this.x = x;
        this.y = y;
        this.colors = colors;
    }
    
    public PuzzlePiece copy() {
        return new PuzzlePiece(ID, x, y, colors);
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    /*public int getImg() {
        return imgNo;
    }*/
    
    public int getColor(int i) {
        return colors[i];
    }
    
    public void setLocation(Point p) {
        this.setLocation(p.X,p.Y);
    }
    
    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public Point getLocation() {
        return new Point(x,y);
    }
}