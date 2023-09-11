package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.model.util.IceAndFireTabulaModel;
import com.github.alexthe666.iceandfire.entity.EntityDragonSkull;
import com.github.alexthe666.iceandfire.enums.EnumDragonTextures;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderDragonSkull extends Render<EntityDragonSkull> {

	private static final float[][] growth_stages = new float[][]{{1F, 3F}, {3F, 7F}, {7F, 12.5F}, {12.5F, 20F}, {20F, 30F}};
	private final IceAndFireTabulaModel fireDragonModel;
	private final IceAndFireTabulaModel iceDragonModel;
	private final IceAndFireTabulaModel lightningDragonModel;

	public RenderDragonSkull(RenderManager renderManager, ModelBase fireDragonModel, ModelBase iceDragonModel, ModelBase lightningDragonModel) {
		super(renderManager);
		this.fireDragonModel = (IceAndFireTabulaModel)fireDragonModel;
		this.iceDragonModel	 = (IceAndFireTabulaModel)iceDragonModel;
		this.lightningDragonModel = (IceAndFireTabulaModel)lightningDragonModel;
	}

	@Override
	public void doRender(EntityDragonSkull entity, double x, double y, double z, float entityYaw, float partialTicks) {
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		GlStateManager.translate((float) x, (float) y, (float) z);
		GlStateManager.rotate(entity.getYaw(), 0, -1, 0);
		float f = 0.0625F;
		GlStateManager.enableRescaleNormal();
		GlStateManager.scale(1.0F, -1.0F, 1.0F);
		GlStateManager.enableAlpha();
		this.bindEntityTexture(entity);
		if (this.renderOutlines) {
			GlStateManager.enableColorMaterial();
			GlStateManager.enableOutlineMode(this.getTeamColor(entity));
		}
		float size = getRenderSize(entity) / 3;
		GlStateManager.scale(size, size, size);
		GlStateManager.translate(0,  entity.isOnWall() ? -0.24F : -0.12F, 0.5F);
		if(entity.getType() == 0){
			fireDragonModel.resetToDefaultPose();
			setRotationAngles(fireDragonModel.getCube("Head"), entity.isOnWall() ? (float)Math.toRadians(50F) : 0F, 0, 0);
			fireDragonModel.getCube("Head").render(0.0625F);
		}
		if(entity.getType() == 1){
			iceDragonModel.resetToDefaultPose();
			setRotationAngles(iceDragonModel.getCube("Head"), entity.isOnWall() ? (float)Math.toRadians(50F) : 0F, 0, 0);
			iceDragonModel.getCube("Head").render(0.0625F);
		}
		if(entity.getType() == 2){
			lightningDragonModel.resetToDefaultPose();
			setRotationAngles(lightningDragonModel.getCube("Head"), entity.isOnWall() ? (float)Math.toRadians(50F) : 0F, 0, 0);
			lightningDragonModel.getCube("Head").render(0.0625F);
		}
		if (this.renderOutlines) {
			GlStateManager.disableOutlineMode();
			GlStateManager.disableColorMaterial();
		}
		GlStateManager.popMatrix();
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	private static void setRotationAngles(ModelRenderer cube, float rotX, float rotY, float rotZ){
		cube.rotateAngleX = rotX;
		cube.rotateAngleY = rotY;
		cube.rotateAngleZ = rotZ;
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityDragonSkull entity) {
		if (entity.getType() == 1) {
			return EnumDragonTextures.getIceDragonSkullTextures(entity);
		} else if (entity.getType() == 2) {
			return EnumDragonTextures.getLightningDragonSkullTextures(entity);
		}
		return EnumDragonTextures.getFireDragonSkullTextures(entity);
	}

	public float getRenderSize(EntityDragonSkull skull) {
		float step = (growth_stages[skull.getDragonStage() - 1][1] - growth_stages[skull.getDragonStage() - 1][0]) / 25;
		if (skull.getDragonAge() > 125) {
			return growth_stages[skull.getDragonStage() - 1][0] + ((step * 25));
		}
		return growth_stages[skull.getDragonStage() - 1][0] + ((step * this.getAgeFactor(skull)));
	}

	private int getAgeFactor(EntityDragonSkull skull) {
		return (skull.getDragonStage() > 1 ? skull.getDragonAge() - (25 * (skull.getDragonStage() - 1)) : skull.getDragonAge());
	}
}