package com.github.alexthe666.iceandfire.client.render.entity;

import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerDragonArmor;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerDragonEyes;
import com.github.alexthe666.iceandfire.client.render.entity.layer.LayerDragonRider;
import com.github.alexthe666.iceandfire.client.texture.ArrayLayeredTexture;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.enums.EnumDragonTextures;
import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RenderDragonBase extends RenderLiving<EntityDragonBase> {

	private Map<String, ResourceLocation> LAYERED_TEXTURE_CACHE = Maps.newHashMap();
	private final int dragonType;

	public RenderDragonBase(RenderManager renderManager, ModelBase model, int dragonType) {
		super(renderManager, model, 0.8F);
		this.addLayer(new LayerDragonEyes(this));
		this.addLayer(new LayerDragonRider(this));
		this.addLayer(new LayerDragonArmor(this));
		this.dragonType = dragonType;
	}

	public boolean shouldRender(EntityDragonBase dragon, ICamera camera, double camX, double camY, double camZ) {
		return true;
	}

	@Override
	protected void preRenderCallback(EntityDragonBase entity, float f) {
		this.shadowSize = entity.getRenderSize() / 3;
		GL11.glScalef(shadowSize, shadowSize, shadowSize);
	}

	protected ResourceLocation getEntityTexture(EntityDragonBase entity) {
		String baseTexture = entity.getVariantName(entity.getVariant()) + " " + entity.getDragonStage() + entity.isModelDead() + entity.isMale() + entity.isSkeletal() + entity.isSleeping() + entity.isBlinking();
		ResourceLocation resourcelocation = LAYERED_TEXTURE_CACHE.get(baseTexture);
		if (resourcelocation == null) {
			resourcelocation = EnumDragonTextures.getTextureFromDragon(entity);
			List<String> tex = new ArrayList<String>();
			tex.add(resourcelocation.toString());
			if (entity.isMale() && !entity.isSkeletal()) {
				if (dragonType == 1) {
					tex.add(EnumDragonTextures.getDragonEnum(entity).ICE_MALE_OVERLAY.toString());
				} else if (dragonType == 2) {
					tex.add(EnumDragonTextures.getDragonEnum(entity).LIGHTNING_MALE_OVERLAY.toString());
				} else {
					tex.add(EnumDragonTextures.getDragonEnum(entity).FIRE_MALE_OVERLAY.toString());
				}
			} else {
				tex.add(EnumDragonTextures.Armor.EMPTY.FIRETEXTURE.toString());
			}
			ArrayLayeredTexture layeredBase = new ArrayLayeredTexture(tex);
			Minecraft.getMinecraft().getTextureManager().loadTexture(resourcelocation, layeredBase);
			LAYERED_TEXTURE_CACHE.put(baseTexture, resourcelocation);
		}
		return resourcelocation;
	}
}
