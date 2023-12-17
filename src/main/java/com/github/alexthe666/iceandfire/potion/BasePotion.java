package com.github.alexthe666.iceandfire.potion;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.core.ModPotions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BasePotion extends Potion {

    protected final ResourceLocation icon;
    protected String name;

    public BasePotion(String name, int color, boolean isBadEffect) {
        super(isBadEffect, color);
        this.name = name;
        this.icon = new ResourceLocation(IceAndFire.MODID, "textures/potions/" + name + ".png");
        this.setPotionName(IceAndFire.MODID + ".effect." + name);
        this.setRegistryName(new ResourceLocation(IceAndFire.MODID, name));
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderInventoryEffect(PotionEffect effect, Gui gui, int x, int y, float z) {
        this.renderInventoryEffect(x, y, effect, Minecraft.getMinecraft());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
        if (this.getTexture() == null) {
            return;
        }
        mc.getTextureManager().bindTexture(this.getTexture());
        Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0, 0, 18, 18, 18, 18);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderHUDEffect(PotionEffect effect, Gui gui, int x, int y, float z, float alpha) {
        this.renderHUDEffect(x, y, effect, Minecraft.getMinecraft(), alpha);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
        if (this.getTexture() == null) {
            return;
        }
        mc.getTextureManager().bindTexture(this.getTexture());
        Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0, 0, 18, 18, 18, 18);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        if (this == ModPotions.acid)
        {
            int j = 25 >> amplifier;

            if (j > 0)
            {
                return duration % j == 0;
            }
            else
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        if (this == ModPotions.acid) {
            entity.attackEntityFrom(IceAndFire.acid, 1.0F);
        }
    }


    public ResourceLocation getTexture() {
        return icon;
    }

    @Override
    public boolean hasStatusIcon() {
        return icon != null;
    }

    @Override
    public boolean shouldRenderHUD(PotionEffect effect) {
        return this.hasStatusIcon();
    }

    @Override
    public boolean shouldRender(PotionEffect effect) {
        return this.hasStatusIcon();
    }

    @Override
    public boolean shouldRenderInvText(PotionEffect effect) {
        return this.hasStatusIcon();
    }
}
