package com.github.alexthe666.iceandfire.enums;

import com.google.common.collect.Maps;
import net.minecraft.util.text.TextFormatting;

import java.util.Map;

public enum EnumDragonEgg {
    RED(0, TextFormatting.DARK_RED, EnumDragonType.FIRE), GREEN(1, TextFormatting.DARK_GREEN, EnumDragonType.FIRE), BRONZE(2, TextFormatting.GOLD, EnumDragonType.FIRE), GRAY(3, TextFormatting.GRAY, EnumDragonType.FIRE),
    BLUE(4, TextFormatting.AQUA, EnumDragonType.ICE), WHITE(5, TextFormatting.WHITE, EnumDragonType.ICE), SAPPHIRE(6, TextFormatting.BLUE, EnumDragonType.ICE), SILVER(7, TextFormatting.DARK_GRAY, EnumDragonType.ICE),
    ELECTRIC(8, TextFormatting.DARK_BLUE, EnumDragonType.LIGHTNING), AMETHYST(9, TextFormatting.LIGHT_PURPLE, EnumDragonType.LIGHTNING), COPPER(10, TextFormatting.GOLD, EnumDragonType.LIGHTNING),
    BLACK(11, TextFormatting.DARK_GRAY, EnumDragonType.LIGHTNING);

    private static final Map<Integer, EnumDragonEgg> META_LOOKUP = Maps.newHashMap();

    static {
        EnumDragonEgg[] var0 = values();
        int var1 = var0.length;

        for (EnumDragonEgg var3 : var0) {
            META_LOOKUP.put(var3.meta, var3);
        }
    }

    public int meta;
    public TextFormatting color;
    public EnumDragonType dragonType;

    EnumDragonEgg(int meta, TextFormatting color, EnumDragonType dragonType) {
        this.meta = meta;
        this.color = color;
        this.dragonType = dragonType;
    }

    public static EnumDragonEgg byMetadata(int meta) {
        EnumDragonEgg i = META_LOOKUP.get(meta);
        return i == null ? RED : i;
    }
}
