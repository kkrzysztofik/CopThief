package copthief.engine;

public class GameEndException extends Exception {
    public GameEndException(String message) {
        super(message);
    }

    public String getMessage() {
        return super.getMessage();
    }
}