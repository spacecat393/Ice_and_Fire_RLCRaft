package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.particle.lightning.ParticleLightningVector;
import com.github.alexthe666.iceandfire.compat.LycanitesCompat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityDragonLightning extends EntityFireball implements IDragonProjectile {

	private Vec3d lastPos;

	public EntityDragonLightning(World worldIn) {
		super(worldIn);
	}

	public EntityDragonLightning(World worldIn, double posX, double posY, double posZ, double accelX, double accelY, double accelZ) {
		super(worldIn, posX, posY, posZ, accelX, accelY, accelZ);

		if (posX != 0 || posY != 0 || posZ != 0) {
			lastPos = new Vec3d(posX, posY, posZ);
		}
	}

	public EntityDragonLightning(World worldIn, EntityDragonBase shooter, double accelX, double accelY, double accelZ) {
		super(worldIn, shooter, accelX, accelY, accelZ);
		this.setSize(0.5F, 0.5F);
		double d0 = MathHelper.sqrt(accelX * accelX + accelY * accelY + accelZ * accelZ);
		this.accelerationX = accelX / d0 * (0.1D * (shooter.isFlying() ? 4 * shooter.getDragonStage() : 1));
		this.accelerationY = accelY / d0 * (0.1D * (shooter.isFlying() ? 4 * shooter.getDragonStage() : 1));
		this.accelerationZ = accelZ / d0 * (0.1D * (shooter.isFlying() ? 4 * shooter.getDragonStage() : 1));

		lastPos = shooter.getHeadPosition();
	}

	public void setSizes(float width, float height) {
		this.setSize(width, height);
	}

	@Override
	public void setPosition(double x, double y, double z) {
		if (lastPos == null && (x != 0 || y != 0 || z != 0)) {
			lastPos = new Vec3d(posX, posY, posZ);
		}
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
		Vec3d currentPos = new Vec3d(posX, posY, posZ);
		emitLightningFx(currentPos);
		lastPos = currentPos;
		if (ticksExisted > 160) {
			setDead();
		}
		if (this.isInWater()) {
			setDead();
		}
	}

	@Override
	protected void onImpact(RayTraceResult movingObject) {
		boolean flag = this.world.getGameRules().getBoolean("mobGriefing");

		emitLightningFx(movingObject.hitVec);

		if (!this.world.isRemote) {
			if (movingObject.entityHit instanceof IDragonProjectile) {
				return;
			}
			if (DragonUtils.isOwner(movingObject.entityHit, shootingEntity) || DragonUtils.hasSameOwner(movingObject.entityHit, shootingEntity)) {
				this.setDead();
				return;
			}
			if (movingObject.entityHit == null || !(movingObject.entityHit instanceof IDragonProjectile) && this.shootingEntity != null && this.shootingEntity instanceof EntityDragonBase && movingObject.entityHit != shootingEntity) {
				if (this.shootingEntity != null && this.shootingEntity instanceof EntityDragonBase && IceAndFire.CONFIG.dragonGriefing != 2) {
					LightningExplosion explosion = new LightningExplosion(world, shootingEntity, this.posX, this.posY, this.posZ, ((EntityDragonBase) this.shootingEntity).getDragonStage() * 2.5F, flag);
					explosion.doExplosionA();
					explosion.doExplosionB(false);
				}
				this.setDead();
				return;
			}
			if (!(movingObject.entityHit instanceof IDragonProjectile) && !movingObject.entityHit.isEntityEqual(shootingEntity)) {
				if (this.shootingEntity != null && this.shootingEntity instanceof EntityDragonBase) {
					if (movingObject.entityHit instanceof EntityLivingBase && ((EntityLivingBase) movingObject.entityHit).getHealth() == 0) {
						((EntityDragonBase) this.shootingEntity).attackDecision = true;
					}
				}
				this.applyEnchantments(this.shootingEntity, movingObject.entityHit);
				movingObject.entityHit.attackEntityFrom(IceAndFire.dragonLightning, 3);
				if(movingObject.entityHit instanceof EntityLivingBase){
					if (IceAndFire.CONFIG.lightningDragonKnockback) {
						double xRatio = this.shootingEntity.posX - movingObject.entityHit.posX;
						double zRatio = this.shootingEntity.posZ - movingObject.entityHit.posZ;
						((EntityLivingBase) movingObject.entityHit).knockBack(this.shootingEntity, 0.3F, xRatio, zRatio);
					}
					if (IceAndFire.CONFIG.lightningDragonParalysis) {
						LycanitesCompat.applyParalysis(movingObject.entityHit, IceAndFire.CONFIG.lightningDragonParalysisTicks);
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

	private void emitLightningFx(Vec3d pos) {
		if (lastPos != null && !pos.equals(lastPos)) {
			ParticleLightningVector source = new ParticleLightningVector(lastPos.x, lastPos.y, lastPos.z);
			ParticleLightningVector target = new ParticleLightningVector(pos.x, pos.y, pos.z);
			IceAndFire.PROXY.spawnLightningEffect(world, source, target, true);
		}
	}
}
