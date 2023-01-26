package com.github.cfogrady.dim.modifier.utils;


import com.github.cfogrady.vb.dim.card.DimReader;

public class NoneUtils {
    public static int NONE = 0xFFFF;

    public static int defaultIfNone(int value, int defaultValue) {
        return value == DimReader.NONE_VALUE ? defaultValue : value;
    }

    public static Integer nullIfNone(int value) {
        if(value == NONE) {
            return null;
        }
        return Integer.valueOf(value);
    }

    public static String defaultIfNone(int value, String defaultValue) {
        return value == DimReader.NONE_VALUE ? defaultValue : Integer.toString(value);
    }
}
