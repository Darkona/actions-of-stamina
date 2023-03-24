package com.ccr4ft3r.actionsofstamina.data;

import com.elenai.feathers.api.FeathersHelper;
import net.minecraft.world.phys.Vec3;

import static com.ccr4ft3r.actionsofstamina.config.ProfileConfig.*;

public class ServerPlayerData {

    private Vec3 lastPosition;
    private boolean isMoving;
    private boolean isFlying;
    private boolean isParagliding;
    private boolean startedMoving;
    private boolean isCrawling;
    private boolean isSprinting;
    private boolean isBlocking;

    private int crawlingTicks;
    private int flyingTicks;
    private int sprintingTicks;
    private int blockingTicks;
    private int paraglidingTicks;
    private int jumps;
    private double attacks;

    ServerPlayerData() {
    }

    public Vec3 getLastPosition() {
        return lastPosition;
    }

    public void setLastPosition(Vec3 lastPosition) {
        this.lastPosition = lastPosition;
    }

    public boolean isMoving() {
        if (startedMoving) {
            this.startedMoving = false;
            return true;
        }
        return isMoving;
    }

    public void setMoving(boolean moving) {
        if (moving && !isMoving)
            startedMoving = true;
        isMoving = moving;
    }

    public boolean isCrawling() {
        return isCrawling;
    }

    public void setCrawling(boolean crawling) {
        checkStarting(isCrawling, crawling);
        isCrawling = crawling;
        if (isCrawling)
            this.crawlingTicks++;
    }

    public boolean isFlying() {
        return isFlying;
    }

    public void setFlying(boolean flying) {
        checkStarting(isFlying, flying);
        isFlying = flying;
        if (isFlying)
            this.flyingTicks++;
    }

    public void setBlocking(boolean blocking) {
        checkStarting(isBlocking, blocking);
        isBlocking = blocking;
        if (isBlocking)
            this.blockingTicks++;
    }

    private void checkStarting(boolean currentValue, boolean newValue) {
        if (!currentValue && newValue)
            FeathersHelper.spendFeathers(getProfile().initialCosts.get());
    }

    public boolean isSprinting() {
        return isSprinting;
    }

    public boolean isBlocking() {
        return isBlocking;
    }

    public void setSprinting(boolean sprinting) {
        checkStarting(isSprinting, sprinting);
        isSprinting = sprinting;
        if (isSprinting)
            this.sprintingTicks++;
    }

    public void setParagliding(boolean paragliding) {
        checkStarting(isParagliding, paragliding);
        isParagliding = paragliding;
        if (isParagliding)
            this.paraglidingTicks++;
    }

    public int getSprintingTicks() {
        return sprintingTicks;
    }

    public void resetSprintingTicks() {
        this.sprintingTicks = 0;
    }

    public int getCrawlingTicks() {
        return crawlingTicks;
    }

    public void resetCrawlingTicks() {
        this.crawlingTicks = 0;
    }

    public void resetFlyingTicks() {
        this.flyingTicks = 0;
    }

    public void resetBlockingTicks() {
        this.blockingTicks = 0;
    }

    public void jump() {
        jumps++;
    }

    public void attack(double multiplier) {
        attacks += multiplier;
    }

    public void resetJumps() {
        jumps = 0;
    }

    public int getJumps() {
        return jumps;
    }

    public double getAttacks() {
        return attacks;
    }

    public void resetAttacks() {
        attacks -= getProfile().afterAttacking.get();
    }

    public void resetParaglidingTicks() {
        paraglidingTicks = 0;
    }

    public int getBlockingTicks() {
        return blockingTicks;
    }

    public int getFlyingTicks() {
        return flyingTicks;
    }

    public int getParaglidingTicks() {
        return paraglidingTicks;
    }

    public boolean isParagliding() {
        return isParagliding;
    }
}