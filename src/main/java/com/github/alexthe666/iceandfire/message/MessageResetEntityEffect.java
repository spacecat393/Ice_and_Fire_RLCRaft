package com.github.alexthe666.iceandfire.message;

import com.github.alexthe666.iceandfire.api.IEntityEffectCapability;
import com.github.alexthe666.iceandfire.api.InFCapabilities;
import com.github.alexthe666.iceandfire.capability.entityeffect.EntityEffectCapability;
import com.github.alexthe666.iceandfire.capability.entityeffect.EntityEffectStorage;
import io.netty.buffer.ByteBuf;
import net.ilexiconn.llibrary.server.network.AbstractMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageResetEntityEffect extends AbstractMessage<MessageResetEntityEffect> {
    private int entityId;

    public MessageResetEntityEffect() { }

    public MessageResetEntityEffect(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.entityId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.entityId);
    }

    public int getEntityId() {
        return this.entityId;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onClientReceived(Minecraft client, MessageResetEntityEffect message, EntityPlayer player, MessageContext messageContext) {
        Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.getEntityId());
        if (entity != null) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                Capability<IEntityEffectCapability> def = InFCapabilities.ENTITY_EFFECT;
                IEntityEffectCapability capability = entity.getCapability(def, null);
                if (capability != null) {
                    NBTTagCompound compound = new NBTTagCompound();
                    compound.setString(EntityEffectStorage.effectId, EntityEffectCapability.EntityEffectEnum.NONE.name());
                    compound.setInteger(EntityEffectStorage.effectTime, 0);
                    compound.setInteger(EntityEffectStorage.effectAdditionalData, 0);
                    def.getStorage().readNBT(def, capability, null, compound);
                }
            });
        }
    }

    @Override
    public void onServerReceived(MinecraftServer server, MessageResetEntityEffect message, EntityPlayer player, MessageContext messageContext) {}
}