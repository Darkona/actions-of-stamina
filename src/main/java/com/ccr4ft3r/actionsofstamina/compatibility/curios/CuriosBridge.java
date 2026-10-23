package com.ccr4ft3r.actionsofstamina.compatibility.curios;

import com.ccr4ft3r.actionsofstamina.actions.ElytraAction;
import java.util.function.Predicate;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.NeoForge;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.event.CurioChangeEvent;

final class CuriosBridge {

    private static final Predicate<ItemStack> STAMINA_WINGS = stack -> stack.is(ElytraAction.STAMINA_WINGS);

    private CuriosBridge() {
    }

    static void register() {
        NeoForge.EVENT_BUS.addListener(CuriosBridge::onCurioChange);
    }

    static boolean wearsStaminaWings(Player player) {
        return CuriosApi.getCuriosInventory(player).map(inventory -> inventory.isEquipped(STAMINA_WINGS)).orElse(false);
    }

    private static void onCurioChange(CurioChangeEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getFrom().is(ElytraAction.STAMINA_WINGS) || event.getTo().is(ElytraAction.STAMINA_WINGS)) {
            CuriosCompat.refreshWings(player);
        }
    }
}
