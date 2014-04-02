package copthief.ai;

import copthief.engine.Board;
import copthief.engine.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RandomAI extends Player {
    public RandomAI(boolean ttype, int ttimeLimit) {
        this.type = ttype;
        this.timeLimit = ttimeLimit;
        this.moves = new LinkedList<Integer>();
    }

    public RandomAI(int ttimelimit) {
        this.type = false;
        this.timeLimit = ttimelimit;
        this.moves = new LinkedList<Integer>();
    }

    public RandomAI(boolean ttype) {
        this.type = ttype;
        this.timeLimit = 500;
        this.moves = new LinkedList<Integer>();
    }

    public RandomAI() {
        this.type = true;
        this.timeLimit = 500;
        this.moves = new LinkedList<Integer>();
    }

    public void prepareMove(List<Board> stateList, int k) {
        Random rand = new Random();

        for (int i=0; i<k; i++) {
            int value = rand.nextInt(5);
            this.moves.add(value);
        }

    }
}
