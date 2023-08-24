package com.github.alexthe666.iceandfire.compat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.Loader;

public class LycanitesCompat {

    private static final String MOD_ID = "lycanitesmobs";
    private static final String PARALYSIS = "paralysis";

    public static boolean isEnabled() {
        return Loader.isModLoaded(MOD_ID);
    }

    public static void applyParalysis(Entity entity, int duration) {
        if (!(entity instanceof EntityLivingBase)) {
            return;
        }
        if (isEnabled()) {
            try {
                EntityLivingBase livingBase = (EntityLivingBase) entity;
                String resourceLocation = MOD_ID + ":" + PARALYSIS;
                Potion effect = getPotionEffect(resourceLocation);
                if (effect == null) {
                    return;
                }
                if (!livingBase.isPotionActive(effect)) {
                    livingBase.addPotionEffect(new PotionEffect(effect, duration));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isParalysisEffect(PotionEffect effect) {
        if (effect == null) {
            return false;
        }
        if (isEnabled()) {
            String resourceLocation = MOD_ID + ":" + PARALYSIS;
            Potion paralysis = getPotionEffect(resourceLocation);
            if (paralysis == null) {
                return false;
            }
            return effect.getPotion().getRegistryName().toString().contentEquals(paralysis.getRegistryName().toString());
        }
        return false;
    }

    private static Potion getPotionEffect(String resourceLocation) {
        try {
            return Potion.getPotionFromResourceLocation(resourceLocation);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

