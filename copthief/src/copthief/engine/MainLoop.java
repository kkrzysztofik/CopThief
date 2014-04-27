package copthief.engine;

import copthief.ai.RandomAI;

import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class MainLoop {
    private int T;
    private int k;
    private int timeLimit; //in ms
    private int currentT;
    private int cops;
    private int copRange;
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
    private RandomSingleton rnd;

//    private LinkedList<BoardObject> objectsList;
//    private LinkedList<Player> playerList;
    private LinkedList<Board> visitedStates;

    private String thievesEngine;
    private String copEngine;

    private Board gameBoard;
    private String message;
    private Display gameDisp;
    private int drawID = 0;

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

        this.copRange = 1;
//        this.objectsList = new LinkedList<BoardObject>();
//        this.playerList = new LinkedList<Player>();
        this.visitedStates = new LinkedList<Board>();

//        RandomSingleton.setRandomSeed();
        RandomSingleton.setSeed(-3037105205831816012L);
        this.rnd = RandomSingleton.getInstance();
        this.gameBoard = new Board(this.boardWidth);
        this.message = "";
        this.gameDisp = new Display("CopThief");
        gameDisp.configureForSize(gameBoard.getSize(), gameBoard.getSize());
    }

    private void initBoard() {
        RandomSingleton rnd = RandomSingleton.getInstance();

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

            BoardObject obj = new BoardObject(Constants.ObjectTypes.GATEWAY, sizeX, sizeY, gateMovementChance, gateMovementDirectionChange);
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

            BoardObject obj = new BoardObject(Constants.ObjectTypes.WALL, sizeX, sizeY, wallMovementChance, wallMovementDirectionChange);
            obj.setPos(posX, posY);
            gameBoard.objects.add(obj);
            gameBoard.refreshBoard();
        }

        //RandomAI for now
        for(int i = 0; i<cops; i++) {
            Player cop = new RandomAI(Constants.ObjectTypes.COP);
            int posX = rnd.nextInt(this.boardWidth)+1,
                posY = rnd.nextInt(this.boardWidth)+1;

            while(gameBoard.checkIfCollide(Constants.Direction.STAY, posX, posY, Constants.ObjectTypes.COP)) { // until collides randomise new values
                posX = rnd.nextInt(this.boardWidth)+1;
                posY = rnd.nextInt(this.boardWidth)+1;
            }

            cop.setPos(posX, posY);
            gameBoard.players.add(cop);
            gameBoard.cops.add(cop);
            gameBoard.refreshBoard();

        }

        for(int i = 0; i<thieves; i++) {
            Player thief = new RandomAI(Constants.ObjectTypes.THIEF);
            int posX = rnd.nextInt(this.boardWidth)+1,
                posY = rnd.nextInt(this.boardWidth)+1;

            while(gameBoard.checkIfCollide(Constants.Direction.STAY, posX, posY, Constants.ObjectTypes.THIEF)) {
                posX = rnd.nextInt(this.boardWidth)+1;
                posY = rnd.nextInt(this.boardWidth)+1;
            }

            thief.setPos(posX, posY);
            gameBoard.players.add(thief);
            gameBoard.thieves.add(thief);
            gameBoard.refreshBoard();
        }
    }

    private void prepareMove() {
        List<Board> lastList;
        int visitedSize = this.visitedStates.size() - 1;

        if (this.visitedStates.size() >= this.k) {
            lastList = this.visitedStates.subList(visitedSize-k, visitedSize);
        } else {
            lastList = this.visitedStates;
        }


        for(BoardObject obj : gameBoard.objects){
            List<Board> copyList = new LinkedList<Board>();

            for(Board b : lastList) {
                copyList.add(new Board(b));
            }

            obj.prepareMove(copyList, k);
        }

        for(Player plr : gameBoard.players){
            List<Board> copyList = new LinkedList<Board>();

            for(Board b : lastList) {
                copyList.add(new Board(b));
            }

            plr.prepareMove(copyList, k);
        }
    }

    private void makeMove() throws GameEndException {
        int boardSize = gameBoard.getSize();

        for(BoardObject obj : gameBoard.objects){
            Constants.Direction movement = obj.getMove();
            int posX = obj.getPosX(),
                posY = obj.getPosY(),
                sizeX = obj.getSizeX(),
                sizeY = obj.getSizeY();

            if(obj.getType() == Constants.ObjectTypes.WALL) { //wall
                switch (movement) {
                    case STAY:
                        //do nothing
                        break;
                    case UP:
                        obj.setPos(posX, posY+1);
                        break;
                    case DOWN:
                        obj.setPos(posX, posY-1);
                        break;
                    case LEFT:
                        obj.setPos(posX-1, posY);
                        break;
                    case RIGHT:
                        obj.setPos(posX+1, posY);
                        break;
                }
                // check if collide with players
            } else { //gateway
                switch (movement) {
                    case STAY:
                        //do nothing
                        break;
                    case LEFT:
                        if(posX == boardSize - 1 && posY <= sizeY && sizeY > sizeX) {
                            obj.swapSize();
                            obj.setPos(boardSize - sizeX, 0);
                        } else if(posX > 0 && posY == 0) {
                            obj.setPos(posX-1, posY);
                        } else if(posX == 0 && posY == 0) {
                            obj.swapSize();
                            obj.setPos(0, sizeY);
                        } else if(posX == 0 && posY < boardSize - 1) {
                            obj.setPos(posX, posY+1);
                        } else if(posX == 0 && posY == boardSize - 1) {
                            if(sizeX < sizeY) {
                                obj.swapSize();
                            } else {
                                obj.setPos(posX+1, posY);
                            }
                        } else if(posX < boardSize - sizeX && posY == boardSize - 1) {
                            obj.setPos(posX+1, posY);
                        } else if(posX == boardSize - sizeX && posY == boardSize - 1) {
                            if(sizeX > sizeY) {
                                obj.swapSize();
                            } else {
                                obj.setPos(posX, posY-1);
                            }
                        } else if(posX == boardSize - 1 && posY > sizeY) {
                            obj.setPos(posX, posY-1);
                        } else {
                            System.out.println("Wrong pos. You missed sthg?");
                        }

                        break;
                    case RIGHT:
//                        if(posX > 0 && posX <= boardSize - 1) {
//                            if(posY <= 0) {
//                                obj.setPos(posX+1, posY);
//                            } else {
//                                obj.setPos(posX-1, posY);
//                            }
//                        } else if (posX <= 0 && posY >= sizeY) {
//                            obj.setPos(posX, posY-1);
//                        } else if (posX >= boardSize - sizeX - 1 && posY < boardSize - 1) {
//                            obj.setPos(posX, posY+1);
//                        } else {
//                            if (posX <= 0 && posY <= sizeY) {
//                                int tmp = sizeX;
//                                sizeX = sizeY;
//                                sizeY = tmp;
//                                obj.setSize(sizeX, sizeY);
//                            } else if (posX <= 0 && posY >= boardSize - 1) {
//                                int tmp = sizeX;
//                                sizeX = sizeY;
//                                sizeY = tmp;
//                                obj.setSize(sizeX, sizeY);
//                            } else if (posX >= boardSize - sizeX - 1 && posY <= 0) {
//                                int tmp = sizeX;
//                                sizeX = sizeY;
//                                sizeY = tmp;
//                                obj.setSize(sizeX, sizeY);
//                                obj.setPos(boardSize - 1, sizeY);
//                            } else if (posX >= boardSize - 1  && posY >= boardSize - 1) {
//                                int tmp = sizeX;
//                                sizeX = sizeY;
//                                sizeY = tmp;
//                                obj.setSize(sizeX, sizeY);
//                                obj.setPos(boardSize - sizeX - 1, boardSize - 1);
//                            } else {
//                                System.out.println("DUPA2");
//                            }
//                        }
                        break;
                    default:
                        System.out.println("JAKI KIERUNEK?!?!?!?");
                        break;
                }
            }
        }

        gameBoard.refreshBoard();

        for(Player plr: gameBoard.players){
            Constants.Direction movement = plr.getMove();
            Constants.ObjectTypes plrType = plr.getType();
            int posX = plr.getPosX(),
                posY = plr.getPosY();
            boolean stay_collide = gameBoard.checkIfCollide(Constants.Direction.STAY, posX, posY, plrType);

            switch (movement) {
                case STAY:
                    if(stay_collide){
                        gameBoard.moveFromCollision(plr, posX, posY);
                    }
                    break;
                case UP:
                    if(!gameBoard.checkIfCollide(Constants.Direction.UP, posX, posY, plrType)) {
                        plr.move(Constants.Direction.UP);
                    } else if(stay_collide){
                        gameBoard.moveFromCollision(plr, posX, posY);
                    }
                    break;
                case DOWN:
                    if(!gameBoard.checkIfCollide(Constants.Direction.DOWN, posX, posY, plrType)) {
                        plr.move(Constants.Direction.DOWN);
                    } else if(stay_collide){
                        gameBoard.moveFromCollision(plr, posX, posY);
                    }
                    break;
                case RIGHT:
                    if(!gameBoard.checkIfCollide(Constants.Direction.RIGHT, posX, posY, plrType)) {
                        plr.move(Constants.Direction.RIGHT);
                    } else if(stay_collide){
                        gameBoard.moveFromCollision(plr, posX, posY);
                    }
                    break;
                case LEFT:
                    if(!gameBoard.checkIfCollide(Constants.Direction.LEFT, posX, posY, plrType)) {
                        plr.move(Constants.Direction.LEFT);
                    } else if(stay_collide){
                        gameBoard.moveFromCollision(plr, posX, posY);
                    }
                    break;
            }
        }
        gameBoard.refreshBoard();
        visitedStates.add(new Board(gameBoard));
        //check if thief in range of cop or in gateway
        for(Player thf : gameBoard.thieves) {
            int thfX = thf.getPosX(),
                thfY = thf.getPosY();

            for(Player cop : gameBoard.cops) {
                int copX = cop.getPosX(),
                    copY = cop.getPosY();

                double dist = Math.sqrt(Math.pow(thfX-copX, 2) + Math.pow(thfY-copY, 2));

                if(dist <= copRange) {
                    throw new GameEndException("Thief busted!");
                }
            }

            if (thfX <= 0 || thfX >= boardSize - 1 || thfY <= 0 || thfY >= boardSize-1){
                throw new GameEndException("Thief run!");
            }
        }
    }

    private void redrawDisplay(int drawID) {
        Board board = visitedStates.get(drawID);
        int boardSize = board.getSize();

        for(int i = boardSize-1; i >= 0; i--) {
            for(int j=0; j < boardSize; j++) {
                switch(board.board[j][i]) {
                    case WALL:
                        gameDisp.drawAtLocation("Wall", i, j);
                        break;
                    case GATEWAY:
                        gameDisp.drawAtLocation("Goal", i, j);
                        break;
                    case THIEF:
                        gameDisp.drawAtLocation("Thief", i, j);
                        break;
                    case COP:
                        gameDisp.drawAtLocation("Cop", i, j);
                        break;
                    case EMPTY:
                        gameDisp.drawAtLocation("Empty", i, j);
                        break;
                }
            }
        }
        gameDisp.setVisible(true);
        gameDisp.grabFocus();
    }

    public String print() {
        String ret = "";

        if(this.message.length() > 0) {
            ret += "Message: " + this.message + " at step " + this.currentT + "\n";
        }
        ret += gameBoard.toString();

        return ret;
    }

    public void quit() {
        System.out.println("DONE");
        System.exit(1);
    }

    private boolean processSingleCommand(Constants.Commands cmd) {
        switch (cmd) {
            case QUIT:
                quit();
                break;
            case NEXT:
                if(drawID < visitedStates.size() - 1) {
                    drawID++;
                    return true;
                }
                break;
            case PREVIOUS:
                if(drawID > 0) {
                    drawID--;
                    return true;
                }
                break;
            default:
                break;
        }
        return false;
    }

    public void run() {
        this.initBoard();

        System.out.println("Initial state:");
        System.out.println(this.print());

        this.visitedStates.add(new Board(this.gameBoard));

        for(currentT = 0; currentT <= T; currentT++){
            int movesLeft = currentT%k;

            if (movesLeft <= 0) {
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

        System.out.println(this.print());
        System.out.println("Seed: " + RandomSingleton.getSeed());

        Boolean redraw = true;
        while(true) {
            if(redraw) {
                redrawDisplay(this.drawID);
            }

            redraw = processSingleCommand(gameDisp.getCommandFromUser());
        }
    }
}

