package me.nahu.fairies.utils;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.NumberConversions;

import java.lang.reflect.Field;
import java.util.Random;

public class Utilities {
    public static final Random RANDOM = new Random();

    public static BukkitRunnable bukkitRunnable(final Runnable r) {
        return new BukkitRunnable(){
            @Override
            public void run(){
                r.run();
            }
        };
    }

    public static Field makeField(Class<?> clazz, String name) {
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException var3) {
            return null;
        } catch (Exception var4) {
            throw new RuntimeException(var4);
        }
    }

    public static void setField(Field field, Object instance, Object value) {
        if (field == null) {
            throw new RuntimeException("No such field");
        } else {
            field.setAccessible(true);

            try {
                field.set(instance, value);
            } catch (Exception var4) {
                throw new RuntimeException(var4);
            }
        }
    }

    public static int getRandomNumberFromBoundary(int max, int min, int fluctuation) {
        return NumberConversions.round(getRandomNumberFromBoundary(max, min) * formatFluctuation(fluctuation));
    }

    public static int getRandomNumberFromBoundary(int max, int min) {
        return getRandomNumberFromBoundary(max, min, RANDOM);
    }

    public static int getRandomNumberFromBoundary(int max, int min, Random random) {
        return random.nextInt((max - min) + 1) + min;
    }

    public static double formatFluctuation(int rawNumber, int base) {
        return Math.abs(base + (RANDOM.nextInt(rawNumber) * .01));
    }

    public static double formatFluctuation(int rawNumber) {
        return formatFluctuation(rawNumber, -1);
    }
}
