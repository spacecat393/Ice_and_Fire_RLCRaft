package com.github.alexthe666.iceandfire.capability.entityeffect;

import com.github.alexthe666.iceandfire.api.IEntityEffectCapability;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class EntityEffectProvider implements ICapabilitySerializable<NBTBase> {

    @CapabilityInject(IEntityEffectCapability.class)
    public static final Capability<IEntityEffectCapability> ENTITY_EFFECT = null;

    private final IEntityEffectCapability instance = ENTITY_EFFECT.getDefaultInstance();

    @Override
    public boolean hasCapability(Capability<?> requested, EnumFacing facing) {
        return requested == ENTITY_EFFECT;
    }

    @Override
    public <T> T getCapability(Capability<T> requested, EnumFacing facing) {
        return requested == ENTITY_EFFECT ? ENTITY_EFFECT.cast(this.instance) : null;
    }

    @Override
    public NBTBase serializeNBT() {
        return writeNBT(this.instance, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        readNBT(this.instance, null, nbt);
    }

    public static NBTBase writeNBT(IEntityEffectCapability capability, EnumFacing side) {
        return ENTITY_EFFECT.writeNBT(capability, side);
    }

    public static void readNBT(IEntityEffectCapability capability, EnumFacing side, NBTBase nbt) {
        ENTITY_EFFECT.readNBT(capability, side, nbt);
    }
}