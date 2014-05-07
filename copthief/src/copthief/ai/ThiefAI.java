package copthief.ai;

import copthief.engine.Constants;
import copthief.engine.Player;
import copthief.engine.PlayerGroup;
import copthief.engine.RandomSingleton;

import java.util.LinkedList;

public class ThiefAI extends PlayerGroup {
    public ThiefAI() {
        super();
    }

    public ThiefAI(PlayerGroup toCopy) {
        super(toCopy);
    }

    public ThiefAI(Constants.ObjectTypes groupType, int objectsCount) {
        super(groupType, objectsCount);
    }

    public void run() {
        RandomSingleton rand = RandomSingleton.getInstance();
        while(!Thread.currentThread().isInterrupted()) {
            for (Player plr : this.players) {
                LinkedList<Constants.Direction> moves = new LinkedList<Constants.Direction>();
                for (int i = 0; i < k; i++) {
                    int value = rand.nextInt(5);
                    moves.add(Constants.Direction.fromInteger(value));
                }
                plr.setMoves(moves);
            }
        }
    }
}
