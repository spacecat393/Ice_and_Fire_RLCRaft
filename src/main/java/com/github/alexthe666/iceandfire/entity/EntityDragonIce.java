package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.IceAndFireConfig;
import com.github.alexthe666.iceandfire.api.IEntityEffectCapability;
import com.github.alexthe666.iceandfire.api.InFCapabilities;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityDragonIce extends EntityFireball implements IDragonProjectile {

	public EntityDragonIce(World worldIn) {
		super(worldIn);
	}

	public EntityDragonIce(World worldIn, double posX, double posY, double posZ, double accelX, double accelY, double accelZ) {
		super(worldIn, posX, posY, posZ, accelX, accelY, accelZ);
	}

	public EntityDragonIce(World worldIn, EntityDragonBase shooter, double accelX, double accelY, double accelZ) {
		super(worldIn, shooter, accelX, accelY, accelZ);
		this.setSize(0.5F, 0.5F);
		double d0 = (double) MathHelper.sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ);
		this.accelerationX = accelX / d0 * (0.1D * (shooter.isFlying() ? 4 * shooter.getDragonStage() : 1));
		this.accelerationY = accelY / d0 * (0.1D * (shooter.isFlying() ? 4 * shooter.getDragonStage() : 1));
		this.accelerationZ = accelZ / d0 * (0.1D * (shooter.isFlying() ? 4 * shooter.getDragonStage() : 1));
	}

	public void setSizes(float width, float height) {
		this.setSize(width, height);
	}

	protected boolean isFireballFiery() {
		return false;
	}

	@Override
	public boolean canBeCollidedWith() {
		return false;
	}

	@Override
	public void onUpdate() {
		super.onUpdate();
		for (int i = 0; i < 6; ++i) {
			IceAndFire.PROXY.spawnParticle("dragonice", world, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
		}
		if (ticksExisted > 160) {
			setDead();
		}
	}

	@Override
	protected void onImpact(RayTraceResult movingObject) {
		boolean flag = this.world.getGameRules().getBoolean("mobGriefing");

		if (!this.world.isRemote) {
			if (movingObject.entityHit instanceof IDragonProjectile) {
				return;
			}
			if (DragonUtils.isOwner(movingObject.entityHit, shootingEntity) || DragonUtils.hasSameOwner(movingObject.entityHit, shootingEntity)) {
				this.setDead();
				return;
			}
			if (movingObject.entityHit == null || !(movingObject.entityHit instanceof IDragonProjectile) && this.shootingEntity != null && this.shootingEntity instanceof EntityDragonBase && movingObject.entityHit != shootingEntity) {
				if (this.shootingEntity != null && this.shootingEntity instanceof EntityDragonBase && IceAndFireConfig.DRAGON_SETTINGS.dragonGriefing != 2) {
					IceExplosion explosion = new IceExplosion(world, shootingEntity, this.posX, this.posY, this.posZ, ((EntityDragonBase) this.shootingEntity).getDragonStage() * 2.5F, flag);
					explosion.doExplosionA();
					explosion.doExplosionB(true);
				}
				this.setDead();
			}
			if (movingObject.entityHit != null && !(movingObject.entityHit instanceof IDragonProjectile) && !movingObject.entityHit.isEntityEqual(shootingEntity)) {
				if (this.shootingEntity != null && this.shootingEntity instanceof EntityDragonBase) {
					if (movingObject.entityHit instanceof EntityLivingBase && ((EntityLivingBase) movingObject.entityHit).getHealth() == 0) {
						((EntityDragonBase) this.shootingEntity).attackDecision = true;
					}
				}
				this.applyEnchantments(this.shootingEntity, movingObject.entityHit);
				movingObject.entityHit.attackEntityFrom(IceAndFire.dragonIce, 3);
				if (movingObject.entityHit instanceof EntityLivingBase) {
					IEntityEffectCapability capability = InFCapabilities.getEntityEffectCapability((EntityLivingBase)movingObject.entityHit);
					if (capability != null) {
						capability.setFrozen(200);
					}
				}
			}
		}
		this.setDead();
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		return false;
	}

	public float getCollisionBorderSize() {
		return 1F;
	}

}