package com.github.alexthe666.iceandfire.entity.ai;

import com.github.alexthe666.iceandfire.api.IEntityEffectCapability;
import com.github.alexthe666.iceandfire.api.InFCapabilities;
import com.github.alexthe666.iceandfire.entity.util.DragonUtils;
import com.github.alexthe666.iceandfire.entity.EntityIceDragon;
import net.minecraft.block.material.Material;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class DragonAIWaterTarget extends EntityAIBase {
	private EntityIceDragon dragon;

	public DragonAIWaterTarget(EntityIceDragon dragon) {
		this.dragon = dragon;
		this.setMutexBits(1);
	}

	public boolean shouldExecute() {
		if (dragon != null) {
			if (dragon.isSleeping()) {
				return false;
			}
			if (!dragon.isInWater()) {
				return false;
			}
			if (dragon.getOwner() != null && dragon.getPassengers().contains(dragon.getOwner())) {
				return false;
			}
			if (dragon.waterTarget != null && (dragon.isTargetBlocked(new Vec3d(dragon.waterTarget)))) {
				dragon.waterTarget = null;
			}

			if (dragon.waterTarget != null) {
				return false;
			} else {
				Vec3d vec = this.findWaterTarget();

				if (vec == null) {
					return false;
				} else {
					dragon.waterTarget = new BlockPos(vec.x, vec.y, vec.z);
					return true;
				}
			}
		}
		return false;
	}

	public boolean continueExecuting() {
		IEntityEffectCapability capability = InFCapabilities.getEntityEffectCapability(dragon);
		if (!dragon.isInWater()) {
			return false;
		}
		if (dragon.isSleeping()) {
			return false;
		}
		if (capability != null && capability.isStoned()) {
			return false;
		}
		return dragon.airTarget != null;
	}

	public Vec3d findWaterTarget() {
		return new Vec3d(getNearbyWaterTarget());
	}

	public BlockPos getNearbyWaterTarget() {
		if (dragon.getAttackTarget() == null) {
			BlockPos pos = DragonUtils.getWaterBlockInView(dragon);
			if (pos != null && dragon.world.getBlockState(pos).getMaterial() == Material.WATER) {
				return pos;
			}
		} else {
			return new BlockPos((int) dragon.getAttackTarget().posX, (int) dragon.getAttackTarget().posY, (int) dragon.getAttackTarget().posZ);
		}
		return dragon.getPosition();
	}

}