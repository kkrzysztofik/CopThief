package copthief.engine;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class BoardObject {
    private Constants.ObjectTypes type; //0 - wall, 1 - gateway
    private int sizeX, sizeY;
    private int posX, posY;
    private double movementChance;
    private double movementDirectionChange;
    private Constants.Direction movementDirection; //1 - up, 2 - down, 3 - right, 4 - left, 0 - stay

    protected LinkedList<Constants.Direction> moves;

    public BoardObject(Constants.ObjectTypes ttype, int sizeX, int sizeY, double movementChance, double movementDirectionChange) {

        if (ttype == Constants.ObjectTypes.GATEWAY) {
            this.type = ttype;
        } else {
            this.type = Constants.ObjectTypes.WALL;
        }

        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.movementChance = movementChance;
        this.movementDirectionChange = movementDirectionChange;

        RandomSingleton rn = RandomSingleton.getInstance();
        if(type == Constants.ObjectTypes.GATEWAY) {
            this.movementDirection = Constants.Direction.fromInteger(rn.nextInt(3));
        } else {
            this.movementDirection = Constants.Direction.fromInteger(rn.nextInt(4)+1);
        }

        this.moves = new LinkedList<Constants.Direction>();
    }

    public BoardObject(Constants.ObjectTypes ttype, int sizeX, int sizeY) {
        if (ttype == Constants.ObjectTypes.GATEWAY) {
            this.type = ttype;
        } else {
            this.type = Constants.ObjectTypes.WALL;
        }
        this.sizeX = sizeX;
        this.sizeY = sizeY;

        this.movementChance = 0.75;
        this.movementDirectionChange = 0.05;

        RandomSingleton rn = RandomSingleton.getInstance();
        if(type == Constants.ObjectTypes.GATEWAY) {
            this.movementDirection = Constants.Direction.fromInteger(rn.nextInt(3));
        } else {
            this.movementDirection = Constants.Direction.fromInteger(rn.nextInt(5));
        }

        this.moves = new LinkedList<Constants.Direction>();
    }

    public BoardObject(BoardObject toCopy) {
        this.type = toCopy.type;
        this.sizeX = toCopy.sizeX;
        this.posX = toCopy.posX;
        this.posY = toCopy.posY;
        this.sizeY = toCopy.sizeY;
        this.movementChance = toCopy.movementChance;
        this.movementDirectionChange = toCopy.movementDirectionChange;
        this.movementDirection = toCopy.movementDirection;

        this.moves = new LinkedList<Constants.Direction>();
    }

    public Constants.Direction getMove() {
        if(this.moves.size() > 0) {
            return this.moves.remove();
        } else {
            return Constants.Direction.STAY;
        }
    }

    public int getSizeX() {
        return this.sizeX;
    }

    public int getSizeY() {
        return this.sizeY;
    }

    public void setSize(int sizeX, int sizeY) {
        this.sizeY = sizeY;
        this.sizeX = sizeX;
    }

    public Constants.ObjectTypes getType() {
        return this.type;
    }

    public int getPosX(){
        return this.posX;
    }

    public int getPosY(){
        return this.posY;
    }

    public void setPos(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public void prepareMove(List<Board> stateList, int k) {
        RandomSingleton rn = RandomSingleton.getInstance();
        Board last = stateList.get(stateList.size() - 1);

        int futureX = posX,
            futureY = posY,
            boardSize = last.getSize();

        for (int i = 0; i < k; i++) {
            double d = rn.nextDouble();     // random value in range 0.0 - 1.0
            if (d <= this.movementChance) {
                d = rn.nextDouble();
                if (this.type == Constants.ObjectTypes.WALL) {
                    if (d <= this.movementDirectionChange) {
                        this.movementDirection = Constants.Direction.fromInteger(rn.nextInt(4)+1); //(0-3) + 1 = 1-4
                    }

                    switch(movementDirection) {
                        case UP:
                            if(futureY + this.sizeY >= boardSize) {
                                this.movementDirection = Constants.Direction.DOWN;
                                futureY -= 1;
                                break;
                            }
                            futureY += 1;
                            break;

                        case DOWN:
                            if(futureY <= 2) {
                                this.movementDirection = Constants.Direction.UP;
                                futureY += 1;
                                break;
                            }

                            futureY -= 1;
                            break;

                        case LEFT:
                            if(futureX <= 2) {
                                this.movementDirection = Constants.Direction.RIGHT;
                                futureX += 1;
                                break;
                            }

                            futureX -= 1;
                            break;

                        case RIGHT:
                            if(futureX + sizeX >= boardSize) {
                                this.movementDirection = Constants.Direction.LEFT;
                                futureX -= 1;
                                break;
                            }

                            futureX += 1;
                            break;
                    }

                    this.moves.push(this.movementDirection);

                } else {
                    if (d <= this.movementDirectionChange) {
                        this.movementDirection = Constants.Direction.fromInteger(rn.nextInt(2) + 1); //(0-1) + 1 = 1-2
                    }

                    switch (movementDirection) {
                        case RIGHT:
                            //todo: gate movement
                            break;
                        case LEFT:
                            break;
                    }
                    this.moves.push(this.movementDirection);
                }
            } else {
                this.moves.push(Constants.Direction.STAY);
            }
        }
    }
}
