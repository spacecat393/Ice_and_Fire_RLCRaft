package com.github.alexthe666.iceandfire.entity.ai;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.Random;

public class SeaSerpentAIGetInWater extends AquaticAIGetInWater {

    public SeaSerpentAIGetInWater(EntityCreature theCreatureIn, double movementSpeedIn) {
        super(theCreatureIn, movementSpeedIn);
    }

    @Override
    public boolean isAttackerInWater() {
        return false;
    }

    @Nullable
    public Vec3d findPossibleShelter() {
        return findPossibleShelter(15, 12);
    }

    @Nullable
    protected Vec3d findPossibleShelter(int xz, int y) {
        Random random = this.creature.getRNG();
        BlockPos blockpos = new BlockPos(this.creature.posX, this.creature.getEntityBoundingBox().minY, this.creature.posZ);

        for (int i = 0; i < 10; ++i) {
            BlockPos blockpos1 = blockpos.add(random.nextInt(xz * 2) - xz, random.nextInt(y) + 2, random.nextInt(xz * 2) - xz);
            while(this.world.isAirBlock(blockpos1) && blockpos1.getY() > 3){
                blockpos1 = blockpos1.down();
            }
            if (this.world.getBlockState(blockpos1).getMaterial() == Material.WATER) {
                return new Vec3d((double) blockpos1.getX(), (double) blockpos1.getY(), (double) blockpos1.getZ());
            }
        }

        return null;
    }
}
