package com.ccr4ft3r.actionsofstamina.content.stamina;

final class FractionalCarry {

    private double carry;

    int add(double amount) {
        carry += amount;
        int whole = (int) carry;
        carry -= whole;
        return whole;
    }

    void reset() {
        carry = 0.0;
    }
}
