package com.ccr4ft3r.actionsofstamina.actions.minecraft.crawl;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.actions.Action;
import com.ccr4ft3r.actionsofstamina.data.PlayerActions;
import com.ccr4ft3r.actionsofstamina.config.AoSServerConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.UUID;


public class CrawlAction extends Action {

    public static final String actionName = "crawl_action";
    public static final ResourceLocation SOURCE = ActionsOfStamina.id("crawl");
    private static final UUID CRAWL_SPEED_MODIFIER_ID = UUID.fromString("0d3f6e0a-6c1b-4a8e-b2f1-7e5c9a4d2b18");
    private static final AttributeModifier CRAWL_SPEED_MODIFIER =
            new AttributeModifier(CRAWL_SPEED_MODIFIER_ID, "actionsofstamina:crawling_speed", -0.5, AttributeModifier.Operation.MULTIPLY_TOTAL);


    public CrawlAction() {
        super(SOURCE, AoSServerConfig.CRAWL);
    }


    @Override
    public String name() {
        return actionName;
    }

    @Override
    public int id() {
        return CRAWL;
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
