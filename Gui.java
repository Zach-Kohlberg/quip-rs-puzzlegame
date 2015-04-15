/* Allows server and client to both use the PuzzleWindow
 */
public interface Gui {
    public void processMessage(String msg);
    public void processMove(PuzzleMove move);
}