package info801.tp;

import java.util.Random;

public abstract class RandomGenerator {
    public static boolean nextBool(){
        Random random = new Random();
        return random.nextBoolean();
    }

    public static int nextInt(int min, int max){
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }

}

