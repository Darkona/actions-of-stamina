package com.ccr4ft3r.actionsofstamina.content.stamina;

import java.util.Arrays;

final class SourcedDrains<K> {

    private static final int RATE_ONE = 1000;
    private static final byte BLOCKS_REGEN = 1;

    private Object[] sources = new Object[2];
    private long[] rates = new long[2];
    private int[] remainders = new int[2];
    private int[] timeouts = new int[2];
    private long[] refreshed = new long[2];
    private byte[] flags = new byte[2];
    private int size;

    static long perTick(double unitsPerTick) {
        return Math.round(unitsPerTick * RATE_ONE);
    }

    int size() {
        return size;
    }

    int indexOf(K source) {
        for (int i = 0; i < size; i++) {
            if (sources[i].equals(source)) return i;
        }
        return -1;
    }

    void refresh(K source, long milliPerTick, int timeoutTicks, boolean blocksRegen, long now) {
        int i = indexOf(source);
        if (i < 0) {
            if (size == sources.length) grow();
            i = size++;
            sources[i] = source;
            remainders[i] = 0;
        }
        rates[i] = Math.max(0L, milliPerTick);
        timeouts[i] = timeoutTicks;
        refreshed[i] = now;
        flags[i] = blocksRegen ? BLOCKS_REGEN : 0;
    }

    boolean timedOut(int index, long now) {
        return timeouts[index] > 0 && now - refreshed[index] > timeouts[index];
    }

    int advance(int index) {
        long total = remainders[index] + rates[index];
        remainders[index] = (int) (total % RATE_ONE);
        return (int) Math.min(Integer.MAX_VALUE, total / RATE_ONE);
    }

    boolean anyBlocksRegen() {
        for (int i = 0; i < size; i++) {
            if ((flags[i] & BLOCKS_REGEN) != 0) return true;
        }
        return false;
    }

    void removeAt(int index) {
        int tail = size - index - 1;
        if (tail > 0) {
            System.arraycopy(sources, index + 1, sources, index, tail);
            System.arraycopy(rates, index + 1, rates, index, tail);
            System.arraycopy(remainders, index + 1, remainders, index, tail);
            System.arraycopy(timeouts, index + 1, timeouts, index, tail);
            System.arraycopy(refreshed, index + 1, refreshed, index, tail);
            System.arraycopy(flags, index + 1, flags, index, tail);
        }
        sources[--size] = null;
    }

    boolean remove(K source) {
        int i = indexOf(source);
        if (i < 0) return false;
        removeAt(i);
        return true;
    }

    private void grow() {
        int grown = size * 2;
        sources = Arrays.copyOf(sources, grown);
        rates = Arrays.copyOf(rates, grown);
        remainders = Arrays.copyOf(remainders, grown);
        timeouts = Arrays.copyOf(timeouts, grown);
        refreshed = Arrays.copyOf(refreshed, grown);
        flags = Arrays.copyOf(flags, grown);
    }
}
