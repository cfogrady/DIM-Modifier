package com.github.cfogrady.dim.modifier.utils;

public class NullUtils {
    public static <T> T getOrDefault(T value, T dflt) {
        if(value == null) {
            return dflt;
        }
        return value;
    }
}
