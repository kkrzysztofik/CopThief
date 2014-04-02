package copthief.engine;

import java.util.LinkedList;
import java.util.List;

public class Player {
    protected boolean type; //True - cop, false - thief
    protected int timeLimit; //in ms
    protected LinkedList<Integer> moves;
    protected int posX, posY;

    public Player(boolean ttype, int ttimeLimit) {
        this.type = ttype;
        this.timeLimit = ttimeLimit;
        this.moves = new LinkedList<Integer>();
    }

    public Player(int ttimelimit) {
        this.type = false;
        this.timeLimit = ttimelimit;
        this.moves = new LinkedList<Integer>();
    }

    public Player(boolean ttype) {
        this.type = ttype;
        this.timeLimit = 500;
        this.moves = new LinkedList<Integer>();
    }

    public Player() {
        this.type = false;
        this.timeLimit = 500;
        this.moves = new LinkedList<Integer>();
    }

    public Player(Player toCopy) {
        this.type = toCopy.type;
        this.timeLimit = toCopy.timeLimit;
        this.moves = new LinkedList<Integer>();
    }
    public int getMove() {
        if(this.moves.size() > 0) {
            return this.moves.remove();
        } else {
            return 0;
        }
    }

    public void setPos(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public int getPosX(){
        return this.posX;
    }

    public int getPosY(){
        return this.posY;
    }

    public boolean getType() {
        return this.type;
    }

    public void prepareMove(List<Board> stateList, int k) {
        //1 - up, 2 - down, 3 - right, 4 - left, 0 - stay
        //must fill moves list
    }

    public int getTimeLimit() {
        return this.timeLimit;
    }
}