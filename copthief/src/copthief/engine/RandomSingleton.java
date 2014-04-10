package copthief.engine;

import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Random;

public class RandomSingleton {
    private static RandomSingleton instance;
    private Random rnd;
    private static long seed = -1;

    private RandomSingleton() {
        rnd = new Random(seed);
    }

    public static RandomSingleton getInstance() {
        if(instance == null) {
            if(seed != -1) {
                instance = new RandomSingleton();
            } else {
                throw new ExceptionInInitializerError("Seed not initialized");
            }
        }
        return instance;
    }

    public static void setSeed(long sseed) {
        if(instance != null) {
            throw new ExceptionInInitializerError("Cannot initialize already initialized randomiser");
        }
        seed = sseed;
    }

    public static void setRandomSeed() {
        if(instance != null) {
            throw new ExceptionInInitializerError("Cannot initialize already initialized randomiser");
        }

        SecureRandom sec = new SecureRandom();
        byte[] sbuf = sec.generateSeed(8);
        ByteBuffer bb = ByteBuffer.wrap(sbuf);
        seed = bb.getLong();
    }

    public static long getSeed() {
        return seed;
    }

    public double nextDouble() {
        return rnd.nextDouble();
    }

    public int nextInt(int max) {
        return rnd.nextInt(max);
    }
}