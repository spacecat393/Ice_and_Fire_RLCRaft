package com.github.alexthe666.iceandfire.client.render.entity;

import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

@SideOnly(Side.CLIENT)
public class RenderModCapes {
	public ResourceLocation redTex = new ResourceLocation("iceandfire", "textures/models/misc/cape_fire.png");
	public ResourceLocation redElytraTex = new ResourceLocation("iceandfire", "textures/models/misc/elytra_fire.png");
	public ResourceLocation blueTex = new ResourceLocation("iceandfire", "textures/models/misc/cape_ice.png");
	public ResourceLocation blueElytraTex = new ResourceLocation("iceandfire", "textures/models/misc/elytra_ice.png");
	public ResourceLocation betaTex = new ResourceLocation("iceandfire", "textures/models/misc/cape_beta.png");
	public ResourceLocation betaElytraTex = new ResourceLocation("iceandfire", "textures/models/misc/elytra_beta.png");
	public UUID[] redcapes = new UUID[]{
	/* zeklo */UUID.fromString("59efccaf-902d-45da-928a-5a549b9fd5e0"),
	/* Alexthe666 */UUID.fromString("71363abe-fd03-49c9-940d-aae8b8209b7c")};
	public UUID[] bluecapes = new UUID[]{
	/* Raptorfarian */UUID.fromString("0ed918c8-d612-4360-b711-cd415671356f"),
	/*Zyranna*/		UUID.fromString("5d43896a-06a0-49fb-95c5-38485c63667f")};
	public UUID[] betatesters = new UUID[]{
	};

	private static Field playerInfoField;
	private static Field playerTexturesField;

	@SubscribeEvent
	public void playerRender(RenderPlayerEvent.Pre event) {
		if(event.getEntityPlayer() instanceof AbstractClientPlayer) {
			NetworkPlayerInfo info = null;

			try {
				if(playerInfoField == null) playerInfoField = ObfuscationReflectionHelper.findField(AbstractClientPlayer.class, "field_175157_a");
				info = (NetworkPlayerInfo)playerInfoField.get(event.getEntityPlayer());
			}
			catch(IllegalArgumentException | IllegalAccessException var7) {
				var7.printStackTrace();
			}

			if(info != null) {
				Map<MinecraftProfileTexture.Type, ResourceLocation> textureMap = null;

				try {
					if(playerTexturesField == null) playerTexturesField = ObfuscationReflectionHelper.findField(NetworkPlayerInfo.class, "field_187107_a");
					textureMap = (Map)playerTexturesField.get(info);
				}
				catch(IllegalArgumentException | IllegalAccessException var5) {
					var5.printStackTrace();
				}

				if(textureMap != null) {
					if(this.hasBetaCape(event.getEntityPlayer().getUniqueID())) {
						textureMap.put(MinecraftProfileTexture.Type.CAPE, this.betaTex);
						textureMap.put(MinecraftProfileTexture.Type.ELYTRA, this.betaElytraTex);
					}

					if(this.hasRedCape(event.getEntityPlayer().getUniqueID())) {
						textureMap.put(MinecraftProfileTexture.Type.CAPE, this.redTex);
						textureMap.put(MinecraftProfileTexture.Type.ELYTRA, this.redElytraTex);
					}

					if(this.hasBlueCape(event.getEntityPlayer().getUniqueID())) {
						textureMap.put(MinecraftProfileTexture.Type.CAPE, this.blueTex);
						textureMap.put(MinecraftProfileTexture.Type.ELYTRA, this.blueElytraTex);
					}
				}
			}
		}
		/*
		 * if(event.getEntityPlayer() instanceof AbstractClientPlayer){ if
		 * (hasRedCape(event.getEntityPlayer().getUniqueID())){
		 * AbstractClientPlayer player =
		 * (AbstractClientPlayer)event.getEntityPlayer(); Field field =
		 * ReflectionHelper.findField(AbstractClientPlayer.class,
		 * ObfuscationReflectionHelper
		 * .remapFieldNames(AbstractClientPlayer.class.getName(), playerInfo));
		 * try { Field modifier = Field.class.getDeclaredField("modifiers");
		 * modifier.setAccessible(true); modifier.setInt(field,
		 * field.getModifiers() & ~Modifier.FINAL); NetworkPlayerInfo info =
		 * (NetworkPlayerInfo)field.get(player); Field field_capes =
		 * ReflectionHelper.findField(NetworkPlayerInfo.class,
		 * ObfuscationReflectionHelper
		 * .remapFieldNames(NetworkPlayerInfo.class.getName(), cape)); try {
		 * Field modifier_i = Field.class.getDeclaredField("modifiers");
		 * modifier_i.setAccessible(true); modifier_i.setInt(field_capes,
		 * field_capes.getModifiers() & ~Modifier.FINAL); field_capes.set(info,
		 * redTex); } catch (Exception exception) { exception.printStackTrace();
		 * } } catch (Exception exception) { exception.printStackTrace(); }
		 * 
		 * } else if (hasBlueCape(event.getEntityPlayer().getUniqueID())){
		 * AbstractClientPlayer player =
		 * (AbstractClientPlayer)event.getEntityPlayer(); Field field =
		 * ReflectionHelper.findField(AbstractClientPlayer.class,
		 * ObfuscationReflectionHelper
		 * .remapFieldNames(AbstractClientPlayer.class.getName(), playerInfo));
		 * try { Field modifier = Field.class.getDeclaredField("modifiers");
		 * modifier.setAccessible(true); modifier.setInt(field,
		 * field.getModifiers() & ~Modifier.FINAL); NetworkPlayerInfo info =
		 * (NetworkPlayerInfo)field.get(player); Field field_capes =
		 * ReflectionHelper.findField(NetworkPlayerInfo.class,
		 * ObfuscationReflectionHelper
		 * .remapFieldNames(NetworkPlayerInfo.class.getName(), cape)); try {
		 * Field modifier_i = Field.class.getDeclaredField("modifiers");
		 * modifier_i.setAccessible(true); modifier_i.setInt(field_capes,
		 * field_capes.getModifiers() & ~Modifier.FINAL); field_capes.set(info,
		 * blueTex); } catch (Exception exception) {
		 * exception.printStackTrace(); } } catch (Exception exception) {
		 * exception.printStackTrace(); }
		 * 
		 * } }
		 */
	}

	private boolean hasRedCape(UUID uniqueID) {
		for (UUID uuid1 : redcapes) {
			if (uniqueID.equals(uuid1)) {
				return true;
			}
		}
		return false;
	}

	private boolean hasBlueCape(UUID uniqueID) {
		for (UUID uuid1 : bluecapes) {
			if (uniqueID.equals(uuid1)) {
				return true;
			}
		}
		return false;
	}

	private boolean hasBetaCape(UUID uniqueID) {
		for (UUID uuid1 : betatesters) {
			if (uniqueID.equals(uuid1)) {
				return true;
			}
		}
		return false;
	}
}
