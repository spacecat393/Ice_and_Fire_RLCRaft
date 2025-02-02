package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.core.ModBlocks;
import com.github.alexthe666.iceandfire.core.ModItems;
import com.github.alexthe666.iceandfire.core.ModSounds;
import com.github.alexthe666.iceandfire.entity.tile.TileEntityJar;
import com.github.alexthe666.iceandfire.item.ICustomRendered;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockJar extends BlockContainer implements ICustomRendered {
	private static final AxisAlignedBB AABB = new AxisAlignedBB(0.1875F, 0, 0.1875F, 0.8125F, 1F, 0.8125F);
	private final boolean empty;

	@SuppressWarnings("deprecation")
	public BlockJar(boolean empty) {
		super(Material.GLASS);
		this.setHardness(1.0F);
		this.setResistance(2.0F);
		this.setSoundType(SoundType.GLASS);
		this.setCreativeTab(IceAndFire.TAB);
		this.setTranslationKey("iceandfire.jar" + (empty ? "_empty" : "_pixie"));
		this.setRegistryName(IceAndFire.MODID, "jar" + (empty ? "_empty" : "_pixie"));
		if(!empty){
			this.setLightLevel(0.75F);
			GameRegistry.registerTileEntity(TileEntityJar.class, "jar");
		}
		this.empty = empty;
	}

	@Override
	@SuppressWarnings("deprecation")
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return AABB;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isOpaqueCube(IBlockState blockstate) {
		return false;
	}

	@Override
	@SuppressWarnings("deprecation")
	public boolean isFullCube(IBlockState blockstate) {
		return false;
	}

	@Override
	public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
		IBlockState iblockstate = worldIn.getBlockState(pos.down());
		return iblockstate.isSideSolid(worldIn, pos, EnumFacing.UP) || iblockstate.getBlock().canPlaceTorchOnTop(iblockstate, worldIn, pos);
	}

	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		dropPixie(worldIn, pos);
		super.breakBlock(worldIn, pos, state);
	}

	@Override
	@SuppressWarnings("deprecation")
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		this.checkFall(world, pos);
	}

	private void checkFall(World worldIn, BlockPos pos) {
		if(!this.canPlaceBlockAt(worldIn, pos)) {
			worldIn.destroyBlock(pos, true);
			dropPixie(worldIn, pos);
		}
	}

	public void dropPixie(World world, BlockPos pos) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityJar && ((TileEntityJar)tile).hasPixie) {
			((TileEntityJar)tile).releasePixie();
		}
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(ModBlocks.jar_empty);
	}

	@Override
	protected ItemStack getSilkTouchDrop(IBlockState state){
		return new ItemStack(ModBlocks.jar_empty);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if(empty) return false;
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityJar && ((TileEntityJar)tile).hasPixie && ((TileEntityJar)tile).hasProduced) {
			((TileEntityJar)tile).hasProduced = false;
			if (!world.isRemote) {
				world.spawnEntity(new EntityItem(world, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, new ItemStack(ModItems.pixie_dust)));
			}
			world.playSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5, ModSounds.PIXIE_HURT, SoundCategory.NEUTRAL, 1, 1, false);
		}
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile instanceof TileEntityJar) {
			if (!empty) {
				((TileEntityJar)tile).hasPixie = true;
				((TileEntityJar)tile).pixieType = stack.getMetadata();
			} else {
				((TileEntityJar)tile).hasPixie = false;
			}
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityJar(empty);
	}

	public static class ItemBlockJar extends ItemBlock {
		public ItemBlockJar(Block block) {
			super(block);
			this.maxStackSize = 1;
			this.setHasSubtypes(true);
		}

		@Override
		public String getTranslationKey(ItemStack stack) {
			return "tile.iceandfire.jar_" + stack.getMetadata();
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> subItems) {
			if(this.isInCreativeTab(tab)){
				for (int i = 0; i < 5; i++) {
					subItems.add(new ItemStack(this, 1, i));
				}
			}
		}
	}
}