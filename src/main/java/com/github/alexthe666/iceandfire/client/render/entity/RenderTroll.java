package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.api.IEntityEffectCapability;
import com.github.alexthe666.iceandfire.api.InFCapabilities;
import com.github.alexthe666.iceandfire.client.model.ModelTroll;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerStoneEntityCrack;
import com.github.alexthe666.iceandfire.entity.EntityGorgon;
import com.github.alexthe666.iceandfire.entity.EntityTroll;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderTroll extends RenderLiving<EntityTroll> implements ICustomStoneLayer {

	public RenderTroll(RenderManager renderManager) {
		super(renderManager, new ModelTroll(), 0.9F);
		this.layerRenderers.add(new LayerTrollWeapon(this));
		this.layerRenderers.add(new LayerTrollEyes(this));
	}

	@Override
	public void preRenderCallback(EntityTroll entitylivingbaseIn, float partialTickTime) { }

	@Override
	protected ResourceLocation getEntityTexture(EntityTroll troll) {
		return troll.getType().TEXTURE;
	}

	@Override
	public LayerRenderer<EntityLivingBase> getStoneLayer(RenderLivingBase<? extends EntityLivingBase> render) {
		return new LayerTrollStone(render);
	}

	@Override
	public LayerRenderer<EntityLivingBase> getCrackLayer(RenderLivingBase<? extends EntityLivingBase> render) {
		return new LayerStoneEntityCrack(render);
	}

	@SideOnly(Side.CLIENT)
	public static class LayerTrollWeapon implements LayerRenderer<EntityTroll> {

		private final RenderTroll renderer;

		public LayerTrollWeapon(RenderTroll renderer) {
			this.renderer = renderer;
		}

		@Override
		public void doRenderLayer(EntityTroll entity, float f, float f1, float i, float f2, float f3, float f4, float f5) {
			if (entity.getWeaponType() != null && !EntityGorgon.isStoneMob(entity)) {
				this.renderer.bindTexture(entity.getWeaponType().TEXTURE);
				this.renderer.getMainModel().render(entity, f, f1, f2, f3, f4, f5);
			}
		}

		@Override
		public boolean shouldCombineTextures() {
			return false;
		}
	}

	@SideOnly(Side.CLIENT)
	public static class LayerTrollEyes implements LayerRenderer<EntityTroll> {

		private final RenderTroll renderer;

		public LayerTrollEyes(RenderTroll renderer) {
			this.renderer = renderer;
		}

		@Override
		public void doRenderLayer(EntityTroll troll, float f, float f1, float i, float f2, float f3, float f4, float f5) {
			if (!EntityGorgon.isStoneMob(troll)) {
				this.renderer.bindTexture(troll.getType().TEXTURE_EYES);
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
				GlStateManager.disableLighting();
				GlStateManager.depthMask(!troll.isInvisible());
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 61680.0F, 0.0F);
				GlStateManager.enableLighting();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				this.renderer.getMainModel().render(troll, f, f1, f2, f3, f4, f5);
				this.renderer.setLightmap(troll);
				GlStateManager.depthMask(true);
				GlStateManager.disableBlend();
			}
		}

		@Override
		public boolean shouldCombineTextures() {
			return true;
		}
	}

	@SideOnly(Side.CLIENT)
	public static class LayerTrollStone implements LayerRenderer<EntityLivingBase> {

		private final RenderLivingBase<? extends EntityLivingBase> renderer;

		public LayerTrollStone(RenderLivingBase<? extends EntityLivingBase> renderer) {
			this.renderer = renderer;
		}

		@Override
		public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float f, float f1, float i, float f2, float f3, float f4, float f5) {
			if (entitylivingbaseIn instanceof EntityTroll) {
				EntityTroll troll = (EntityTroll)entitylivingbaseIn;
				IEntityEffectCapability capability = InFCapabilities.getEntityEffectCapability(troll);
				if (capability != null && capability.isStoned()) {
					GlStateManager.depthMask(true);
					GL11.glEnable(GL11.GL_CULL_FACE);
					this.renderer.bindTexture(troll.getType().TEXTURE_STONE);
					this.renderer.getMainModel().render(entitylivingbaseIn, f, 0, 0, f3, f4, f5);
					GL11.glDisable(GL11.GL_CULL_FACE);
				}
			}
		}

		@Override
		public boolean shouldCombineTextures() {
			return true;
		}
	}
}