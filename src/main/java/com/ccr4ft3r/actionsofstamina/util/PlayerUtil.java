package com.ccr4ft3r.actionsofstamina.util;

import com.alrex.parcool.common.action.impl.Crawl;
import com.alrex.parcool.common.capability.Parkourability;
import com.ccr4ft3r.actionsofstamina.config.ActionType;
import com.ccr4ft3r.actionsofstamina.config.AoSAction;
import com.ccr4ft3r.actionsofstamina.data.ServerPlayerData;
import com.elenai.feathers.api.FeathersHelper;
import com.elenai.feathers.client.ClientFeathersData;
import com.mojang.logging.LogUtils;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.ModList;

import java.util.function.Supplier;

import static com.ccr4ft3r.actionsofstamina.ModConstants.*;
import static com.ccr4ft3r.actionsofstamina.config.MainConfig.*;
import static com.ccr4ft3r.actionsofstamina.config.ProfileConfig.*;
import static com.ccr4ft3r.actionsofstamina.data.ServerData.*;
import static com.ccr4ft3r.actionsofstamina.events.ClientHandler.*;
import static com.elenai.feathers.api.FeathersHelper.*;

public class PlayerUtil {

    public static boolean cannotBeExhausted(Player player) {
        return player instanceof FakePlayer || !player.isAlive()  || !player.isAddedToWorld() || //|| player.level().isClientSide
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
                LogUtils.getLogger().info("Spending {} feathers for player '{}' due to rule '{}'", feathersToSpend, player.getScoreboardName(),
                    String.join(".", optionEnabled.getPath()));
            FeathersHelper.spendFeathers(player, feathersToSpend);
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

    public static boolean hasEnoughFeathers(ForgeConfigSpec.IntValue costs, ForgeConfigSpec.IntValue min) {
        if (min.get() == 0)
            return true;
        int feathersMin = Math.max(costs.get(), min.get());
        return getFeathers() + getEndurance() - ClientFeathersData.getWeight() >= feathersMin;
    }

    public static boolean hasEnoughFeathers(ForgeConfigSpec.IntValue costs, ForgeConfigSpec.IntValue min, ServerPlayer player) {
        if (min.get() == 0)
            return true;
        int feathersMin = Math.max(costs.get(), min.get());
        return getFeathers(player) + getEndurance(player) - getPlayerWeight(player) >= feathersMin;
    }
}