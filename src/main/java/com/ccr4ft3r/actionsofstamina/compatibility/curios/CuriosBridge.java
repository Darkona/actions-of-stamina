package com.ccr4ft3r.actionsofstamina.compatibility.curios;

import com.ccr4ft3r.actionsofstamina.actions.minecraft.elytra.ElytraAction;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

import java.util.function.Predicate;

/** Direct Curios calls; only reached through {@link CuriosCompat} when Curios is loaded. */
final class CuriosBridge {

    private static final Predicate<ItemStack> STAMINA_WINGS = stack -> stack.is(ElytraAction.STAMINA_WINGS);

    private CuriosBridge() {
    }

    static void register() {
        MinecraftForge.EVENT_BUS.addListener(CuriosBridge::onCurioChange);
    }

    static boolean wearsStaminaWings(Player player) {
        return CuriosApi.getCuriosInventory(player).map(inventory -> inventory.isEquipped(STAMINA_WINGS)).orElse(false);
    }

    /** Server: a curio slot changed; only a change to or from stamina wings needs a new lookup. */
    private static void onCurioChange(CurioChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getFrom().is(ElytraAction.STAMINA_WINGS) || event.getTo().is(ElytraAction.STAMINA_WINGS)) {
            PlayerActions.get(player).refreshCurioWings(player);
        }
    }
}
