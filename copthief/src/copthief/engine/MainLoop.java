package copthief.engine;

import copthief.ai.CopAI;
import copthief.ai.ThiefAI;

import java.util.LinkedList;
import java.util.List;

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

    private Constants.GameEndStates endState = Constants.GameEndStates.TIMEOUT;
    private int copPayout = 0, thiefPayout = 0;

    public MainLoop(String thievesEngine, String copEngine){
        this.T = 250;
        this.currentT = 0;

        this.k = 5;
        this.timeLimit = 500;

        this.cops = 5;
        this.thieves = 1;

        this.gates = 2;
        this.gatesWidth = 2;
        this.gateMovementChance = 1;
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

        this.visitedStates = new LinkedList<Board>();

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
            int sizeX = 1,
                sizeY = 1,
                posX = 0,
                posY = 0,
                z = rnd.nextInt(4*boardWidth + 3),
                a = (int) Math.floor(z/(boardWidth + 1));

            switch (a) {
                case 0:
                    posX = z % (boardWidth + 1);
                    posY = 0;
                    sizeX = this.gatesWidth;

                    break;
                case 1:
                    posX = (boardWidth + 1);
                    posY = z % (boardWidth + 1);
                    sizeY = this.gatesWidth;

                    break;
                case 2:
                    posX = (boardWidth) - z % (boardWidth + 1);
                    posY = (boardWidth + 1);
                    sizeX = this.gatesWidth;

                    break;
                case 3:
                    posX = 0;
                    posY = (boardWidth) - z % (boardWidth + 1);
                    sizeY = this.gatesWidth;

                    break;
            }

            BoardObject obj = new BoardObject(Constants.ObjectTypes.GATEWAY, sizeX, sizeY, gateMovementChance, gateMovementDirectionChange);
            obj.setPos(posX, posY);
            obj.setZ(z);
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
        PlayerGroup copGrp = new CopAI(Constants.ObjectTypes.COP, this.cops);
        copGrp.k = this.k;

        for(int i = 0; i<cops; i++) {
            Player cop = new Player(Constants.ObjectTypes.COP);
            int sizeX = 1, sizeY = 1;
            int posX = rnd.nextInt(this.boardWidth)+1,
                posY = rnd.nextInt(this.boardWidth)+1;

            while(gameBoard.checkIfCollide(Constants.Direction.STAY, posX, posY, Constants.ObjectTypes.COP, sizeX, sizeY)) { // until collides randomise new values
                posX = rnd.nextInt(this.boardWidth)+1;
                posY = rnd.nextInt(this.boardWidth)+1;
            }

            cop.setPos(posX, posY);
            copGrp.players.add(cop);
        }

        gameBoard.cops = copGrp;
        gameBoard.refreshBoard();

        ///////////////////////////

        PlayerGroup thfGrp = new ThiefAI(Constants.ObjectTypes.THIEF, this.thieves);
        thfGrp.k = this.k;

        for(int i = 0; i<thieves; i++) {
            Player thief = new Player(Constants.ObjectTypes.THIEF);
            int sizeX = 1, sizeY = 1;

            int posX = rnd.nextInt(this.boardWidth)+1,
                posY = rnd.nextInt(this.boardWidth)+1;

            while(gameBoard.checkIfCollide(Constants.Direction.STAY, posX, posY, Constants.ObjectTypes.THIEF, sizeX, sizeY)) {
                posX = rnd.nextInt(this.boardWidth)+1;
                posY = rnd.nextInt(this.boardWidth)+1;
            }

            thief.setPos(posX, posY);
            thfGrp.players.add(thief);
        }

        gameBoard.thieves = thfGrp;
        gameBoard.refreshBoard();
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


        List<Board> copyList = new LinkedList<Board>();

        for(Board b : lastList) {
            copyList.add(new Board(b));
        }

        gameBoard.cops.states = copyList;
        Thread copThrd = new Thread(gameBoard.cops);
        long t1 = System.currentTimeMillis();

        copThrd.start();
        try {
            copThrd.join(500);
        } catch (InterruptedException ex) {

        }

        List<Board> thievesList = new LinkedList<Board>();

        for(Board b : lastList) {
            thievesList.add(new Board(b));
        }
        System.out.println(thievesList.size());

        gameBoard.thieves.states = thievesList;
        Thread thvThrd = new Thread(gameBoard.thieves);

        thvThrd.start();
        try {
            copThrd.join(500);
        } catch (InterruptedException ex){

        }
    }

    private void makeMove() throws GameEndException {
        int boardSize = gameBoard.getSize();

        for(BoardObject obj : gameBoard.objects){
            Constants.Direction movement = obj.getMove();
            Constants.ObjectTypes objType = obj.getType();
            int posX = obj.getPosX(),
                posY = obj.getPosY(),
                sizeX = obj.getSizeX(),
                sizeY = obj.getSizeY();

            if(objType == Constants.ObjectTypes.WALL) { //wall
                switch (movement) {
                    case STAY:
                        //do nothing
                        break;
                    case UP:
                        if(!gameBoard.checkIfCollide(Constants.Direction.UP, posX, posY, objType, sizeX, sizeY)) {
                            obj.setPos(posX, posY + 1);
                        }
                        break;
                    case DOWN:
                        if(!gameBoard.checkIfCollide(Constants.Direction.DOWN, posX, posY, objType, sizeX, sizeY)) {
                            obj.setPos(posX, posY - 1);
                        }
                        break;
                    case LEFT:
                        if(!gameBoard.checkIfCollide(Constants.Direction.LEFT, posX, posY, objType, sizeX, sizeY)) {
                            obj.setPos(posX - 1, posY);
                        }
                        break;
                    case RIGHT:
                        if(!gameBoard.checkIfCollide(Constants.Direction.RIGHT, posX, posY, objType, sizeX, sizeY)) {
                            obj.setPos(posX + 1, posY);
                        }
                        break;
                }
                // check if collide with players
            } else { //gateway
                int z = obj.getZ();

                switch (movement) {
                    case STAY:
                        //do nothing
                        break;
                    case LEFT:
                        System.out.println("LEFT");
                        obj.setZ(z-1);
                        if((z - 1) < 0) {
                            obj.setZ(4*boardWidth + 3);
                        }

                        break;
                    case RIGHT:
                        System.out.println("RIGHT");
                        obj.setZ(z+1);
                        if((z + 1) > (4*boardWidth + 3)) {
                            obj.setZ(0);
                        }

                        break;
                    default:
                        System.out.println("JAKI KIERUNEK?!?!?!?");
                        break;
                }

                z = obj.getZ();
                int a = (int) Math.floor(z/(boardSize-1));

                switch (a) {
                    case 0:
                        posX = z % (boardSize-1);
                        posY = 0;

                        obj.setPos(posX, posY);
                        if(sizeY > sizeX) {
                            obj.swapSize();
                        }

                        break;
                    case 1:
                        posX = boardSize - 1;
                        posY = z % (boardSize-1);

                        obj.setPos(posX, posY);
                        if(sizeX > sizeY) {
                            obj.swapSize();
                        }

                        break;
                    case 2:
                        posX = boardSize - 2 - z % (boardSize-1);
                        posY = boardSize - 1;

                        obj.setPos(posX, posY);
                        if(sizeY > sizeX) {
                            obj.swapSize();
                        }
                        break;
                    case 3:
                        posX = 0;
                        posY = boardSize - 2 - z % (boardSize-1);

                        obj.setPos(posX, posY);
                        if(sizeY < sizeX) {
                            obj.swapSize();
                        }
                        break;
                }
            }
        }

        gameBoard.refreshBoard();

        for(Player plr: gameBoard.cops.players){
            Constants.Direction movement = plr.getMove();
            System.out.println("COP" + movement);
            Constants.ObjectTypes plrType = plr.getType();
            int posX = plr.getPosX(),
                posY = plr.getPosY(),
                sizeX = 1,
                sizeY = 1;

            switch (movement) {
                case STAY:
                    break;
                case UP:
                    if(!gameBoard.checkIfCollide(Constants.Direction.UP, posX, posY, plrType, sizeX, sizeY)) {
                        plr.move(Constants.Direction.UP);
                    }
                    break;
                case DOWN:
                    if(!gameBoard.checkIfCollide(Constants.Direction.DOWN, posX, posY, plrType, sizeX, sizeY)) {
                        plr.move(Constants.Direction.DOWN);
                    }
                    break;
                case RIGHT:
                    if(!gameBoard.checkIfCollide(Constants.Direction.RIGHT, posX, posY, plrType, sizeX, sizeY)) {
                        plr.move(Constants.Direction.RIGHT);
                    }
                    break;
                case LEFT:
                    if(!gameBoard.checkIfCollide(Constants.Direction.LEFT, posX, posY, plrType, sizeX, sizeY)) {
                        plr.move(Constants.Direction.LEFT);
                    }
                    break;
            }
        }

        for(Player plr: gameBoard.thieves.players){
            Constants.Direction movement = plr.getMove();
            System.out.println("THIEF: " + movement);
            Constants.ObjectTypes plrType = plr.getType();
            int posX = plr.getPosX(),
                posY = plr.getPosY(),
                sizeX = 1,
                sizeY = 1;

            switch (movement) {
                case STAY:
                    break;
                case UP:
                    if(!gameBoard.checkIfCollide(Constants.Direction.UP, posX, posY, plrType, sizeX, sizeY)) {
                        plr.move(Constants.Direction.UP);
                    }
                    break;
                case DOWN:
                    if(!gameBoard.checkIfCollide(Constants.Direction.DOWN, posX, posY, plrType, sizeX, sizeY)) {
                        plr.move(Constants.Direction.DOWN);
                    }
                    break;
                case RIGHT:
                    if(!gameBoard.checkIfCollide(Constants.Direction.RIGHT, posX, posY, plrType, sizeX, sizeY)) {
                        plr.move(Constants.Direction.RIGHT);
                    }
                    break;
                case LEFT:
                    if(!gameBoard.checkIfCollide(Constants.Direction.LEFT, posX, posY, plrType, sizeX, sizeY)) {
                        plr.move(Constants.Direction.LEFT);
                    }
                    break;
            }
        }
        gameBoard.refreshBoard();
        visitedStates.add(new Board(gameBoard));
        //check if thief in range of cop or in gateway
        for(Player thf : gameBoard.thieves.players) {
            int thfX = thf.getPosX(),
                thfY = thf.getPosY();

            for(Player cop : gameBoard.cops.players) {
                int copX = cop.getPosX(),
                    copY = cop.getPosY();

                double dist = Math.sqrt(Math.pow(thfX-copX, 2) + Math.pow(thfY-copY, 2));

                if(dist <= copRange) {
                    endState = Constants.GameEndStates.BUSTED;
                    throw new GameEndException("Thief busted!");
                }
            }

//            for(BoardObject obj: gameBoard.objects) {
//                int objX = obj.getPosX(),
//                    objY = obj.getPosY(),
//                    sizeX = obj.getSizeX(),
//                    sizeY = obj.getSizeY();
//                Constants.ObjectTypes objType = obj.getType();
//
//                if(objType != Constants.ObjectTypes.GATEWAY) {
//                    continue;
//                }
//
//                for(int i = 0; i < sizeX; i++) {
//                    for(int j = 0; j < sizeY; j++) {
//                       if(thfX == objX+i && thfY == objY+j) {
//                           throw new GameEndException("Thief run!");
//                       }
//                    }
//                }
//            }

            if (thfX <= 0 || thfX >= boardSize - 1 || thfY <= 0 || thfY >= boardSize-1){
                endState = Constants.GameEndStates.RUN;
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

        switch (endState) {
            case RUN:
                thiefPayout = 2*T - currentT - 1;
                break;
            case BUSTED:
                thiefPayout = currentT;
                break;
            case TIMEOUT:
                thiefPayout = T;
                break;
        }

        copPayout = -thiefPayout;

        System.out.println("Payout:");
        System.out.println("- thief: " + thiefPayout);
        System.out.println("- cop: " + copPayout);

        Boolean redraw = true;
        while(true) {
            if(redraw) {
                redrawDisplay(this.drawID);
            }

            redraw = processSingleCommand(gameDisp.getCommandFromUser());
        }
    }
}

