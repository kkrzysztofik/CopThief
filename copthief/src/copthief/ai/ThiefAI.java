package copthief.ai;

import copthief.engine.Constants;
import copthief.engine.Player;
import copthief.engine.PlayerGroup;
import copthief.engine.RandomSingleton;

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

        for(Player plr : this.players) {
            for (int i=0; i<k; i++) {
                int value = rand.nextInt(5);
                plr.setMove(Constants.Direction.fromInteger(value));
            }

        }
    }
}
