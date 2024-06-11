package com.ccr4ft3r.actionsofstamina.util;

import com.alrex.parcool.common.action.impl.Crawl;
import com.alrex.parcool.common.capability.Parkourability;
import com.ccr4ft3r.actionsofstamina.config.ActionType;
import com.ccr4ft3r.actionsofstamina.config.AoSAction;
import com.ccr4ft3r.actionsofstamina.data.ServerPlayerData;
import com.darkona.feathers.api.FeathersAPI;
import com.mojang.logging.LogUtils;
import net.minecraft.client.player.LocalPlayer;
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
        return player instanceof FakePlayer || !player.isAddedToWorld() || !player.isAlive() ||
                player.isCreative() || player.isSpectator();
    }

    public static void exhaust(Player player, AoSAction action) {
        if (cannotBeExhausted(player))
            return;

        ServerPlayerData playerData = getPlayerData(player);
        boolean shouldExhaust = playerData.get(action) >= getProfile().delayByAction.get(action).get();
        int feathersToSpend = getProfile().costsByAction.get(action).get();

        if (shouldExhaust) {
            if (CONFIG_DATA.enableExtendedLogging.get())
                LogUtils.getLogger().info("Spending {} feathers for player '{}'",
                        feathersToSpend, player.getScoreboardName());

            FeathersAPI.spendFeathers(player, feathersToSpend, 20);
            playerData.reset(action);
        }
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
        return FeathersAPI.getAvailableFeathers(player) >= feathersMin;
    }
}