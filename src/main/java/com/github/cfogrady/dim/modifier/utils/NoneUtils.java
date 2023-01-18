package com.github.cfogrady.dim.modifier.utils;


import com.github.cfogrady.vb.dim.card.DimReader;

public class NoneUtils {
    public static int defaultIfNone(int value, int defaultValue) {
        return value == DimReader.NONE_VALUE ? defaultValue : value;
    }

    public static String defaultIfNone(int value, String defaultValue) {
        return value == DimReader.NONE_VALUE ? defaultValue : Integer.toString(value);
    }
}
