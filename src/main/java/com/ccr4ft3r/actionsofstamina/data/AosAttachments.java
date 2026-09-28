package com.ccr4ft3r.actionsofstamina.data;

import com.ccr4ft3r.actionsofstamina.ActionsOfStamina;
import com.ccr4ft3r.actionsofstamina.stamina.internal.InternalStamina;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class AosAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ActionsOfStamina.MOD_ID);

    /**
     * Per-player action state. Deliberately NOT serialized: the actions are rebuilt from the current config every
     * time a player entity joins a level (login, respawn, dimension change), so config edits apply right away.
     */
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<PlayerActions>> PLAYER_ACTIONS =
            ATTACHMENT_TYPES.register("player_actions", () -> AttachmentType.builder(PlayerActions::new).build());

    /** The internal stamina bar (used without Green Feathers). Saved; starts full after death. */
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<InternalStamina>> INTERNAL_STAMINA =
            ATTACHMENT_TYPES.register("internal_stamina",
                    () -> AttachmentType.builder(() -> new InternalStamina()).serialize(InternalStamina.CODEC).build());

    private AosAttachments() {
    }
}
