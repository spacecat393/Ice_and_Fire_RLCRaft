package com.github.alexthe666.iceandfire.api;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class InFCapabilities {
    @CapabilityInject(IEntityEffectCapability.class)
    public static final Capability<IEntityEffectCapability> ENTITY_EFFECT = null;

    public static final String ENTITY_EFFECT_IDENTIFIER = "entity_effect";

    public static IEntityEffectCapability getEntityEffectCapability(EntityLivingBase entity) {
        return entity.getCapability(ENTITY_EFFECT, null);
    }
}