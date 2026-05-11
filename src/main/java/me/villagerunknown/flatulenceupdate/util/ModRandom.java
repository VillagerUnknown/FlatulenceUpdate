package me.villagerunknown.flatulenceupdate.util;

import java.util.Random;

public final class ModRandom {

    private static final Random RAND = new Random();

    private ModRandom() {}

    public static boolean hasChance(float chance) {
        return RAND.nextFloat() < chance;
    }

    public static float getRandomWithinRange(float min, float max) {
        return min + (float) (Math.random() * (max - min));
    }
}
