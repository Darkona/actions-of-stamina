package com.ccr4ft3r.actionsofstamina.util;

import com.ccr4ft3r.actionsofstamina.data.ServerPlayerData;
import com.elenai.feathers.api.FeathersHelper;
import com.elenai.feathers.client.ClientFeathersData;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.util.FakePlayer;

import static com.ccr4ft3r.actionsofstamina.data.ServerData.*;
import static com.ccr4ft3r.actionsofstamina.events.ClientHandler.*;
import static com.elenai.feathers.api.FeathersHelper.*;

public class PlayerUtil {

    public static boolean cannotBeExhausted(Player player) {
        return player instanceof FakePlayer || player.getLevel().isClientSide() || !player.isAddedToWorld() ||
            player.isCreative() || player.isSpectator();
    }

    public static void exhaust(Player player, ForgeConfigSpec.BooleanValue optionEnabled, boolean onlyIf,
                               ForgeConfigSpec.IntValue feathersToSpend, Runnable resetter) {
        if (cannotBeExhausted(player))
            return;
        if (optionEnabled.get() && onlyIf) {
            FeathersHelper.spendFeathers((ServerPlayer) player, feathersToSpend.get());
            resetter.run();
        }
    }

    public static boolean isCrawling(Player player) {
        boolean hasCrawlPos = !player.isInLava() && !player.isInWater() && player.getPose() == Pose.SWIMMING;
        ServerPlayerData playerData = getPlayerData(player);
        return hasCrawlPos && playerData.isMoving() && !player.position().equals(playerData.getLastPosition());
    }

    public static boolean isCrawling(LocalPlayer player) {
        boolean hasCrawlPos = !player.isInLava() && !player.isInWater() && player.getPose() == Pose.SWIMMING;
        return hasCrawlPos && PLAYER_DATA.isMoving();
    }

    public static boolean hasEnoughFeathers(ForgeConfigSpec.IntValue costs, ForgeConfigSpec.IntValue min) {
        int feathersMin = Math.max(costs.get(), min.get());
        return getFeathers() + getEndurance() - ClientFeathersData.getWeight() >= feathersMin;
    }

    public static boolean hasEnoughFeathers(ForgeConfigSpec.IntValue costs, ForgeConfigSpec.IntValue min, ServerPlayer player) {
        int feathersMin = Math.max(costs.get(), min.get());
        return getFeathers(player) + getEndurance(player) - getPlayerWeight(player) >= feathersMin;
    }
}