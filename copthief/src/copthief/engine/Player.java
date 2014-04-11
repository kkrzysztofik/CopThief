package copthief.engine;

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

    public Constants.Direction getMove() {
        if(this.moves.size() > 0) {
            return this.moves.remove();
        } else {
            return Constants.Direction.STAY;
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

    public void prepareMove(List<Board> stateList, int k) {
        //1 - up, 2 - down, 3 - right, 4 - left, 0 - stay
        //must fill moves list
    }
}