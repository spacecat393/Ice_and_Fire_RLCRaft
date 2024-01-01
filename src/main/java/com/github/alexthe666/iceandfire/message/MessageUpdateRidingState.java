package com.github.alexthe666.iceandfire.message;

import com.github.alexthe666.iceandfire.entity.util.ISyncMount;
import io.netty.buffer.ByteBuf;
import net.ilexiconn.llibrary.server.network.AbstractMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MessageUpdateRidingState extends AbstractMessage<MessageUpdateRidingState> {

    public int entityId;
    public boolean riding;

    public MessageUpdateRidingState(int entityId, boolean riding) {
        this.entityId = entityId;
        this.riding = riding;
    }

    public MessageUpdateRidingState() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        entityId = buf.readInt();
        riding = buf.readBoolean();

    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeBoolean(riding);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onClientReceived(Minecraft client, MessageUpdateRidingState message, EntityPlayer player, MessageContext messageContext) {
    }

    @Override
    public void onServerReceived(MinecraftServer server, MessageUpdateRidingState message, EntityPlayer player, MessageContext messageContext) {
        if (player.world != null) {
            Entity entity = player.world.getEntityByID(message.entityId);
            if (entity instanceof ISyncMount) {
                if (message.riding) {
                    player.startRiding(entity, true);
                } else if (entity.equals(player.getRidingEntity())) {
                    player.dismountRidingEntity();
                }
            }
        }
    }
}