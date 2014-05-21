package copthief.engine;

import java.awt.event.KeyEvent;

public class Constants {
    public static enum Direction {
        LEFT, RIGHT, UP, DOWN, STAY;
        private static final Direction[] constList = Direction.values();

        public static Direction fromInteger(int x) {
            return constList[x];
        }
    }

    public static enum GameEndStates {
        BUSTED, RUN, TIMEOUT;
    }

    public static enum ObjectTypes {
        EMPTY, WALL, GATEWAY, THIEF, COP
    }

    public static enum Commands {
        NEXT, PREVIOUS, QUIT, UNKNOWN;

        public static Commands fromKeyCode(int keyCode){
            switch (keyCode) {
                case KeyEvent.VK_KP_DOWN:
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_KP_RIGHT:
                case KeyEvent.VK_RIGHT:
                    return NEXT;

                case KeyEvent.VK_KP_UP:
                case KeyEvent.VK_UP:
                case KeyEvent.VK_KP_LEFT:
                case KeyEvent.VK_LEFT:
                    return PREVIOUS;

                case KeyEvent.VK_Q:
                    return QUIT;
            }
            return UNKNOWN;
        }
    }
}
