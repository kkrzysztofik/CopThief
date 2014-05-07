package copthief.engine;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Player {
    protected Constants.ObjectTypes type;

    protected LinkedList<Constants.Direction> moves;

    protected int posX, posY;

    public Player(Constants.ObjectTypes ttype) {
        if(ttype == Constants.ObjectTypes.COP) {
            this.type = ttype;
        } else {
            this.type = Constants.ObjectTypes.THIEF;
        }
        this.moves = new LinkedList<Constants.Direction>();
    }

    public Player() {
        this.type = Constants.ObjectTypes.THIEF;
        this.moves = new LinkedList<Constants.Direction>();
    }

    public Player(Player toCopy) {
        this.type = toCopy.type;
        this.posY = toCopy.posY;
        this.posX = toCopy.posX;
        this.moves = new LinkedList<Constants.Direction>();
    }

    public synchronized Constants.Direction getMove() {
        Constants.Direction toRet;
        synchronized (this) {
            if (this.moves.size() > 0) {
                toRet = this.moves.remove();
            } else {
                toRet = Constants.Direction.STAY;
            }
        }
        return toRet;
    }

    public synchronized void setMoves(LinkedList<Constants.Direction> moves) {
        synchronized (this) {
            this.moves = new LinkedList<Constants.Direction>(moves);
        }
    }

    public void setPos(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public void move(Constants.Direction dir) {
        switch (dir) {
            case UP:
                this.setPos(posX, posY+1);
                break;
            case DOWN:
                this.setPos(posX, posY-1);
                break;
            case LEFT:
                this.setPos(posX-1, posY);
                break;
            case RIGHT:
                this.setPos(posX+1, posY);
                break;
        }
    }

    public int getPosX(){
        return this.posX;
    }

    public int getPosY(){
        return this.posY;
    }

    public Constants.ObjectTypes getType() {
        return this.type;
    }
}