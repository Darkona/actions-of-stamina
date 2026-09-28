package com.ccr4ft3r.actionsofstamina.util;

/**
 * Bit layout of the movement-state byte the client sends to the server. Plain {@code byte} + bit ops: nothing is
 * allocated to build, compare or read it.
 */
public final class ActionFlags {

    public static final int MOVING = 1;
    public static final int SPRINTING = 1 << 1;
    public static final int CRAWLING = 1 << 2;
    public static final int SWIMMING = 1 << 3;
    public static final int ELYTRA = 1 << 4;
    public static final int HOLDING_SHIELD = 1 << 5;
    public static final int CLIMBING = 1 << 6;
    public static final int PARAGLIDING = 1 << 7;

    private ActionFlags() {
    }

    public static boolean has(byte flags, int bit) {
        return (flags & bit) != 0;
    }

    public static byte with(byte flags, int bit, boolean state) {
        return (byte) (state ? (flags | bit) : (flags & ~bit));
    }
}
