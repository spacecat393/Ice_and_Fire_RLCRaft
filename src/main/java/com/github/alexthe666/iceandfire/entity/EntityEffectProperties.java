package com.github.alexthe666.iceandfire.entity;

import net.ilexiconn.llibrary.server.entity.EntityProperties;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntityEffectProperties extends EntityProperties<EntityLivingBase> {

	private enum EntityEffect {
		NONE, STONE, FROZEN, PARALYZED, CHARMED;
		public static final EntityEffect[] values = values();
	}

	public EntityEffect activeEffect = EntityEffect.NONE;
	public int effectData;
	public int additionalData;

	@Override
	public int getTrackingTime() {
		return 20;
	}

	@Override
	public void saveNBTData(NBTTagCompound compound) {
		compound.setInteger("activeEffect", activeEffect.ordinal());
		compound.setInteger("effectData", effectData);
		compound.setInteger("additionalData", additionalData);
	}

	@Override
	public void loadNBTData(NBTTagCompound compound) {
		this.activeEffect = EntityEffect.values[compound.getInteger("activeEffect")];
		this.effectData = compound.getInteger("effectData");
		this.additionalData = compound.getInteger("additionalData");
	}

	@Override
	public void init() {
		activeEffect = EntityEffect.NONE;
		effectData = 0;
		additionalData = 0;
	}

	@Override
	public String getID() {
		return "Ice And Fire - Entity Effect Property Tracker";
	}

	@Override
	public Class<EntityLivingBase> getEntityClass() {
		return EntityLivingBase.class;
	}

	public void turnToStone() {
		this.activeEffect = EntityEffect.STONE;
		this.effectData = 0;
		this.additionalData = 0;
	}

	public void setFrozenFor(int frozenFor) {
		if (isStone()) {
			return;
		}
		if(!(this.getEntity() instanceof EntityIceDragon)) {
			this.activeEffect = EntityEffect.FROZEN;
			this.effectData = frozenFor;
			this.additionalData = 0;
		}
	}

	public void setParalyzedFor(int paralyzedFor) {
		if (isStone() || isFrozen()) {
			return;
		}
		if(!(this.getEntity() instanceof EntityLightningDragon)) {
			this.activeEffect = EntityEffect.PARALYZED;
			this.effectData = paralyzedFor;
			this.additionalData = 0;
		}
	}

	public void setCharmed(int entityID) {
		if (isStone() || isFrozen() || isParalyzed()) {
			return;
		}
		this.activeEffect = EntityEffect.CHARMED;
		this.effectData = entityID;
		this.additionalData = 0;
	}

	public void reset() {
		this.activeEffect = EntityEffect.NONE;
		this.effectData = 0;
		this.additionalData = 0;
	}

	public boolean isStone() {
		return activeEffect == EntityEffect.STONE;
	}

	public boolean isFrozen() {
		return activeEffect == EntityEffect.FROZEN;
	}

	public boolean isParalyzed() {
		return activeEffect == EntityEffect.PARALYZED;
	}

	public boolean isCharmed() {
		return activeEffect == EntityEffect.CHARMED;
	}

	public EntitySiren getSiren(World world) {
		if (!isCharmed()) {
			return null;
		}
		Entity entity = world.getEntityByID(effectData);
		if (entity instanceof EntitySiren) {
			return (EntitySiren) entity;
		}
		return null;
	}
}
