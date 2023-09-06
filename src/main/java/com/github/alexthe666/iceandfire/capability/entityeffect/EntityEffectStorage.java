package com.github.alexthe666.iceandfire.capability.entityeffect;

import com.github.alexthe666.iceandfire.api.IEntityEffectCapability;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class EntityEffectStorage implements Capability.IStorage<IEntityEffectCapability> {
    public static final String effectId = "effectId";
    public static final String effectTime = "effectTime";
    public static final String effectAdditionalData = "effectAdditionalData";

    @Override
    public NBTBase writeNBT(Capability<IEntityEffectCapability> capability, IEntityEffectCapability instance, EnumFacing side) {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setString(effectId, instance.getEffect().name());
        compound.setInteger(effectTime, instance.getTime());
        compound.setInteger(effectAdditionalData, instance.getAdditionalData());
        return compound;
    }

    @Override
    public void readNBT(Capability<IEntityEffectCapability> capability, IEntityEffectCapability instance, EnumFacing side, NBTBase nbt) {
        if(nbt instanceof NBTTagCompound) {
            NBTTagCompound compound = (NBTTagCompound)nbt;
            if(compound.hasKey(effectId)) {
                instance.setEffect(
                        EntityEffectCapability.EntityEffectEnum.valueOf(compound.getString(effectId)),
                        compound.hasKey(effectTime) ? compound.getInteger(effectTime) : 0,
                        compound.hasKey(effectAdditionalData) ? compound.getInteger(effectAdditionalData) : 0);
            }
            else {
                instance.reset();
            }
        }
    }
}