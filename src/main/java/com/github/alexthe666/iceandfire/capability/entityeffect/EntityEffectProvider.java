package com.github.alexthe666.iceandfire.capability.entityeffect;

import com.github.alexthe666.iceandfire.api.IEntityEffectCapability;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

public class EntityEffectProvider implements ICapabilitySerializable<NBTBase> {

    private final Capability<IEntityEffectCapability> capability;
    private final IEntityEffectCapability instance;

    public EntityEffectProvider(Capability<IEntityEffectCapability> newCap) {
        this.capability = newCap;
        this.instance = this.capability.getDefaultInstance();
    }

    @Override
    public boolean hasCapability(Capability<?> requested, EnumFacing facing) {
        return requested == this.capability;
    }

    @Override
    public <T> T getCapability(Capability<T> requested, EnumFacing facing) {
        if(requested == this.capability) return this.capability.cast(this.instance);
        return null;
    }

    @Override
    public NBTBase serializeNBT() {
        return this.capability.writeNBT(this.instance, null);
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        this.capability.readNBT(this.instance, null, nbt);
    }
}