package com.ccr4ft3r.actionsofstamina.util;

import com.alrex.parcool.common.action.impl.Crawl;
import com.alrex.parcool.common.capability.Parkourability;
import com.ccr4ft3r.actionsofstamina.config.ActionType;
import com.ccr4ft3r.actionsofstamina.config.AoSAction;
import com.ccr4ft3r.actionsofstamina.data.ServerPlayerData;
import com.mojang.logging.LogUtils;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.ModList;

import java.util.function.Supplier;

import static com.ccr4ft3r.actionsofstamina.ModConstants.PARCOOL_MOD_ID;
import static com.ccr4ft3r.actionsofstamina.config.MainConfig.CONFIG_DATA;
import static com.ccr4ft3r.actionsofstamina.config.ProfileConfig.getProfile;
import static com.ccr4ft3r.actionsofstamina.data.ServerData.getPlayerData;
import static com.ccr4ft3r.actionsofstamina.events.ClientHandler.PLAYER_DATA;

public class PlayerUtil {

    public static boolean cannotBeExhausted(Player player) {
        return player instanceof FakePlayer || player.level().isClientSide || !player.isAddedToWorld() || !player.isAlive() ||
                player.isCreative() || player.isSpectator();
    }

    public static void exhaust(ServerPlayer player, AoSAction action) {
        if (cannotBeExhausted(player))
            return;
        ForgeConfigSpec.BooleanValue optionEnabled = getProfile().enabledByAction.get(action);
        ServerPlayerData playerData = getPlayerData(player);
        Supplier<Boolean> shouldExhaust = () -> optionEnabled.get() && playerData.get(action) >= getProfile().delayByAction.get(action).get();
        if (shouldExhaust.get()) {
            int feathersToSpend = getProfile().costsByAction.get(action).get();
            if (CONFIG_DATA.enableExtendedLogging.get())
                LogUtils.getLogger().info("Spending {} feathers for player '{}' due to rule '{}'",
                        feathersToSpend, player.getScoreboardName(),
                    String.join(".", optionEnabled.getPath()));
            //FeathersAPI.spendFeathers(player, feathersToSpend, 0);
            playerData.reset(action);
        }
        if (action.getType() == ActionType.TIMES && shouldExhaust.get())
            exhaust(player, action);
    }

    public static boolean isCrawling(Player player) {
        boolean hasCrawlPos = !player.isInLava() && !player.isInWater() && player.getPose() == Pose.SWIMMING;
        ServerPlayerData playerData = getPlayerData(player);
        return (hasCrawlPos || hasParcoolCrawlPos(player)) && playerData.isMoving() && !player.position().equals(playerData.getLastPosition());
    }

    private static boolean hasParcoolCrawlPos(Player player) {
        if (!ModList.get().isLoaded(PARCOOL_MOD_ID))
            return false;
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null)
            return false;
        Crawl crawl = parkourability.get(Crawl.class);
        if (crawl == null)
            return false;
        return crawl.isDoing();
    }

    public static boolean isCrawling(LocalPlayer player) {
        boolean hasCrawlPos = !player.isInLava() && !player.isInWater() && player.getPose() == Pose.SWIMMING;
        return hasCrawlPos && PLAYER_DATA.isMoving();
    }

    public static boolean hasEnoughFeathers(int costs, int min, Player player) {
        if (min == 0) return true;
        int feathersMin = Math.max(costs, min);
        //int capacity = FeathersAPI.getAvailableFeathers(player);
        //return capacity >= feathersMin;
        return true;
    }
}