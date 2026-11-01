package com.ccr4ft3r.actionsofstamina.content.actions;

import java.util.UUID;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class CrawlAction extends Action {

    private static final UUID CRAWL_SPEED_MODIFIER_ID = UUID.fromString("0d3f6e0a-6c1b-4a8e-b2f1-7e5c9a4d2b18");
    private static final AttributeModifier CRAWL_SPEED_MODIFIER =
            new AttributeModifier(CRAWL_SPEED_MODIFIER_ID, "actionsofstamina:crawling_speed", -0.5, AttributeModifier.Operation.MULTIPLY_TOTAL);

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
        if (attr != null && attr.getModifier(CRAWL_SPEED_MODIFIER_ID) == null) {
            attr.addTransientModifier(CRAWL_SPEED_MODIFIER);
        }
    }

}
