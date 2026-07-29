package com.ccr4ft3r.actionsofstamina.actions.minecraft.crawl;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.actions.ActionType;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class CrawlAction extends Action {

    private static final ResourceLocation CRAWL_SPEED_MODIFIER_ID = ActionsOfStamina.id("crawling_speed");
    private static final AttributeModifier CRAWL_SPEED_MODIFIER =
            new AttributeModifier(CRAWL_SPEED_MODIFIER_ID, -0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL);

    public CrawlAction(ActionType type) {
        super(type);
    }

    private void removeModifier(Player p){
        var attr = p.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null) {
            attr.removeModifier(CRAWL_SPEED_MODIFIER_ID);
        }
    }

    @Override
    protected void finishPerforming(Player p, PlayerActions a) {
        super.finishPerforming(p, a);
        removeModifier(p);
    }

    @Override
    public void cleanUp(Player player) {
        removeModifier(player);
    }

    @Override
    protected void performingEffects(Player p, PlayerActions a) {
       removeModifier(p);
    }

    @Override
    protected void notPerformingEffects(Player player, PlayerActions a) {
        var attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null && !attr.hasModifier(CRAWL_SPEED_MODIFIER_ID)) {
            attr.addTransientModifier(CRAWL_SPEED_MODIFIER);
        }
    }

}
