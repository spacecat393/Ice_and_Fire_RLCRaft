package com.github.alexthe666.iceandfire.api;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.IceAndFireConfig;
import com.github.alexthe666.iceandfire.compat.LycanitesCompat;
import com.github.alexthe666.iceandfire.core.ModSounds;
import com.github.alexthe666.iceandfire.entity.DragonUtils;
import com.github.alexthe666.iceandfire.entity.EntityMutlipartPart;
import com.github.alexthe666.iceandfire.message.MessageChainLightningFX;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.*;

public class ChainLightningUtils {

    public static void createChainLightningFromTarget(World world, EntityLivingBase target, EntityLivingBase attacker) {
        float damage = IceAndFireConfig.MISC_SETTINGS.chainLightningDamage;
        int hops = IceAndFireConfig.MISC_SETTINGS.chainLightningHops;

        createChainLightningFromTarget(world, target, attacker, damage, hops);
    }

    public static void createChainLightningFromTarget(World world, EntityLivingBase target, EntityLivingBase attacker, float damage, int hops) {
        int range = IceAndFireConfig.MISC_SETTINGS.chainLightningRange;

        createChainLightningFromTarget(world, target, attacker, damage, hops, range);
    }

    public static void createChainLightningFromTarget(World world, EntityLivingBase target, EntityLivingBase attacker, float damage, int hops, int range) {
        float damageReductionPerHop = damage / (float)(hops + 1);

        boolean isParalysisEnabled = IceAndFireConfig.MISC_SETTINGS.chainLightningParalysis;
        int paralysisTicks = IceAndFireConfig.MISC_SETTINGS.chainLightningParalysisTicks;
        int paralysisChance = IceAndFireConfig.MISC_SETTINGS.chainLightningParalysisChance;

        attackEntityWithLightningDamage(attacker, target, damage);
        if (isParalysisEnabled) {
            applyParalysis(target, paralysisTicks, paralysisChance);
        }

        target.playSound(ModSounds.LIGHTNING_STRIKE, 1, 1);

        LightningSource lightningSource = new LightningSource(target);

        List<EntityLivingBase> entityLiving = new ArrayList<>();
        for(Entity ent : world.getEntitiesWithinAABBExcludingEntity(lightningSource.get(), lightningSource.getBoundingBox(range))) {
            if(ent instanceof EntityMutlipartPart) ent = ((EntityMutlipartPart)ent).getParent();
            if(ent instanceof EntityLivingBase &&
                    ent.isEntityAlive() &&
                    !entityLiving.contains((EntityLivingBase)ent) &&
                    lightningSource.canChainTo(ent, attacker))
                entityLiving.add((EntityLivingBase)ent);
        }
        if(entityLiving.isEmpty()) return;

        entityLiving.sort(getFindByNearestComparator(lightningSource));

        LinkedList<Integer> alreadyTargetedEntities = new LinkedList<>();
        for(EntityLivingBase nextTarget : entityLiving) {
            if(hops <= 0 || damage <= 0) break;
            if(alreadyTargetedEntities.contains(nextTarget.getEntityId())) continue;

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
        if (IceAndFireConfig.MISC_SETTINGS.chainLightningTransformsMobs) {
            if (target instanceof EntityPig || target instanceof EntityVillager) {
                EntityLightningBolt lightningBolt = new EntityLightningBolt(target.world, target.posX, target.posY, target.posZ, true);
                target.onStruckByLightning(lightningBolt);
            } else {
                target.attackEntityFrom(new EntityDamageSourceIndirect("lightningBolt", attacker, attacker), damage);

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
            target.attackEntityFrom(new EntityDamageSourceIndirect("lightningBolt", attacker, attacker), damage);
        }
    }

    //TODO: Transition to EntityEffectEnum.SHOCKED?
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
