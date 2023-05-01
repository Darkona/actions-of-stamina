package com.ccr4ft3r.actionsofstamina.util;

public class ArrayUtil {

    @SafeVarargs
    public static <T, E extends Enum<E>> T get(E enumValue, T... values) {
        if (values.length == 1)
            return values[0];
        return values.length <= enumValue.ordinal() ? values[1] : values[enumValue.ordinal()];
    }

    @SafeVarargs
    public static <T> T[] of(T... v) {
        return v;
    }

}