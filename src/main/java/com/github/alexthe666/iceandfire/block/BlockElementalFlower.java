package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.core.ModBlocks;
import net.minecraft.block.BlockBush;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import thaumcraft.api.crafting.IInfusionStabiliser;

@Optional.Interface(iface = "thaumcraft.api.crafting.IInfusionStabiliser", modid = "thaumcraft")
public class BlockElementalFlower extends BlockBush implements IInfusionStabiliser {
	public Item itemBlock;

	public BlockElementalFlower(int type) {
		this.setTickRandomly(true);
		this.setCreativeTab(IceAndFire.TAB);
		this.setTranslationKey(type == 0 ? "iceandfire.fire_lily" : type == 1 ? "iceandfire.frost_lily": "iceandfire.lightning_lily");
		setRegistryName(IceAndFire.MODID, type == 0 ? "fire_lily" : type == 1? "frost_lily" : "lightning_lily");
		this.setSoundType(SoundType.PLANT);
	}

	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		IBlockState soil = worldIn.getBlockState(pos.down());
		if (this == ModBlocks.fire_lily) {
			return worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos) && (soil.getMaterial() == Material.SAND || soil.getBlock() == Blocks.NETHERRACK);
		} else if (this == ModBlocks.frost_lily) {
			return worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos) && (soil.getMaterial() == Material.PACKED_ICE || soil.getMaterial() == Material.ICE);
		} else {
			return worldIn.getBlockState(pos).getBlock().isReplaceable(worldIn, pos) && (soil.getMaterial() == Material.GROUND || soil.getMaterial() == Material.GRASS);
		}
	}

	protected boolean canSustainBush(IBlockState state) {
		return true;
	}

	@Override
	@Optional.Method(modid = "thaumcraft")
	public boolean canStabaliseInfusion(World world, BlockPos pos) {
		return true;
	}
}
