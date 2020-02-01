package me.nahu.fairies.utils;

import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;

public class Utilities {
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
}
