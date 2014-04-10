package copthief.engine;

import java.util.LinkedList;
import java.util.List;

public class Player {
    protected Constants.ObjectTypes type; //True - cop, false - thief
    protected int timeLimit; //in ms
    protected LinkedList<Constants.Direction> moves;
    protected int posX, posY;

    public Player(Constants.ObjectTypes ttype, int ttimeLimit) {
        if(ttype == Constants.ObjectTypes.COP) {
            this.type = ttype;
        } else {
            this.type = Constants.ObjectTypes.THIEF;
        }
        this.timeLimit = ttimeLimit;
        this.moves = new LinkedList<Constants.Direction>();
    }

    public Player(int ttimelimit) {
        this.type = Constants.ObjectTypes.THIEF;
        this.timeLimit = ttimelimit;
        this.moves = new LinkedList<Constants.Direction>();
    }

    public Player(Constants.ObjectTypes ttype) {
        if(ttype == Constants.ObjectTypes.COP) {
            this.type = ttype;
        } else {
            this.type = Constants.ObjectTypes.THIEF;
        }
        this.timeLimit = 500;
        this.moves = new LinkedList<Constants.Direction>();
    }

    public Player() {
        this.type = Constants.ObjectTypes.THIEF;
        this.timeLimit = 500;
        this.moves = new LinkedList<Constants.Direction>();
    }

    public Player(Player toCopy) {
        this.type = toCopy.type;
        this.timeLimit = toCopy.timeLimit;
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

    public void moveRandom(int posX, int posY, int boardSize) {
        RandomSingleton rnd = RandomSingleton.getInstance();
        Constants.Direction movementDirection = Constants.Direction.fromInteger(rnd.nextInt(4)+1);
        switch(movementDirection) {
            case UP:
                if(posY + 1 >= boardSize) {
                    posY -= 1;
                    break;
                }

                posY += 1;
                break;

            case DOWN:
                if(posY - 1 <= 2) {
                    posY += 1;
                    break;
                }

                posY -= 1;
                break;

            case LEFT:
                if(posX <= 2) {
                    posX += 1;
                    break;
                }

                posX -= 1;
                break;

            case RIGHT:
                if(posX + 1 >= boardSize) {
                    posX -= 1;
                    break;
                }

                posX += 1;
                break;
        }
        this.setPos(posX, posY);
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

    public int getTimeLimit() {
        return this.timeLimit;
    }
}