package copthief.engine;

public class Constants {
    public static enum Direction {
        LEFT, RIGHT, UP, DOWN, STAY;
        private static final Direction[] constList = Direction.values();

        public static Direction fromInteger(int x) {
            return constList[x];
        }
    }
    public static enum ObjectTypes {
        EMPTY, WALL, GATEWAY, THIEF, COP
    }
}
