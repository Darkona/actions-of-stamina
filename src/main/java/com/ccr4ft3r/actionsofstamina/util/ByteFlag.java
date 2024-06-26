package com.ccr4ft3r.actionsofstamina.util;

public class ByteFlag {

    private byte flag;

    public ByteFlag() {
        this.flag = 0;
    }

    public ByteFlag(byte flag) {
        this.flag = flag;
    }

    public void set(Enum<?> e, boolean state) {
        setFlag(e.ordinal(), state);
    }

    public boolean get(Enum<?> e) {
        return getFlag(e.ordinal());
    }

    public void setFlag(int flag, boolean state) {
        if (state) {
            this.flag = (byte) (this.flag | (1 << flag));
        } else {
            clearFlag(flag);
        }
    }

    public void clearFlag(int flag) {
        this.flag = (byte) (this.flag & ~(1 << flag));
    }

    public boolean getFlag(int flag) {
        return (this.flag & (1 << flag)) != 0;
    }

    public byte getFlag() {
        return this.flag;
    }

    public void copy(ByteFlag other) {
        this.flag = other.flag;
    }

    public boolean equals(ByteFlag other) {
        return this.flag == other.flag;
    }
}
