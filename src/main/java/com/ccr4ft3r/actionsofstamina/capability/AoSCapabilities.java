package com.ccr4ft3r.actionsofstamina.capability;

import com.ccr4ft3r.actionsofstamina.IActionCapability;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;

public class AoSCapabilities {

    public static Capability<IActionCapability> PLAYER_ACTIONS = CapabilityManager.get(new CapabilityToken<>() {
    });


}
