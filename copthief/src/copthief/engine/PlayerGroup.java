package copthief.engine;

import java.util.LinkedList;

public abstract class PlayerGroup implements Runnable {
    public LinkedList<Player> players;
    public abstract void run();
}
