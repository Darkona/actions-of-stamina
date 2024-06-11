package com.ccr4ft3r.actionsofstamina.actions.minecraft.attack;

import com.ccr4ft3r.actionsofstamina.ModConstants;
import com.ccr4ft3r.actionsofstamina.config.ProfileConfig;
import com.ccr4ft3r.actionsofstamina.data.ServerPlayerData;
import com.ccr4ft3r.actionsofstamina.network.PacketHandler;
import com.ccr4ft3r.actionsofstamina.network.ServerboundPacket;
import com.google.common.collect.Multimap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Iterator;

import static com.ccr4ft3r.actionsofstamina.config.AoSAction.ATTACKING;
import static com.ccr4ft3r.actionsofstamina.config.ProfileConfig.getProfile;
import static com.ccr4ft3r.actionsofstamina.data.ServerData.getPlayerData;
import static com.ccr4ft3r.actionsofstamina.network.ServerboundPacket.Action.WEAPON_SWING;
import static com.ccr4ft3r.actionsofstamina.util.PlayerUtil.cannotBeExhausted;

@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID)
public class AttackHandler {

    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {


        var player = event.getEntity();

        boolean canAttack = ProfileConfig.canPlayerDo(player, ATTACKING);

        if (!canAttack) {
            event.setCanceled(true);
            return;
        }

        exhaustForWeaponSwing(player);

    }

    @SubscribeEvent
    public static void onPlayerAttack(PlayerInteractEvent.LeftClickEmpty event) {
        if(!getProfile().onlyForHits.get())
            PacketHandler.sendToServer(new ServerboundPacket(WEAPON_SWING));
    }

    public static void exhaustForWeaponSwing(Player p) {
        if (!getProfile().isActionAvailable.get(ATTACKING).get()) return;

        if (!(p instanceof ServerPlayer player) || cannotBeExhausted(player))
            return;

        ServerPlayerData playerData = getPlayerData(player);
        ItemStack itemstack = player.getItemInHand(InteractionHand.MAIN_HAND);
        Multimap<Attribute, AttributeModifier> modifiers = itemstack.getItem().getAttributeModifiers(EquipmentSlot.MAINHAND, itemstack);
        Iterator<AttributeModifier> attackDamages = modifiers.get(Attributes.ATTACK_DAMAGE).iterator();

        double multiplier = 0d;
        if (attackDamages.hasNext()) {
            AttributeModifier attackDamage = attackDamages.next();
            multiplier = getProfile().attackDamageMultiplier.get() * (attackDamage.getAmount() + 1);
        } else if (!getProfile().alsoForNonWeapons.get())
            return;
        /* ****** */
        playerData.set(ATTACKING, Math.max(0, 1 + multiplier), player);
    }
}
