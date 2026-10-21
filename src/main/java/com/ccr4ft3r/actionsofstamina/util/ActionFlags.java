package com.ccr4ft3r.actionsofstamina.util;

public final class ActionFlags {

    public static final int SPRINTING = 1 << 1;
    public static final int CRAWLING = 1 << 2;
    public static final int SWIMMING = 1 << 3;
    public static final int ELYTRA = 1 << 4;
    public static final int HOLDING_SHIELD = 1 << 5;
    public static final int PARAGLIDING = 1 << 7;
    public static final int WALL_CLINGING = 1 << 8;
    public static final int DRAWING = 1 << 9;
    public static final int CLIMBING = 1 << 10;
    public static final int ROWING = 1 << 11;
    public static final int BRUSHING = 1 << 12;

    private ActionFlags() {
    }

    public static boolean has(int flags, int bit) {
        return (flags & bit) != 0;
    }

    public static int with(int flags, int bit, boolean state) {
        return state ? flags | bit : flags & ~bit;
    }
}
