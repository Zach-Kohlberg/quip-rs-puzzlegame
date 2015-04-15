import java.util.Random;
import java.util.ArrayList;
import java.util.TreeMap;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class Puzzle implements Serializable {
    
    //Static constants
    private static Color[] colors = {new Color(255,0,0), new Color(0,255,0), new Color(0,0,255), new Color(0,153,153), new Color(255,0,255), new Color(255,255,0)};
    
    //Private instance data
    private TreeMap<Point,PuzzlePiece> board;
    private ArrayList<PuzzlePiece> pieces;
    private int numPlayers, width, height, numPieces;
    private int scale, gridWidth, gridHeight;
    private Point origin;
    
    //Test method
    public static void main(String[] args) {
        Puzzle p = new Puzzle(1,3,3,50);
    }
    
    public Puzzle(int numPlayers, int width, int height, int scale) {
        this.board = new TreeMap<Point,PuzzlePiece>();
        this.pieces = new ArrayList<PuzzlePiece>();
        this.numPlayers = numPlayers;
        this.width = width;
        this.height = height;
        this.numPieces = width*height;
        this.scale = scale;
        this.gridWidth = width*2 + width%2;
        this.gridHeight = height*2 + height%2;
        this.origin = new Point(scale/2,scale/2);
        
        Random randy = new Random();
        
        //My stupid method for creating the piece colors
        int[][][] stuffs = new int[width][height][4];
        ArrayList<int[]> tempList = new ArrayList<int[]>();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                for (int k = 0; k < 4; k++) {
                    stuffs[i][j][k] = -1;
                }
            }
        }
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                //Right side
                if (stuffs[(i+1)%width][j][2] == -1)
                    stuffs[i][j][0] = randy.nextInt(6);
                else
                    stuffs[i][j][0] = stuffs[(i+1)%width][j][2];
                //Top side
                if (stuffs[i][(j-1+height)%height][3] == -1)
                    stuffs[i][j][1] = randy.nextInt(6);
                else
                    stuffs[i][j][1] = stuffs[i][(j-1+height)%height][3];
                //Left side
                if (stuffs[(i-1+width)%width][j][0] == -1)
                    stuffs[i][j][2] = randy.nextInt(6);
                else
                    stuffs[i][j][2] = stuffs[(i-1+width)%width][j][0];
                //Bottom side
                if (stuffs[i][(j+1)%height][1] == -1)
                    stuffs[i][j][3] = randy.nextInt(6);
                else
                    stuffs[i][j][3] = stuffs[i][(j+1)%height][1];
                tempList.add(stuffs[i][j]);
            }
        }
        
        for (int i = 0; i < numPieces; i++) {
            int player = i%numPlayers;                                          //Alternates players
            int x = i%width + (width+1)/2;                                      //Fills one row at a time
            int y = i/height + (height+1)/2;
            int[] clr = tempList.remove(randy.nextInt(tempList.size()));        //Chooses a random color configuration
            pieces.add(new PuzzlePiece(player,x,y,clr));                        //Initializes the object
            board.put(new Point(x,y), pieces.get(i));                           //Adds piece to the board
        }
    }
    
    //Constructor for copying a puzzle
    private Puzzle(int numPlayers, int width, int height, int scale, ArrayList<PuzzlePiece> pieces) {
        this.numPlayers = numPlayers;
        this.width = width;
        this.height = height;
        this.numPieces = width*height;
        this.scale = scale;
        this.gridWidth = width*2 + width%2;
        this.gridHeight = height*2 + height%2;
        this.origin = new Point(scale/2,scale/2);
        this.pieces = new ArrayList<PuzzlePiece>();
        this.board = new TreeMap<Point,PuzzlePiece>();
        for (int i = 0; i < pieces.size(); i++) {
            PuzzlePiece p = pieces.get(i).copy();
            this.pieces.add(p);
            this.board.put(new Point(p.getX(),p.getY()), p);
        }
    }
    
    //Returns a copy of this puzzle
    public Puzzle copy() {
        return new Puzzle(numPlayers, width, height, scale, pieces);
    }
    
    //Getters for width, height, and scale
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public int getScale() {
        return scale;
    }
    
    //Checks if the puzzle is solved
    public boolean isSolved() {
        for (int i = 0; i < gridWidth; i++) {
            for (int j = 0; j < gridHeight; j++) {
                if (board.get(new Point(i,j)) != null) {
                    for (int x = i; x < i+width; x++) {
                        for (int y = j; y < j+height; y++) {
                            if (x > i) {
                                PuzzlePiece me = board.get(new Point(x,y)), them = board.get(new Point(x-1,y));
                                if (me == null || them == null || me.getColor(2) != them.getColor(0)) {
                                    return false;
                                }
                            }
                            if (x < i+width-1) {
                                PuzzlePiece me = board.get(new Point(x,y)), them = board.get(new Point(x+1,y));
                                if (me == null || them == null || me.getColor(0) != them.getColor(2)) {
                                    return false;
                                }
                            }
                            if (y > j) {
                                PuzzlePiece me = board.get(new Point(x,y)), them = board.get(new Point(x,y-1));
                                if (me == null || them == null || me.getColor(1) != them.getColor(3)) {
                                    return false;
                                }
                            }
                            if (y < j+height-1) {
                                PuzzlePiece me = board.get(new Point(x,y)), them = board.get(new Point(x,y+1));
                                if (me == null || them == null || me.getColor(3) != them.getColor(1)) {
                                    return false;
                                }
                            }
                        }
                    }
                    return true;
                }
            }
        }
        return true;
    }
    
    public void drawPiece(Graphics g, PuzzlePiece p, int x, int y) {
        int left = x, xMid = x+scale/2, right = x+scale, top = y, yMid = y+scale/2, bottom = y+scale;
        g.setColor(colors[p.getColor(0)]);
        int[] xPoints = {xMid, right, right};
        int[] yPoints = {yMid, top, bottom};
        g.fillPolygon(xPoints, yPoints, 3);
        g.setColor(colors[p.getColor(1)]);
        xPoints[1] = left;
        yPoints[2] = top;
        g.fillPolygon(xPoints, yPoints, 3);
        g.setColor(colors[p.getColor(2)]);
        xPoints[2] = left;
        yPoints[2] = bottom;
        g.fillPolygon(xPoints, yPoints, 3);
        g.setColor(colors[p.getColor(3)]);
        xPoints[2] = right;
        yPoints[1] = bottom;
        g.fillPolygon(xPoints, yPoints, 3);
        g.setColor(Color.BLACK);
        g.fillRect(x+scale/2-10,y+scale/2-10,20,20);
        g.setColor(Color.WHITE);
        g.drawString(""+(p.ID+1),x+scale/2-2,y+scale/2+3);
    }
    
    //Draws the puzzle in its entirety
    public void draw(Graphics g) {
        //Draws grid labels
        g.setColor(Color.GRAY);
        for (int i = 0; i < gridWidth; i++) {
            for (int j = 0; j < gridHeight; j++) {
                g.drawString(""+((char)('A'+i))+(gridHeight-j),origin.X+i*scale+scale/2-6,origin.Y+j*scale+scale/2+3);
            }
        }
        
        //Draws pieces
        for (int i = 0; i < pieces.size(); i++) {
            PuzzlePiece p = pieces.get(i);
            int x = origin.X + p.getX()*scale;
            int y = origin.Y + p.getY()*scale;
            drawPiece(g, p, x, y);
        }
        
        //Draws grid
        g.setColor(Color.BLACK);
        for (int i = 0; i <= gridWidth; i++) {
            g.drawLine(origin.X+i*scale,origin.Y,origin.X+i*scale,origin.Y+scale*gridHeight);
            if (i < gridWidth)
                g.drawString(""+((char)('A'+i)),origin.X+i*scale+scale/2-2,origin.Y+scale*gridHeight+scale/2);
        }
        for (int i = 0; i <= gridHeight; i++) {
            g.drawLine(origin.X,origin.Y+i*scale,origin.X+scale*gridWidth,origin.Y+i*scale);
            if (i < gridHeight)
                g.drawString(""+(gridHeight-i),origin.X+scale*gridWidth+scale/2,origin.Y+i*scale+scale/2+3);
        }
        
        //Draw any instructions
        //g.drawString("We can put some instructions over here.",origin.X,origin.Y+(gridHeight+1)*scale+scale/2);
    }
    
    //Converts pixel coordinates into a puzzle point
    public Point getPoint(int x, int y) {
        x = (x-origin.X)/scale;
        y = (y-origin.Y)/scale;
        return new Point(x,y);
    }
    
    //Sets the pixel location of the origin point (0,0)
    public void setOrigin(int x, int y) {
        origin = new Point(x,y);
    }
    
    //Applies the move to the current board state if it is valid and returns whether or not it succeeded. 
    public boolean makeMove(PuzzleMove m) {
        PuzzlePiece p = board.get(m.START);
        
        if (p != null && (m.ID == -1 || p.ID == m.ID) && board.get(m.END) == null && m.END.X > -1 && m.END.Y > -1 && m.END.X < gridWidth && m.END.Y < gridHeight) { //Validate move
            board.remove(m.START);
            board.put(m.END,p);
            p.setLocation(m.END);
            return true;
        }
        
        return false;
    }
}