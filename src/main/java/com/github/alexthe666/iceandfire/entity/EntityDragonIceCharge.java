package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.ilexiconn.llibrary.server.entity.EntityPropertiesHandler;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EntityDragonIceCharge extends EntityFireball implements IDragonProjectile {

	public int ticksInAir;

	public EntityDragonIceCharge(World worldIn) {
		super(worldIn);

	}

	public EntityDragonIceCharge(World worldIn, double posX, double posY, double posZ, double accelX, double accelY, double accelZ) {
		super(worldIn, posX, posY, posZ, accelX, accelY, accelZ);
		double d0 = MathHelper.sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ);
		this.accelerationX = accelX / d0 * 0.07D;
		this.accelerationY = accelY / d0 * 0.07D;
		this.accelerationZ = accelZ / d0 * 0.07D;
	}

	public EntityDragonIceCharge(World worldIn, EntityDragonBase shooter, double accelX, double accelY, double accelZ) {
		super(worldIn, shooter, accelX, accelY, accelZ);
		double d0 = MathHelper.sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ);
		this.accelerationX = accelX / d0 * 0.07D;
		this.accelerationY = accelY / d0 * 0.07D;
		this.accelerationZ = accelZ / d0 * 0.07D;
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

	public void onUpdate() {
		for (int i = 0; i < 10; ++i) {
			IceAndFire.PROXY.spawnParticle("snowflake", world, this.posX + this.rand.nextDouble() * 1 * (this.rand.nextBoolean() ? -1 : 1), this.posY + this.rand.nextDouble() * 1 * (this.rand.nextBoolean() ? -1 : 1), this.posZ + this.rand.nextDouble() * 1 * (this.rand.nextBoolean() ? -1 : 1), 0.0D, 0.0D, 0.0D);
		}
		if (this.world.isRemote || (this.shootingEntity == null || !this.shootingEntity.isDead) && this.world.isBlockLoaded(new BlockPos(this))) {
			super.onUpdate();

			if (this.isFireballFiery()) {
				this.setFire(1);
			}

			++this.ticksInAir;
			RayTraceResult raytraceresult = ProjectileHelper.forwardsRaycast(this, false, this.ticksInAir >= 25, this.shootingEntity);

			if (raytraceresult != null) {
				this.onImpact(raytraceresult);
			}

			this.posX += this.motionX;
			this.posY += this.motionY;
			this.posZ += this.motionZ;
			ProjectileHelper.rotateTowardsMovement(this, 0.2F);
			float f = this.getMotionFactor();

			if (this.isInWater()) {
				for (int i = 0; i < 4; ++i) {
					float f1 = 0.25F;
					this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * 0.25D, this.posY - this.motionY * 0.25D, this.posZ - this.motionZ * 0.25D, this.motionX, this.motionY, this.motionZ, new int[0]);
				}

				f = 0.8F;
			}

			this.motionX += this.accelerationX;
			this.motionY += this.accelerationY;
			this.motionZ += this.accelerationZ;
			this.motionX *= f;
			this.motionY *= f;
			this.motionZ *= f;
			this.world.spawnParticle(this.getParticleType(), this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D, new int[0]);
			this.setPosition(this.posX, this.posY, this.posZ);
		} else {
			this.setDead();
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
			if (movingObject.entityHit == null || !(movingObject.entityHit instanceof IDragonProjectile) && movingObject.entityHit != shootingEntity) {
				if (this.shootingEntity != null && IceAndFire.CONFIG.dragonGriefing != 2) {
					int explodeSize = 2;
					if(this.shootingEntity instanceof EntityDragonBase){
						explodeSize = 2 + ((EntityDragonBase) this.shootingEntity).getDragonStage();
					}
					IceExplosion explosion = new IceExplosion(world, shootingEntity, this.posX, this.posY, this.posZ, explodeSize, flag);
					explosion.doExplosionA();
					explosion.doExplosionB(true);
					FireChargeExplosion explosion2 = new FireChargeExplosion(world, shootingEntity, this.posX, this.posY, this.posZ, 2 + ((EntityDragonBase) this.shootingEntity).getDragonStage(), flag);
					explosion2.doExplosionA();
					explosion2.doExplosionB(true);
				}
				this.setDead();
				return;
			}
			if (!(movingObject.entityHit instanceof IDragonProjectile) && !movingObject.entityHit.isEntityEqual(shootingEntity)) {
				if (this.shootingEntity != null && this.shootingEntity instanceof EntityDragonBase && !movingObject.entityHit.isEntityEqual(shootingEntity)) {
					movingObject.entityHit.attackEntityFrom(IceAndFire.dragonIce, 10.0F);
					if (movingObject.entityHit instanceof EntityLivingBase) {
						EntityEffectProperties effectProperties = EntityPropertiesHandler.INSTANCE.getProperties(movingObject.entityHit, EntityEffectProperties.class);
						if (effectProperties != null) {
							effectProperties.setFrozenFor(200);
						}
					}
					if (movingObject.entityHit instanceof EntityLivingBase && ((EntityLivingBase) movingObject.entityHit).getHealth() == 0) {
						((EntityDragonBase) this.shootingEntity).attackDecision = true;
					}
				}
				this.applyEnchantments(this.shootingEntity, movingObject.entityHit);
				IceExplosion explosion = new IceExplosion(world, null, this.posX, this.posY, this.posZ, 2, flag);
				if (shootingEntity != null) {
					explosion = new IceExplosion(world, shootingEntity, this.posX, this.posY, this.posZ, 2, flag);
				}
				explosion.doExplosionA();
				explosion.doExplosionB(true);
				this.world.createExplosion(this, this.posX, this.posY, this.posZ, 4, true);
			}
		}
		this.setDead();
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float amount) {
		return false;
	}

	public float getCollisionBorderSize() {
		return 0F;
	}
}
