package com.ccr4ft3r.actionsofstamina.config;

import com.google.common.collect.Sets;

import java.util.Set;

public enum ActionType {

    TICKS, TIMES;

    public static final Set<AoSAction> CONTINUOUS_ACTIONS = Sets.newConcurrentHashSet();
}