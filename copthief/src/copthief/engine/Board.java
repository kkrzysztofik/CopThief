package copthief.engine;

import java.util.LinkedList;
import java.util.List;

public class Board {
    private int size;
    public List<BoardObject> objects;
    public List<Player> players; //cops and thiefs
    private int[][] board; //1 - wall, 2 - gateway, 3 - thief, 4 - cop, 0 - empty

    public Board(int size, List<BoardObject> objects, List<Player> players){
        this.size = size+2;
        this.objects = objects;
        this.players = players;
        this.board = new int[this.size][this.size];

        for(int i = 0; i < this.size; i++) {
            for(int j = 0; j < this.size; j++) {
                board[i][j] = 0;
            }
        }

        for(int i = 0; i < this.size; i++) {
            board[i][0] = 1;
            board[i][size+1] = 1;
            board[0][i] = 1;
            board[0][size+1] = 1;
        }
    }

    public Board(int size){
        this.size = size+2;
        this.board = new int[this.size][this.size];

        for(int i = 0; i < this.size; i++) {
            for(int j = 0; j < this.size; j++) {
                board[i][j] = 0;
            }
        }

        for(int i = 0; i < this.size; i++) {
            board[i][0] = 1;
            board[i][this.size-1] = 1;
            board[0][i] = 1;
            board[this.size-1][i] = 1;
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

        this.board = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                this.board[i][j] = toCopy.board[i][j];
            }
        }
    }

    public void refreshBoard() {
        //outer walls
        for(int i = 0; i < this.size; i++) {
            board[i][0] = 1;
            board[i][size-1] = 1;
            board[0][i] = 1;
            board[size-1][i] = 1;
        }

        for(BoardObject obj : objects) {
            boolean objType = obj.getType();
            int sizeX = obj.getSizeX(),
                sizeY = obj.getSizeY(),
                posX = obj.getPosX(),
                posY = obj.getPosY();

            for(int i = 0; i < sizeX; i++) {
                for(int j = 0; j < sizeY; j++) {
                    if(objType) { //gateway
                        board[posX+i][posY+j] = 2;
                    } else { //wall
                        board[posX+i][posY+j] = 1;
                    }
                }
            }
        }

        for(Player plr : players) {
            boolean type = plr.getType();
            if(type) { //cop
                board[plr.getPosX()][plr.getPosY()] = 4;
            } else { //thief
                board[plr.getPosX()][plr.getPosY()] = 3;
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
    public boolean checkIfCollide(int direction, int startX, int startY) {
        boolean ret;
        int new_posX = 0, new_posY = 0;

        switch (direction) {
            case 0:
                new_posX = startX;
                new_posY = startY;
                break;
            case 1:
                new_posX = startX+1;
                new_posY = startY;
                break;
            case 2:
                new_posX = startX-1;
                new_posY = startY;
                break;
            case 3:
                new_posX = startX;
                new_posY = startY-1;
                break;
            case 4:
                new_posX = startX;
                new_posY = startY+1;
                break;
        }

        //check if in new field is wall, thief, cop
        return !(board[new_posX][new_posY] == 0 || board[new_posX][new_posY] == 2);
    }

    @Override
    public String toString() {
        String ret = "  ";

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                switch (board[i][j]) {
                    case 0:
                        ret += " - ";
                        break;
                    case 1:
                        ret += " W ";
                        break;
                    case 2:
                        ret += " G ";
                        break;
                    case 3:
                        ret += " T ";
                        break;
                    case 4:
                        ret += " C ";
                        break;
                }
            }
            ret += "\n  ";

        }
        return ret;
    }
}
