package com.ccr4ft3r.actionsofstamina.util;

/**
 * Bit layout of the movement-state flags the client sends to the server (a {@code short} on the wire). Plain
 * {@code int} + bit ops: nothing is allocated to build, compare or read them.
 */
public final class ActionFlags {

    public static final int SPRINTING = 1 << 1;
    public static final int CRAWLING = 1 << 2;
    public static final int SWIMMING = 1 << 3;
    public static final int ELYTRA = 1 << 4;
    public static final int HOLDING_SHIELD = 1 << 5;
    /** Set by the Paragliders compat, which waits in src/disabled on this Minecraft version; the bit stays reserved. */
    public static final int PARAGLIDING = 1 << 7;
    /** Wall-Jump TXF's wall cling (and the slide down the wall that follows it). */
    public static final int WALL_CLINGING = 1 << 8;
    /** Drawing a bow, loading a crossbow or aiming a trident. */
    public static final int DRAWING = 1 << 9;
    /** Going up something climbable (ladder, vine, scaffolding). */
    public static final int CLIMBING = 1 << 10;
    /** Paddling (or turning) a boat from the rowed_boats tag as its driver. */
    public static final int ROWING = 1 << 11;
    /** Brushing (any item with the brush animation). */
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
