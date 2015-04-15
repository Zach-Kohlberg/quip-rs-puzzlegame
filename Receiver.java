/* Interface for classes using ConnectionIn, ConnectionOut, and ConnectionTracker
 */
public interface Receiver {
    public void receive(int flag, Object input);
}