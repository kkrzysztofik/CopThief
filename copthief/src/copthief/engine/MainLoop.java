package copthief.engine;

import copthief.ai.RandomAI;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MainLoop {
    private int T;
    private int k;
    private int timeLimit; //in ms
    private int currentT;
    private int cops;
    private int thieves;

    private int walls;
    private int wallsWidth;
    private double wallMovementChance;
    private double wallMovementDirectionChange;

    private int fieldArea;

    private int gates;
    private int gatesWidth;
    private double gateMovementChance;
    private double gateMovementDirectionChange;

    private int boardWidth;
    private int outerWidth;

//    private LinkedList<BoardObject> objectsList;
//    private LinkedList<Player> playerList;
    private LinkedList<Board> visitedStates;

    private String thievesEngine;
    private String copEngine;

    private Board gameBoard;
    private String message;

    public MainLoop(String thievesEngine, String copEngine){
        this.T = 250;
        this.currentT = 0;

        this.k = 5;
        this.timeLimit = 500;

        this.cops = 5;
        this.thieves = 1;

        this.gates = 2;
        this.gatesWidth = 2;
        this.gateMovementChance = 0.5;
        this.gateMovementDirectionChange = 0.1;

        this.walls = 4;
        this.wallsWidth = 4;
        this.wallMovementChance = 0.75;
        this.wallMovementDirectionChange = 0.05;

        this.boardWidth = 20;
        this.outerWidth = this.boardWidth + 2;

        this.fieldArea = 2;

        this.thievesEngine = thievesEngine;
        this.copEngine = copEngine;

//        this.objectsList = new LinkedList<BoardObject>();
//        this.playerList = new LinkedList<Player>();
        this.visitedStates = new LinkedList<Board>();

        this.gameBoard = new Board(this.boardWidth);
        this.message = "";
    }

    private void initBoard() {
        Random rnd = new Random();

        for(int i = 0; i<gates; i++) { //gates
            int whichWall = rnd.nextInt(4),
                pos = rnd.nextInt(this.boardWidth+1),
                sizeX = 1,
                sizeY = 1,
                posX = 0,
                posY = 0;

            if(whichWall < 2) {
                sizeX = this.gatesWidth;
            } else {
                sizeY = this.gatesWidth;
            }

            switch(whichWall) {
                case 0:
                    posX = pos;
                    posY = this.outerWidth - 1;

                    if(posX+sizeX-1 > this.outerWidth) {
                        posX = this.outerWidth - sizeX;
                    }
                    break;
                case 1:
                    posX = pos;
                    posY = 0;

                    if(posX+sizeX-1 > this.outerWidth) {
                        posX = this.outerWidth - sizeX;
                    }
                    break;
                case 2:
                    posX = 0;
                    posY = pos;

                    if(posY+sizeY-1 > this.outerWidth) {
                        posY = this.outerWidth - sizeY;
                    }
                    break;
                case 3:
                    posX = this.outerWidth - 1;
                    posY = pos;

                    if(posY+sizeY-1 > this.outerWidth) {
                        posY = this.outerWidth - sizeY;
                    }
                    break;
            }

            BoardObject obj = new BoardObject(true, sizeX, sizeY, gateMovementChance, gateMovementDirectionChange);
            obj.setPos(posX, posY);
            gameBoard.objects.add(obj);
            gameBoard.refreshBoard();
        }

        for(int i = 0; i<walls; i++) { //walls
            int direction = rnd.nextInt(2),
                posX = rnd.nextInt(this.boardWidth)+1,
                posY = rnd.nextInt(this.boardWidth)+1,
                sizeX = 1,
                sizeY = 1;

            if(direction > 0) {
                sizeX = this.wallsWidth;
                if(posX+sizeX-1 > this.boardWidth) {
                    posX = this.boardWidth - sizeX;
                }
            } else {
                sizeY = this.wallsWidth;
                if(posY+sizeY-1 > this.boardWidth) {
                    posY = this.boardWidth - sizeY;
                }
            }

            BoardObject obj = new BoardObject(false, sizeX, sizeY, wallMovementChance, wallMovementDirectionChange);
            obj.setPos(posX, posY);
            gameBoard.objects.add(obj);
            gameBoard.refreshBoard();
        }

        //RandomAI for now
        for(int i = 0; i<cops; i++) {
            Player cop = new RandomAI();
            int posX = rnd.nextInt(this.boardWidth)+1,
                posY = rnd.nextInt(this.boardWidth)+1;

            while(gameBoard.checkIfCollide(0, posX, posY)) { // until collides randomise new values
                posX = rnd.nextInt(this.boardWidth)+1;
                posY = rnd.nextInt(this.boardWidth)+1;
            }

            cop.setPos(posX, posY);
            gameBoard.players.add(cop);
            gameBoard.refreshBoard();

        }

        for(int i = 0; i<thieves; i++) {
            Player thief = new RandomAI(false);
            int posX = rnd.nextInt(this.boardWidth)+1,
                posY = rnd.nextInt(this.boardWidth)+1;

            while(gameBoard.checkIfCollide(0, posX, posY)) {
                posX = rnd.nextInt(this.boardWidth)+1;
                posY = rnd.nextInt(this.boardWidth)+1;
            }

            thief.setPos(posX, posY);
            gameBoard.players.add(thief);
            gameBoard.refreshBoard();
        }
    }

    private void prepareMove() {
        List<Board> lastList = new LinkedList<Board>();
        int visitedSize = this.visitedStates.size() - 1;

        if (this.visitedStates.size() >= this.k) {
            lastList = this.visitedStates.subList(visitedSize-k, visitedSize);
        } else {
            lastList = this.visitedStates;
        }

        for(BoardObject obj : gameBoard.objects){
            obj.prepareMove(lastList, k);
        }

        for(Player plr : gameBoard.players){
            plr.prepareMove(lastList, k);
        }
    }

    private void makeMove() throws GameEndException {
        for(BoardObject obj : gameBoard.objects){
            int movement = obj.getMove(),
                posX = obj.getPosX(),
                posY = obj.getPosY();

            if(obj.getType()) { //wall
                switch (movement) {
                    case 0:
                        //do nothing
                        break;
                    case 1:
                        obj.setPos(posX, posY+1);
                        break;
                    case 2:
                        obj.setPos(posX, posY-1);
                        break;
                    case 3:
                        obj.setPos(posX-1, posY);
                        break;
                    case 4:
                        obj.setPos(posX+1, posY);
                        break;
                }
                // check if collide with players
            } else { //gateway
                switch (movement) {
                    case 0:
                        //do nothing
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                }
            }
        }

        for(Player plr: gameBoard.players){
            int movement = plr.getMove();

            if(plr.getType()) { //cop
                switch (movement) {
                    case 0:
                        //do nothing
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                }
            } else { //thief
                switch (movement) {
                    case 0:
                        //do nothing
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                    case 4:
                        break;
                }
            }
        }
        //check if thief in range of cop or in gateway
    }

    public String print() {
        String ret = "";

        if(this.message.length() > 0) {
            ret += "Message: " + this.message + " at step " + this.currentT;
        }
        ret += gameBoard.toString();

        return ret;
    }

    public void run() {
        this.initBoard();

        System.out.println("Initial state:");
        System.out.println(this.print());

        this.visitedStates.add(new Board(this.gameBoard));

        for(currentT = 0; currentT <= T; currentT++){
            boolean movesLeft = (currentT%k == 0);

            if (!movesLeft) {
                prepareMove();
            }

            try {
                makeMove();
            } catch (GameEndException ex) {
                this.message = ex.getMessage();
                break;
            }
            System.out.println("Step #" + currentT);
            System.out.println(this.print());
        }

    }
}
