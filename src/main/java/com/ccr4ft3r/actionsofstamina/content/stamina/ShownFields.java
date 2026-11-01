package com.ccr4ft3r.actionsofstamina.content.stamina;

import java.util.Arrays;

final class ShownFields<E extends Enum<E>> {

    private final int[] shown;
    private final int[] sent;
    private boolean pending = true;

    ShownFields(Class<E> fields) {
        int count = fields.getEnumConstants().length;
        shown = new int[count];
        sent = new int[count];
    }

    void show(E field, int value) {
        shown[field.ordinal()] = value;
    }

    void show(E field, boolean value) {
        shown[field.ordinal()] = value ? 1 : 0;
    }

    boolean shouldSend() {
        if (!pending && Arrays.equals(shown, sent)) return false;
        System.arraycopy(shown, 0, sent, 0, shown.length);
        pending = false;
        return true;
    }

    void resendNext() {
        pending = true;
    }
}
