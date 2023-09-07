package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockGeneric extends Block {

	private final boolean beacon;
	private final boolean transluscent;

	public BlockGeneric(Material materialIn, String gameName, String name, String toolUsed, int toolStrength, float hardness, float resistance, SoundType sound) {
		this(materialIn, gameName, name, toolUsed, toolStrength, hardness, resistance, sound, false);
	}

	public BlockGeneric(Material materialIn, String gameName, String name, String toolUsed, int toolStrength, float hardness, float resistance, SoundType sound, boolean beacon) {
		this(materialIn, gameName, name, toolUsed, toolStrength, hardness, resistance, sound, beacon, false);
	}

	public BlockGeneric(Material materialIn, String gameName, String name, String toolUsed, int toolStrength, float hardness, float resistance, SoundType sound, boolean beacon, boolean slippery) {
		this(materialIn, gameName, name, toolUsed, toolStrength, hardness, resistance, sound, beacon, slippery, false);
	}

	@SuppressWarnings("deprecation")
	public BlockGeneric(Material materialIn, String gameName, String name, String toolUsed, int toolStrength, float hardness, float resistance, SoundType sound, boolean beacon, boolean slippery, boolean transluscent) {
		super(materialIn);
		this.setTranslationKey(name);
		this.setHarvestLevel(toolUsed, toolStrength);
		this.setHardness(hardness);
		this.setResistance(resistance);
		this.setSoundType(sound);
		this.setCreativeTab(IceAndFire.TAB);
		setRegistryName(IceAndFire.MODID, gameName);
		this.beacon = beacon;
		if (slippery) {
			this.slipperiness = 0.98F;
		}
		this.transluscent = transluscent;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return this.transluscent ? BlockRenderLayer.TRANSLUCENT : super.getRenderLayer();
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isOpaqueCube(IBlockState state) {
		return !this.transluscent;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isFullCube(IBlockState state) {
		return !this.transluscent;
	}

	@Override
	@SuppressWarnings("deprecation")
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
		IBlockState iblockstate = blockAccess.getBlockState(pos.offset(side));
		return this.transluscent ? blockState != iblockstate : super.shouldSideBeRendered(blockState, blockAccess, pos, side);
	}

	@Override
	public boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beacon){
		return this.beacon;
	}
}