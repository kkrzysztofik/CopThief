package copthief.engine;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) {
        BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in), 1);
        String resp;

        MainLoop loop = new MainLoop("RandomAI", "RandomAI");
        loop.run();
    }
}
