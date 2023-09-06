package com.github.alexthe666.iceandfire.client.render.entity.layer;

import com.github.alexthe666.iceandfire.api.IEntityEffectCapability;
import com.github.alexthe666.iceandfire.api.InFCapabilities;
import com.github.alexthe666.iceandfire.entity.EntityTroll;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;

public class LayerTrollStone implements LayerRenderer {

	private RenderLivingBase renderer;

	public LayerTrollStone(RenderLivingBase renderer) {
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
