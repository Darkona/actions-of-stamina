package com.ccr4ft3r.actionsofstamina.index;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.content.actions.PlayerActions;
import com.ccr4ft3r.actionsofstamina.content.stamina.InternalStamina;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class ActionsAttachmentTypes {

    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ActionsOfStamina.MOD_ID);

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerActions>> PLAYER_ACTIONS =
            ATTACHMENT_TYPES.register("player_actions", () -> AttachmentType.builder(PlayerActions::new).build());

    public static final DeferredHolder<AttachmentType<?>, AttachmentType<InternalStamina>> INTERNAL_STAMINA =
            ATTACHMENT_TYPES.register("internal_stamina", () -> AttachmentType.builder(InternalStamina::new).serialize(InternalStamina.CODEC).build());

    private ActionsAttachmentTypes() {
    }

    public static void register(IEventBus modBus) {
        ATTACHMENT_TYPES.register(modBus);
    }
}
