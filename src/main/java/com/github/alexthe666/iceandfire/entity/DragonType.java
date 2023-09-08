package com.github.alexthe666.iceandfire.entity;

import com.github.alexthe666.iceandfire.IceAndFireConfig;
import com.github.alexthe666.iceandfire.block.BlockEggInIce;
import com.github.alexthe666.iceandfire.core.ModBlocks;
import com.github.alexthe666.iceandfire.core.ModSounds;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityEggInIce;
import net.minecraft.block.material.Material;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.effect.EntityLightningBolt;

import java.util.Objects;

public class DragonType {

    public static final DragonType FIRE = new DragonType("fire");
    public static final DragonType ICE = new DragonType("ice").setPiscivore();
    public static final DragonType LIGHTNING = new DragonType("lightning");


    private String name;
    private boolean piscivore;

    public DragonType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPiscivore() {
        return piscivore;
    }

    public DragonType setPiscivore(){
        piscivore = true;
        return this;
    }

    public void updateEggCondition(EntityDragonEgg egg) {
        BlockPos pos = new BlockPos(egg);
        if (!meetsEggCondition(egg, pos)) {
            return;
        }
        if (this == FIRE) {
            egg.setDragonAge(egg.getDragonAge() + 1);

            if (egg.getDragonAge() > IceAndFireConfig.DRAGON_SETTINGS.dragonEggTime) {
                egg.world.setBlockToAir(pos);
                EntityFireDragon dragon = new EntityFireDragon(egg.world);
                if(egg.hasCustomName()){
                    dragon.setCustomNameTag(egg.getCustomNameTag());
                }
                dragon.setVariant(egg.getType().meta % 4);
                dragon.setGender(egg.getRNG().nextBoolean());
                dragon.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                dragon.setHunger(50);
                if (!egg.world.isRemote) {
                    egg.world.spawnEntity(dragon);
                }
                dragon.setTamed(true);
                dragon.setOwnerId(egg.getOwnerId());
                egg.world.playSound(egg.posX, egg.posY + egg.getEyeHeight(), egg.posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH, egg.getSoundCategory(), 2.5F, 1.0F, false);
                egg.world.playSound(egg.posX, egg.posY + egg.getEyeHeight(), egg.posZ, ModSounds.DRAGON_HATCH, egg.getSoundCategory(), 2.5F, 1.0F, false);
                egg.setDead();
            }
        } else if (this == ICE) {
            egg.setDead();
            egg.world.setBlockState(pos, ModBlocks.eggInIce.getDefaultState());
            egg.world.playSound(egg.posX, egg.posY + egg.getEyeHeight(), egg.posZ, SoundEvents.BLOCK_GLASS_BREAK, egg.getSoundCategory(), 2.5F, 1.0F, false);
            if (egg.world.getBlockState(pos).getBlock() instanceof BlockEggInIce) {
                ((TileEntityEggInIce) Objects.requireNonNull(egg.world.getTileEntity(pos))).type = egg.getType();
                ((TileEntityEggInIce) Objects.requireNonNull(egg.world.getTileEntity(pos))).ownerUUID = egg.getOwnerId();
            }
        } else if (this == LIGHTNING) {
            egg.setDragonAge(egg.getDragonAge() + 1);

            if (egg.getDragonAge() > IceAndFireConfig.DRAGON_SETTINGS.dragonEggTime) {
                egg.world.setBlockToAir(pos);
                EntityLightningDragon dragon = new EntityLightningDragon(egg.world);
                if(egg.hasCustomName()){
                    dragon.setCustomNameTag(egg.getCustomNameTag());
                }
                dragon.setVariant(egg.getType().meta % 4);
                dragon.setGender(egg.getRNG().nextBoolean());
                dragon.setPosition(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                dragon.setHunger(50);
                if (!egg.world.isRemote) {
                    egg.world.spawnEntity(dragon);
                }
                dragon.setTamed(true);
                dragon.setOwnerId(egg.getOwnerId());
                if (!egg.world.isRemote) {
                    EntityLightningBolt lightningBolt = new EntityLightningBolt(egg.world, egg.posX, egg.posY, egg.posZ, true);
                    egg.world.spawnEntity(lightningBolt);
                }
                egg.world.playSound(egg.posX, egg.posY + egg.getEyeHeight(), egg.posZ, SoundEvents.ENTITY_LIGHTNING_THUNDER, egg.getSoundCategory(), 2.5F, 1.0F, false);
                egg.world.playSound(egg.posX, egg.posY + egg.getEyeHeight(), egg.posZ, ModSounds.DRAGON_HATCH, egg.getSoundCategory(), 2.5F, 1.0F, false);
                egg.setDead();
            }
        }
    }

    private boolean meetsEggCondition(EntityDragonEgg egg, BlockPos position) {
        if (this == ICE) {
            return egg.world.getBlockState(position).getMaterial() == Material.WATER && egg.getRNG().nextInt(500) == 0;
        } else if (this == LIGHTNING) {
            return egg.world.isRainingAt(position) || egg.world.isRainingAt(position.add(0, egg.height, 0));
        }
        return egg.world.getBlockState(position).getMaterial() == Material.FIRE;
    }
}
