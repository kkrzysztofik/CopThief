package copthief.ai;

import copthief.engine.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class CopAI extends PlayerGroup {
    public CopAI() {
        super();
    }

    public CopAI(PlayerGroup toCopy) {
        super(toCopy);
    }

    public CopAI(Constants.ObjectTypes groupType, int objectsCount) {
        super(groupType, objectsCount);
    }

    public void run() {
        RandomSingleton rand = RandomSingleton.getInstance();
//        Random rand = new Random();

        Iterator<Player> iterator = this.players.iterator();
        while (!Thread.currentThread().isInterrupted() && iterator.hasNext()) {
            Player plr = iterator.next();
            LinkedList<Constants.Direction> moves = new LinkedList<Constants.Direction>();

            for (int i = 0; i < k; i++) {
                int value = rand.nextInt(5);
                moves.add(Constants.Direction.fromInteger(value));
            }
            plr.setMoves(moves);
        }
    }
}
