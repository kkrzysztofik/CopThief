package copthief.engine;

import java.util.LinkedList;
import java.util.List;

public abstract class PlayerGroup implements Runnable {
    public LinkedList<Player> players;
    public List<Board> states;

    public int objectsCount;
    public int k;
    public Constants.ObjectTypes groupType;
    public volatile boolean stopExecution;

    public void setStates(List<Board> newStates) {
        this.states = newStates;
    }

    public LinkedList<Player> getPlayers() {
        return this.players;
    }

    public void setExecution(boolean flag) {
        this.stopExecution = flag;
    }
    public void cancel() { Thread.currentThread().interrupt(); }

    public PlayerGroup() {

    }

    public PlayerGroup(Constants.ObjectTypes groupType, int objectsCount) {
        this.objectsCount = objectsCount;
        this.groupType = groupType;
        this.players = new LinkedList<Player>();
    }

    public PlayerGroup(PlayerGroup toCopy) {
        this.objectsCount = toCopy.objectsCount;
        this.groupType = toCopy.groupType;

        this.players = new LinkedList<Player>();
        for(Player plr : toCopy.players) {
            Player tmpPlayer = new Player(plr);
            this.players.add(tmpPlayer);
        }

        this.states = new LinkedList<Board>();
        if(toCopy.states != null) {
            for (Board brd : toCopy.states) {
                Board tmpBoard = new Board(brd);
                this.states.add(tmpBoard);
            }
        }
    }

    public abstract void run();
}
