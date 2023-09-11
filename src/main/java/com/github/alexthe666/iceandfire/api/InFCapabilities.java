package com.github.alexthe666.iceandfire.api;

import com.github.alexthe666.iceandfire.capability.entityeffect.EntityEffectProvider;
import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class InFCapabilities {

    public static IEntityEffectCapability getEntityEffectCapability(EntityLivingBase entity) {
        return entity.getCapability(EntityEffectProvider.ENTITY_EFFECT, null);
    }
}