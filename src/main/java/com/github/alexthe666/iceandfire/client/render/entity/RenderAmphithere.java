package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.ModelAmphithere;
import com.github.alexthe666.iceandfire.entity.EntityAmphithere;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderAmphithere extends RenderLiving<EntityAmphithere> {

	private static final ResourceLocation TEXTURE_BLUE = new ResourceLocation("iceandfire:textures/models/amphithere/amphithere_blue.png");
	private static final ResourceLocation TEXTURE_BLUE_BLINK = new ResourceLocation("iceandfire:textures/models/amphithere/amphithere_blue_blink.png");
	private static final ResourceLocation TEXTURE_GREEN = new ResourceLocation("iceandfire:textures/models/amphithere/amphithere_green.png");
	private static final ResourceLocation TEXTURE_GREEN_BLINK = new ResourceLocation("iceandfire:textures/models/amphithere/amphithere_green_blink.png");
	private static final ResourceLocation TEXTURE_OLIVE = new ResourceLocation("iceandfire:textures/models/amphithere/amphithere_olive.png");
	private static final ResourceLocation TEXTURE_OLIVE_BLINK = new ResourceLocation("iceandfire:textures/models/amphithere/amphithere_olive_blink.png");
	private static final ResourceLocation TEXTURE_RED = new ResourceLocation("iceandfire:textures/models/amphithere/amphithere_red.png");
	private static final ResourceLocation TEXTURE_RED_BLINK = new ResourceLocation("iceandfire:textures/models/amphithere/amphithere_red_blink.png");
	private static final ResourceLocation TEXTURE_YELLOW = new ResourceLocation("iceandfire:textures/models/amphithere/amphithere_yellow.png");
	private static final ResourceLocation TEXTURE_YELLOW_BLINK = new ResourceLocation("iceandfire:textures/models/amphithere/amphithere_yellow_blink.png");

	public RenderAmphithere(RenderManager renderManager) {
		super(renderManager, new ModelAmphithere(), 1.6F);
	}

	@Override
	public void preRenderCallback(EntityAmphithere entitylivingbaseIn, float partialTickTime) {
		GL11.glScalef(2.0F, 2.0F, 2.0F);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityAmphithere amphithere) {
		switch(amphithere.getVariant()) {
			case 0: return amphithere.isBlinking() ? TEXTURE_BLUE_BLINK : TEXTURE_BLUE;
			case 1: return amphithere.isBlinking() ? TEXTURE_GREEN_BLINK : TEXTURE_GREEN;
			case 2: return amphithere.isBlinking() ? TEXTURE_OLIVE_BLINK : TEXTURE_OLIVE;
			case 3: return amphithere.isBlinking() ? TEXTURE_RED_BLINK : TEXTURE_RED;
			case 4: return amphithere.isBlinking() ? TEXTURE_YELLOW_BLINK : TEXTURE_YELLOW;
			default: return TEXTURE_GREEN;
		}
	}
}