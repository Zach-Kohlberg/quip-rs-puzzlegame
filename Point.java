import java.io.Serializable;

/* Represents a point on the puzzle
 */
public class Point implements Comparable, Serializable {
    
    public final int X, Y;
    
    public Point(int x, int y) {
        X = x;
        Y = y;
    }
    
    public boolean equals(Object o) {
        Point p = (Point) o;
        return (X == p.X && Y == p.Y);
    }
    
    public int compareTo(Object o) {
        Point p = (Point) o;
        if (X > p.X)
            return 1;
        else if (X < p.X)
            return -1;
        else if (Y > p.Y)
            return 1;
        else if (Y < p.Y)
            return -1;
        else
             return 0;
    }
}