package com.ccr4ft3r.actionsofstamina.actions.minecraft.crawl;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.capability.PlayerActions;
import com.ccr4ft3r.actionsofstamina.config.AoSCommonConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;

public class CrawlAction extends Action {

    public static final String actionName = "crawl_action";
    private final AttributeModifier crawlSpeedModifier = new AttributeModifier(CRAWL_SPEED_MODIFIER, "Crawling Speed Modifier", -0.5, AttributeModifier.Operation.MULTIPLY_TOTAL);
    private static final UUID CRAWL_SPEED_MODIFIER = UUID.fromString("f6975f3a-1834-4a1b-a7ed-d8519df974f8");


    public CrawlAction() {
        super(AoSCommonConfig.CRAWLING_COST.get(),
                AoSCommonConfig.CRAWLING_MINIMUM_COST.get(),
                AoSCommonConfig.CRAWLING_COOLDOWN.get(),
                AoSCommonConfig.CRAWLING_FEATHERS_PER_SECOND.get(),
                AoSCommonConfig.INHIBIT_REGEN_WHEN_CRAWLING.get(),
                0);
    }

    public CrawlAction(CompoundTag tag) {
        super(tag);
    }

    @Override
    public String name() {
        return actionName;
    }

    private void removeModifier(Player p){
        var attr = p.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null && attr.hasModifier(crawlSpeedModifier)) {
            attr.removeModifier(crawlSpeedModifier);
        }
    }

    @Override
    protected void finishPerforming(Player p, PlayerActions a) {
        super.finishPerforming(p, a);
        removeModifier(p);
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {
       removeModifier(p);
    }

    @Override
    protected void notPerformingEffects(Player player, PlayerActions a) {
        var attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null && !attr.hasModifier(crawlSpeedModifier)) {
            attr.addTransientModifier(crawlSpeedModifier);
        }
    }

}
