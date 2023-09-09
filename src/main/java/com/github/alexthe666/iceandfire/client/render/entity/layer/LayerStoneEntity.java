package com.github.alexthe666.iceandfire.client.render.entity.layer;

import com.github.alexthe666.iceandfire.IceAndFireConfig;
import com.github.alexthe666.iceandfire.api.IEntityEffectCapability;
import com.github.alexthe666.iceandfire.api.InFCapabilities;
import com.github.alexthe666.iceandfire.client.model.ICustomStatueModel;
import com.github.alexthe666.iceandfire.client.model.ModelGuardianStatue;
import com.github.alexthe666.iceandfire.client.model.ModelHorseStatue;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Method;

public class LayerStoneEntity implements LayerRenderer {

	private static final ModelHorseStatue HORSE_MODEL = new ModelHorseStatue();
	private static final ModelGuardianStatue GUARDIAN_MODEL = new ModelGuardianStatue();
	private final RenderLivingBase renderer;

	public LayerStoneEntity(RenderLivingBase renderer) {
		this.renderer = renderer;
	}

	private static Method getEntityTexture;
	private static boolean reflected;

	private ResourceLocation stoneTexture;

	@Override
	public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float f, float f1, float i, float f2, float f3, float f4, float f5) {
		if(entitylivingbaseIn instanceof EntityLiving) {
			IEntityEffectCapability capability = InFCapabilities.getEntityEffectCapability(entitylivingbaseIn);
			if(capability != null && capability.isStoned()) {
				ResourceLocation textureBase = null;
				if(IceAndFireConfig.CLIENT_SETTINGS.customStoneTexture) {
					try {
						if(getEntityTexture == null && !reflected) {
							reflected = true;
							try {
								getEntityTexture = ObfuscationReflectionHelper.findMethod(Render.class, "func_110775_a", ResourceLocation.class, Entity.class);
								getEntityTexture.setAccessible(true);
							}
							catch(Exception ex) {
								ex.printStackTrace();
							}
						}
						if(getEntityTexture != null) {
							textureBase = (ResourceLocation)getEntityTexture.invoke(renderer, entitylivingbaseIn);
						}
					}
					catch(Exception ex) {
						ex.printStackTrace();
					}
				}

				GlStateManager.depthMask(true);
				GL11.glEnable(GL11.GL_CULL_FACE);

				if(this.stoneTexture == null) this.stoneTexture = new ResourceLocation(getStoneType(renderer.getMainModel()));
				this.renderer.bindTexture(this.stoneTexture);

				if (this.renderer.getMainModel() instanceof ICustomStatueModel) {
					((ICustomStatueModel) this.renderer.getMainModel()).renderStatue();
				} else if (entitylivingbaseIn instanceof AbstractHorse && !(entitylivingbaseIn instanceof EntityLlama)) {
					HORSE_MODEL.render(entitylivingbaseIn, f, 0, 0, f3, f4, f5);
				} else if (entitylivingbaseIn instanceof EntityGuardian) {
					GUARDIAN_MODEL.render(entitylivingbaseIn, f, 0, 0, f3, f4, f5);
				} else {
					this.renderer.getMainModel().render(entitylivingbaseIn, f, 0, 0, f3, f4, f5);
				}

				if(IceAndFireConfig.CLIENT_SETTINGS.customStoneTexture && textureBase != null) {
					this.renderer.bindTexture(textureBase);

					GlStateManager.enableBlend();
					GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
					OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
					GlStateManager.color(1.0F, 1.0F, 1.0F, 0.15F);
					GlStateManager.doPolygonOffset(-1.0F, -1.0F);
					GlStateManager.enablePolygonOffset();

					if (this.renderer.getMainModel() instanceof ICustomStatueModel) {
						((ICustomStatueModel) this.renderer.getMainModel()).renderStatue();
					} else if (entitylivingbaseIn instanceof AbstractHorse && !(entitylivingbaseIn instanceof EntityLlama)) {
						HORSE_MODEL.render(entitylivingbaseIn, f, 0, 0, f3, f4, f5);
					} else if (entitylivingbaseIn instanceof EntityGuardian) {
						GUARDIAN_MODEL.render(entitylivingbaseIn, f, 0, 0, f3, f4, f5);
					} else {
						this.renderer.getMainModel().render(entitylivingbaseIn, f, 0, 0, f3, f4, f5);
					}

					GlStateManager.doPolygonOffset(0.0F, 0.0F);
					GlStateManager.disablePolygonOffset();
				}

				GL11.glDisable(GL11.GL_CULL_FACE);
			}
		}
	}

	private String getStoneType(ModelBase model) {
		int sizeX = clampTexture(model.textureWidth);
		int sizeY = clampTexture(model.textureHeight);
		if(sizeX > sizeY && sizeX/2 != sizeY) sizeY = sizeX/2;
		if(sizeY > sizeX && sizeY/2 != sizeX) sizeX = sizeY/2;
		return sizeX <= 16 && sizeY <= 16 ? "textures/blocks/stone.png" : "iceandfire:textures/models/gorgon/stone" + sizeX + "x" + sizeY + ".png";
	}

	private int clampTexture(int i) {
		if(i >= 128) return 128;
		if(i >= 64) return 64;
		if(i >= 32) return 32;
		return 16;
	}

	@Override
	public boolean shouldCombineTextures() {
		return true;
	}
}