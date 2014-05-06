package copthief.ai;

import copthief.engine.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RandomAI extends PlayerGroup {
    public RandomAI() {
        super();
    }

    public RandomAI(PlayerGroup toCopy) {
        super(toCopy);
    }

    public RandomAI(Constants.ObjectTypes groupType, int objectsCount) {
        super(groupType, objectsCount);
    }

    public void run() {
        RandomSingleton rand = RandomSingleton.getInstance();

        for(Player plr : this.players) {
            for (int i=0; i<k; i++) {
                int value = rand.nextInt(5);
                plr.setMove(Constants.Direction.fromInteger(value));
            }

        }
    }
}
