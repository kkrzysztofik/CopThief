package copthief.ai;

import copthief.engine.Board;
import copthief.engine.Constants;
import copthief.engine.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RandomAI extends Player {
    public RandomAI(Constants.ObjectTypes ttype, int ttimeLimit) {
        if(ttype == Constants.ObjectTypes.COP) {
            this.type = ttype;
        } else {
            this.type = Constants.ObjectTypes.THIEF;
        }
        this.timeLimit = ttimeLimit;
        this.moves = new LinkedList<Constants.Direction>();
    }

    public RandomAI(int ttimelimit) {
        this.type = Constants.ObjectTypes.THIEF;
        this.timeLimit = ttimelimit;
        this.moves = new LinkedList<Constants.Direction>();
    }

    public RandomAI(Constants.ObjectTypes ttype) {
        if(ttype == Constants.ObjectTypes.COP) {
            this.type = ttype;
        } else {
            this.type = Constants.ObjectTypes.THIEF;
        }
        this.timeLimit = 500;
        this.moves = new LinkedList<Constants.Direction>();
    }

    public RandomAI() {
        this.type = Constants.ObjectTypes.THIEF;
        this.timeLimit = 500;
        this.moves = new LinkedList<Constants.Direction>();
    }

    public RandomAI(Player toCopy) {
        this.type = toCopy.getType();
        this.timeLimit = toCopy.getTimeLimit();
        this.moves = new LinkedList<Constants.Direction>();
    }

    public void prepareMove(List<Board> stateList, int k) {
        Random rand = new Random();

        for (int i=0; i<k; i++) {
            int value = rand.nextInt(5);
            this.moves.add(Constants.Direction.fromInteger(value));
        }

    }
}
