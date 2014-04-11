package copthief.engine;

import java.io.Console;
import java.util.LinkedList;
import java.util.List;

public class Board {
    private int size;
    public List<BoardObject> objects;
    public List<Player> players; //cops and thiefs
    private Constants.ObjectTypes[][] board; //1 - wall, 2 - gateway, 3 - thief, 4 - cop, 0 - empty

    public Board(int size, List<BoardObject> objects, List<Player> players){
        this.size = size+2;
        this.objects = objects;
        this.players = players;
        this.board = new Constants.ObjectTypes[this.size][this.size];

        for(int i = 0; i < this.size; i++) {
            for(int j = 0; j < this.size; j++) {
                board[i][j] = Constants.ObjectTypes.EMPTY;
            }
        }

        for(int i = 0; i < this.size; i++) {
            board[i][0] = Constants.ObjectTypes.WALL;
            board[i][size+1] = Constants.ObjectTypes.WALL;
            board[0][i] = Constants.ObjectTypes.WALL;
            board[0][size+1] = Constants.ObjectTypes.WALL;
        }
    }

    public Board(int size){
        this.size = size+2;
        this.board = new Constants.ObjectTypes[this.size][this.size];

        for(int i = 0; i < this.size; i++) {
            for(int j = 0; j < this.size; j++) {
                board[i][j] = Constants.ObjectTypes.EMPTY;
            }
        }

        for(int i = 0; i < this.size; i++) {
            board[i][0] = Constants.ObjectTypes.WALL;
            board[i][size+1] = Constants.ObjectTypes.WALL;
            board[0][i] = Constants.ObjectTypes.WALL;
            board[0][size+1] = Constants.ObjectTypes.WALL;
        }

        this.objects = new LinkedList<BoardObject>();
        this.players = new LinkedList<Player>();
    }

    public Board(Board toCopy) {
        this.size = toCopy.size;
        this.objects = new LinkedList<BoardObject>();
        this.players = new LinkedList<Player>();

        for(BoardObject obj : toCopy.objects) {
            BoardObject new_obj = new BoardObject(obj);
            this.objects.add(new_obj);
        }

        for(Player plr: toCopy.players) {
            Player new_plr = new Player(plr);
            this.players.add(new_plr);
        }

        this.board = new Constants.ObjectTypes[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                this.board[i][j] = toCopy.board[i][j];
            }
        }
    }

    public void moveFromCollision(Player plr, int posX, int posY) throws GameEndException {
        Constants.ObjectTypes plrType = plr.getType();

        if (!this.checkIfCollide(Constants.Direction.RIGHT, posX, posY, plrType)){
            plr.move(Constants.Direction.RIGHT);
        } else if (!this.checkIfCollide(Constants.Direction.LEFT, posX, posY, plrType)) {
            plr.move(Constants.Direction.LEFT);
        } else if (!this.checkIfCollide(Constants.Direction.UP, posX, posY, plrType)) {
            plr.move(Constants.Direction.UP);
        } else if (!this.checkIfCollide(Constants.Direction.DOWN, posX, posY, plrType)) {
            plr.move(Constants.Direction.DOWN);
        } else {
            throw new GameEndException("Cannot move anywhere and still in collision!");
//                                plr.moveRandom(posX, posY, gameBoard.getSize());
        }
    }

    public void refreshBoard() {
        for(int i = 0; i < this.size; i++) {
            for(int j = 0; j < this.size; j++) {
                board[i][j] = Constants.ObjectTypes.EMPTY;
            }
        }

        //outer walls
        for(int i = 0; i < this.size; i++) {
            board[i][0] = Constants.ObjectTypes.WALL;
            board[i][size-1] = Constants.ObjectTypes.WALL;
            board[0][i] = Constants.ObjectTypes.WALL;
            board[size-1][i] = Constants.ObjectTypes.WALL;
        }

        for(BoardObject obj : objects) {
            Constants.ObjectTypes objType = obj.getType();
            int sizeX = obj.getSizeX(),
                sizeY = obj.getSizeY(),
                posX = obj.getPosX(),
                posY = obj.getPosY();

            for(int i = 0; i < sizeX; i++) {
                for(int j = 0; j < sizeY; j++) {
                    board[posX+i][posY+j] = objType;
                }
            }
        }

        for(Player plr : players) {
            Constants.ObjectTypes type = plr.getType();
            int posX = plr.getPosX(),
                posY = plr.getPosY();
            board[posX][posY] = type;
            if(posX == 0 || posX == size-1) {
                System.out.println("PosX WTF?");
            }
            if(posY == 0 || posY == size-1) {
                System.out.println("PosY WTF?");
            }
        }
    }

    public int getSize() {
        return this.size;
    }

    public void setObjects(List<BoardObject> objects) {
        this.objects = objects;
        refreshBoard();
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
        refreshBoard();
    }

    public boolean checkIfCollide(Constants.Direction direction, int startX, int startY, Constants.ObjectTypes type) {
        int new_posX = 0, new_posY = 0;

        switch (direction) {
            case STAY:
                new_posX = startX;
                new_posY = startY;
                break;
            case RIGHT:
                new_posX = startX+1;
                new_posY = startY;
                break;
            case LEFT:
                new_posX = startX-1;
                new_posY = startY;
                break;
            case DOWN:
                new_posX = startX;
                new_posY = startY-1;
                break;
            case UP:
                new_posX = startX;
                new_posY = startY+1;
                break;
        }
        if(new_posX >= this.size -1 || new_posY >= this.size -1 || new_posX < 1 || new_posY < 1) {
            return true; //Cannot exceed board
        }

        if(board[new_posX][new_posY] == Constants.ObjectTypes.GATEWAY && type == Constants.ObjectTypes.COP) {
            return true;
        }

        //check if in new field is wall, thief, cop
//        return ;
        if(board[new_posX][new_posY] == Constants.ObjectTypes.EMPTY || board[new_posX][new_posY] == Constants.ObjectTypes.GATEWAY) {
            return false;
        } else {
            System.out.println("Collision");
            return true;
        }
    }

    @Override
    public String toString() {
        String ret = "  ";

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                switch (board[i][j]) {
                    case EMPTY:
                        ret += " - ";
                        break;
                    case WALL:
                        ret += " W ";
                        break;
                    case GATEWAY:
                        ret += " G ";
                        break;
                    case THIEF:
                        ret += " T ";
                        break;
                    case COP:
                        ret += " C ";
                        break;
                }
            }
            ret += "\n  ";

        }
        return ret;
    }
}
