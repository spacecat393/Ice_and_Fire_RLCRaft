package com.github.alexthe666.iceandfire.api;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.compat.LycanitesCompat;
import com.github.alexthe666.iceandfire.core.ModSounds;
import com.github.alexthe666.iceandfire.entity.DragonUtils;
import com.github.alexthe666.iceandfire.message.MessageChainLightningFX;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.*;

public class ChainLightningUtils {

    public static void createChainLightningFromTarget(World world, EntityLivingBase target, EntityLivingBase attacker) {
        float damage = IceAndFire.CONFIG.chainLightningDamage;
        int hops = IceAndFire.CONFIG.chainLightningHops;

        createChainLightningFromTarget(world, target, attacker, damage, hops);
    }

    public static void createChainLightningFromTarget(World world, EntityLivingBase target, EntityLivingBase attacker, float damage, int hops) {
        int range = IceAndFire.CONFIG.chainLightningRange;

        createChainLightningFromTarget(world, target, attacker, damage, hops, range);
    }

    public static void createChainLightningFromTarget(World world, EntityLivingBase target, EntityLivingBase attacker, float damage, int hops, int range) {
        float damageReductionPerHop = damage / (hops + 1);

        boolean isParalysisEnabled = IceAndFire.CONFIG.chainLightningParalysis;
        int paralysisTicks = IceAndFire.CONFIG.chainLightningParalysisTicks;
        int paralysisChance = IceAndFire.CONFIG.chainLightningParalysisChance;

        attackEntityWithLightningDamage(attacker, target, damage);
        if (isParalysisEnabled) {
            applyParalysis(target, paralysisTicks, paralysisChance);
        } else {
            IceAndFire.logger.warn("The Paralysis Effect is currently disabled");
        }

        target.playSound(ModSounds.LIGHTNING_STRIKE, 1, 1);

        LightningSource lightningSource = new LightningSource(target);

        List<Entity> entities = world.getEntitiesWithinAABBExcludingEntity(lightningSource.get(), lightningSource.getBoundingBox(range));
        if (entities.isEmpty()) {
            return;
        }

        entities.sort(getFindByNearestComparator(lightningSource));

        LinkedList<Integer> alreadyTargetedEntities = new LinkedList<>();

        while (hops > 0 && damage > 0) {
            Optional<Entity> optional = entities.stream()
                    .filter(e -> lightningSource.canChainTo(e, attacker) && !alreadyTargetedEntities.contains(e.getEntityId()))
                    .findFirst();

            if (!optional.isPresent()) {
                break;
            }

            EntityLivingBase nextTarget = (EntityLivingBase) optional.get();

            attackEntityWithLightningDamage(attacker, nextTarget, damage);
            if (isParalysisEnabled) {
                applyParalysis(nextTarget, paralysisTicks, paralysisChance);
            }

            alreadyTargetedEntities.add(nextTarget.getEntityId());
            lightningSource.set(nextTarget);

            hops--;
            damage -= damageReductionPerHop;
        }

        if (!alreadyTargetedEntities.isEmpty()) {
            alreadyTargetedEntities.addFirst(target.getEntityId());

            IceAndFire.NETWORK_WRAPPER.sendToAllAround(
                    new MessageChainLightningFX(alreadyTargetedEntities),
                    new NetworkRegistry.TargetPoint(
                            target.dimension,
                            target.posX,
                            target.posY+ target.height / 2,
                            target.posZ,
                            60
                    )
            );
        }
    }

    private static void attackEntityWithLightningDamage(EntityLivingBase attacker, EntityLivingBase target, float damage) {
        if (IceAndFire.CONFIG.chainLightningTransformsMobs) {
            if (target instanceof EntityPig || target instanceof EntityVillager) {
                EntityLightningBolt lightningBolt = new EntityLightningBolt(target.world, target.posX, target.posY, target.posZ, true);
                target.onStruckByLightning(lightningBolt);
            } else {
                target.attackEntityFrom(new EntityDamageSource("LightningBolt", attacker), damage);

                if (target instanceof EntityCreeper) {
                    EntityCreeper creeper = (EntityCreeper) target;
                    if (!creeper.getPowered()) {
                        NBTTagCompound compound = new NBTTagCompound();
                        creeper.writeEntityToNBT(compound);
                        compound.setBoolean("powered", true);
                        creeper.readEntityFromNBT(compound);
                    }
                }
            }
        } else {
            target.attackEntityFrom(new EntityDamageSource("LightningBolt", attacker), damage);
        }
    }

    private static void applyParalysis(EntityLivingBase target, int paralysisTicks, int paralysisChance) {
        if (paralysisChance <= 1 || target.world.rand.nextInt(paralysisChance) == 0) {
            LycanitesCompat.applyParalysis(target, paralysisTicks);
        }
    }

    private static Comparator<Entity> getFindByNearestComparator(LightningSource lightningSource) {
        return Comparator.comparingDouble(e -> e.getDistanceSq(lightningSource.get()));
    }

    private static class LightningSource {

        EntityLivingBase source;
        public LightningSource(EntityLivingBase source) {
            this.source = source;
        }

        public void set(EntityLivingBase source) {
            this.source = source;
        }

        public EntityLivingBase get() {
            return source;
        }

        private boolean canChainTo(Entity target, EntityLivingBase attacker) {
            if (!(target instanceof EntityLivingBase)) {
                return false;
            }
            if (target instanceof EntityPlayer) {
                return false;
            }
            if (!DragonUtils.isAlive((EntityLivingBase) target)) {
                return false;
            }
            if (target instanceof IEntityOwnable && ((IEntityOwnable) target).getOwner() != null) {
                if (target instanceof EntityLiving) {
                    EntityLivingBase attackTarget = ((EntityLiving) target).getAttackTarget();
                    EntityLivingBase revengeTarget = ((EntityLivingBase) target).getRevengeTarget();
                    if (!attacker.equals(attackTarget) && !attacker.equals(revengeTarget)) {
                        return false;
                    }
                } else {
                    EntityLivingBase revengeTarget = ((EntityLivingBase) target).getRevengeTarget();
                    if (!attacker.equals(revengeTarget)) {
                        return false;
                    }
                }
            }
            return source.canEntityBeSeen(target);
        }

        private AxisAlignedBB getBoundingBox(int range) {
            return new AxisAlignedBB(
                    source.posX - range,
                    source.posY - range,
                    source.posZ - range,
                    source.posX + range,
                    source.posY + range,
                    source.posZ + range
            );
        }
    }
}
