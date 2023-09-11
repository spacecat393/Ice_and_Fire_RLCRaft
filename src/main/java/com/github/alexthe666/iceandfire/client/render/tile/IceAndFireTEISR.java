package com.github.alexthe666.iceandfire.client.render.tile;

import com.github.alexthe666.iceandfire.client.model.ModelTrollWeapon;
import com.github.alexthe666.iceandfire.item.ItemTrollWeapon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class IceAndFireTEISR extends TileEntityItemStackRenderer {

    private static final ModelBase MODEL = new ModelTrollWeapon();

    @Override
    public void renderByItem(ItemStack itemStackIn) {
        if(itemStackIn.getItem() instanceof ItemTrollWeapon){
            ItemTrollWeapon weaponItem = (ItemTrollWeapon)itemStackIn.getItem();
            GL11.glPushMatrix();
            GL11.glTranslatef(0.5F, -0.75F, 0.5F);
            GL11.glPushMatrix();
            Minecraft.getMinecraft().getTextureManager().bindTexture(weaponItem.weapon.TEXTURE);
            GL11.glPushMatrix();
            MODEL.render(null, 0, 0, 0, 0, 0, 0.0625F);
            GL11.glPopMatrix();
            GL11.glPopMatrix();
            GL11.glPopMatrix();
        }
    }
}