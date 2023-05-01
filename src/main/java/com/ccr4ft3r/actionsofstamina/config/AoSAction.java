package com.ccr4ft3r.actionsofstamina.config;

import com.google.common.base.CaseFormat;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import tictim.paraglider.capabilities.Caps;

import java.util.function.Consumer;

import static com.ccr4ft3r.actionsofstamina.config.ActionType.*;
import static com.ccr4ft3r.actionsofstamina.util.ArrayUtil.*;

public enum AoSAction {

    ATTACKING(TIMES, of(false, true, true), of(2, 4, 6), of(3, 2, 1)),
    SPRINTING(TICKS, of(true, true, true), of(2, 3, 4), of(75, 50, 25)),
    FLYING(TICKS, of(true, true, true), of(2, 3, 4), of(50, 30, 20), (player -> {
        player.getAbilities().flying = false;
        player.getAbilities().mayfly = false;
        player.onUpdateAbilities();
    }), "for Elytra and Angel Ring"),
    PARAGLIDING(TICKS, of(true, true, true), of(2, 3, 4), of(50, 30, 20), (p) -> p.getCapability(Caps.playerMovement).ifPresent(m -> m.setDepleted(true))),
    CRAWLING(TICKS, of(true, true, true), of(1, 2, 3), of(65, 35, 20), "for Vanilla, Parcool, GoProne & Personality Mod"),
    BLOCKING(TIMES, of(false, true, true), of(1, 2, 3), of(1, 2, 3)),
    SWIMMING(TICKS, of(false, true, true), of(2, 3, 4), of(50, 30, 20)),
    JUMPING(TIMES, of(false, true, true), of(0, 0, 1), of(1, 1, 1)),
    HOLDING_THE_SHIELD(TICKS, of(true, true, true), of(1, 2, 3), of(45, 20, 12), Player::stopUsingItem),
    WALL_JUMPING(TIMES, of(false, true, true), of(1, 2, 3), of(1, 2, 3)),
    CRANKING(TICKS, of(false, true, true), of(1, 1, 2), of(75, 50, 25), "for Create")

    //
    ;

    private final ActionType type;
    private final Boolean[] enabledByProfile;
    private final Consumer<ServerPlayer> stopper;
    private final Integer[] costsByProfile;
    private final Integer[] minByProfile;
    private final Integer[] delayByProfile;
    private final String description;

    AoSAction(ActionType type, Boolean[] enabledByProfile, Integer[] minByProfile, Integer[] delayByProfile, String... description) {
        this(type, enabledByProfile, minByProfile, delayByProfile, null, description);
    }

    AoSAction(ActionType type, Boolean[] enabledByProfile, Integer[] minByProfile, Integer[] delayByProfile,
              Consumer<ServerPlayer> stopper, String... description) {
        this.type = type;
        this.enabledByProfile = enabledByProfile;
        this.stopper = stopper;
        this.costsByProfile = of(1, 1, 1);
        this.minByProfile = minByProfile;
        this.delayByProfile = delayByProfile;
        this.description = description.length > 0 ? " (" + description[0] + ")" : "";
        if (type == TICKS)
            TIME_ACTIONS.add(this);
    }

    String getText(boolean forDescription) {
        if (forDescription)
            return name().toLowerCase().replace("_", " ");
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
    }

    public ActionType getType() {
        return type;
    }

    public boolean isEnabled(AoSProfile profile) {
        return get(profile, enabledByProfile);
    }

    public Integer getCosts(AoSProfile profile) {
        return get(profile, costsByProfile);
    }

    public Integer getMin(AoSProfile profile) {
        return get(profile, minByProfile);
    }

    public Integer getDelay(AoSProfile profile) {
        return get(profile, delayByProfile);
    }

    public String getDescription() {
        return description;
    }

    public Consumer<ServerPlayer> getStopper() {
        return stopper;
    }
}