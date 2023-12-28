package com.github.alexthe666.iceandfire.integration;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

import javax.annotation.Nullable;

public class SpartanWeaponryCompat {
    public static ResourceLocation RETURN_ENCHANTMENT = new ResourceLocation("spartanweaponry","return");

    public static boolean hasReturnEnchantment() {
        return getReturnEnchantment() != null;
    }

    @Nullable
    public static Enchantment getReturnEnchantment() {
        return Enchantment.REGISTRY.getObject(RETURN_ENCHANTMENT);
    }

    @Nullable
    public static SoundEvent getReturnSoundEvent() {
        return SoundEvent.REGISTRY.getObject(new ResourceLocation("spartanweaponry", "throwing_weapon_return"));
    }

    @Nullable
    public static SoundEvent getThrowingWeaponSoundEvent() {
        return SoundEvent.REGISTRY.getObject(new ResourceLocation("spartanweaponry", "throwing_weapon_throw"));
    }
}
