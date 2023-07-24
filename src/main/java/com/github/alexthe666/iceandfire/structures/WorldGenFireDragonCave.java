package com.github.alexthe666.iceandfire.structures;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.block.BlockGoldPile;
import com.github.alexthe666.iceandfire.core.ModBlocks;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.github.alexthe666.iceandfire.entity.EntityFireDragon;
import net.minecraft.block.BlockChest;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.storage.loot.LootTableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldGenFireDragonCave extends WorldGenDragonCave {
	public static final ResourceLocation FIREDRAGON_CHEST = LootTableList.register(new ResourceLocation("iceandfire", "fire_dragon_female_cave"));
	public static final ResourceLocation FIREDRAGON_MALE_CHEST = LootTableList.register(new ResourceLocation("iceandfire", "fire_dragon_male_cave"));

	protected IBlockState getStone() {
		return ModBlocks.charedStone.getDefaultState();
	}

	protected IBlockState getCobblestone() {
		return ModBlocks.charedCobblestone.getDefaultState();
	}

	protected IBlockState getPile() {
		return ModBlocks.goldPile.getDefaultState();
	}

	protected IBlockState getGemstone() {
		return Blocks.EMERALD_ORE.getDefaultState();
	}

	protected ResourceLocation getLootTable() {
		if (isMale) {
			return FIREDRAGON_MALE_CHEST;
		}
		else return FIREDRAGON_CHEST;
	}

	protected EntityDragonBase createDragon(World worldIn) {
		return new EntityFireDragon(worldIn);
	}
}
