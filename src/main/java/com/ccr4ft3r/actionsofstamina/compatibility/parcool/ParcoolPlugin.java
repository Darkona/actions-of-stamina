package com.ccr4ft3r.actionsofstamina.compatibility.parcool;

import com.alrex.parcool.common.action.impl.Crawl;
import com.alrex.parcool.common.capability.Parkourability;
import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import net.minecraft.world.entity.player.Player;

public class ParcoolPlugin {

    private static boolean hasParcoolCrawlPos(Player player) {
        if (ActionsOfStamina.PARCOOL)
            return false;
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null)
            return false;
        Crawl crawl = parkourability.get(Crawl.class);
        if (crawl == null)
            return false;
        return crawl.isDoing();
    }
}
