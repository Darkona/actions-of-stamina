package com.ccr4ft3r.actionsofstamina.data;

import com.elenai.feathers.api.FeathersHelper;
import net.minecraft.world.phys.Vec3;

import static com.ccr4ft3r.actionsofstamina.config.ProfileConfig.*;

public class ServerPlayerData {

    private Vec3 lastPosition;
    private boolean isMoving;
    private boolean startedMoving;
    private boolean isCrawling;
    private boolean isSprinting;
    private boolean isBlocking;

    private int crawlingTicks;
    private int sprintingTicks;
    private int blockingTicks;
    private int jumps;

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

    public void resetBlockingTicks() {
        this.blockingTicks = 0;
    }

    public void jump() {
        jumps++;
    }

    public void resetJumps() {
        jumps = 0;
    }

    public int getJumps() {
        return jumps;
    }

    public int getBlockingTicks() {
        return blockingTicks;
    }
}