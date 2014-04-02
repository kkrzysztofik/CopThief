package copthief.engine;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class BoardObject {
    private boolean type; //0 - wall, 1 - gateway
    private int sizeX, sizeY;
    private int posX, posY;
    private double movementChance;
    private double movementDirectionChange;
    private int movementDirection; //1 - up, 2 - down, 3 - right, 4 - left, 0 - stay

    protected LinkedList<Integer> moves;

    public BoardObject(boolean ttype, int sizeX, int sizeY, double movementChance, double movementDirectionChange) {

        this.type = ttype;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.movementChance = movementChance;
        this.movementDirectionChange = movementDirectionChange;

        Random rn = new Random();
        if(type) {
            this.movementDirection = rn.nextInt(2);
        } else {
            this.movementDirection = rn.nextInt(4)+1;
        }

        this.moves = new LinkedList<Integer>();
    }

    public BoardObject(boolean ttype, int sizeX, int sizeY) {
        this.type = ttype;
        this.sizeX = sizeX;
        this.sizeY = sizeY;

        this.movementChance = 0.75;
        this.movementDirectionChange = 0.05;

        Random rn = new Random();
        if(type) {
            this.movementDirection = rn.nextInt(2);
        } else {
            this.movementDirection = rn.nextInt(4)+1;
        }

        this.moves = new LinkedList<Integer>();
    }

    public BoardObject(BoardObject toCopy) {
        this.type = toCopy.type;
        this.sizeX = toCopy.sizeX;
        this.sizeY = toCopy.sizeY;
        this.movementChance = toCopy.movementChance;
        this.movementDirectionChange = toCopy.movementDirectionChange;
        this.movementDirection = toCopy.movementDirection;

        this.moves = new LinkedList<Integer>();
    }

    public int getMove() {
        if(this.moves.size() > 0) {
            return this.moves.remove();
        } else {
            return 0;
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

    public boolean getType() {
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
        Random rn = new Random();
        Board last = stateList.get(stateList.size() - 1);

        int futureX = posX,
            futureY = posY,
            boardSize = last.getSize();

        for (int i = 0; i < k; i++) {
            double d = rn.nextDouble();     // random value in range 0.0 - 1.0
            if (d <= this.movementChance) {
                d = rn.nextDouble();
                if (!this.type) {
                    if (d <= this.movementDirectionChange) {
                        this.movementDirection = rn.nextInt(4) + 1; //(0-3) + 1 = 1-4
                    }

                    switch(movementDirection) {
                        case 1:
                            if(futureY + this.sizeY - 1 >= boardSize+1) {
                                this.movementDirection = 2;
                                futureY -= 1;
                                break;
                            }
                            futureY += 1;
                            break;

                        case 2:
                            if(futureY <= 2) {
                                this.movementDirection = 1;
                                futureY += 1;
                            }

                            futureY -= 1;
                            break;

                        case 3:
                            if(futureX <= 2) {
                                this.movementDirection = 4;
                                futureX += 1;
                            }

                            futureX -= 1;
                            break;

                        case 4:
                            if(futureX + sizeX - 1 >= boardSize+1) {
                                this.movementDirection = 3;
                                futureX -= 1;
                            }

                            futureX += 1;
                            break;
                    }

                    this.moves.push(this.movementDirection);

                } else {
                    if (d <= this.movementDirectionChange) {
                        this.movementDirection = rn.nextInt(2) + 1; //(0-1) + 1 = 1-2
                    }

                    switch (movementDirection) {
                        case 1:
                            //todo: gate movement
                            break;
                        case 2:
                            break;
                    }
                    this.moves.push(this.movementDirection);
                }
            } else {
                this.moves.push(0);
            }
        }
    }
}
