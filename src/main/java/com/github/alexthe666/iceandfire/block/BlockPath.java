package com.github.alexthe666.iceandfire.block;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.core.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrassPath;
import net.minecraft.block.SoundType;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockPath extends BlockGrassPath {

    public enum Type {
        CHARED, CRACKLED, FROZEN
    }

    public static final PropertyBool REVERTS = PropertyBool.create("revert");
    public Item itemBlock;
    Type type;

    @SuppressWarnings("deprecation")
    public BlockPath(Type type) {
        super();
        this.type = type;
        this.setTranslationKey(createTranslationKey());
        this.setHarvestLevel("shovel", 0);
        this.setHardness(0.6F);
        this.setSoundType(createSoundType());
        this.setCreativeTab(IceAndFire.TAB);
        if (type == Type.FROZEN) {
            this.slipperiness = 0.98F;
        }
        this.setLightOpacity(0);
        setRegistryName(getRegistryKey());
        this.setDefaultState(this.blockState.getBaseState().withProperty(REVERTS, Boolean.valueOf(false)));
        this.setTickRandomly(true);
    }

    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (!worldIn.isRemote) {
            if (!worldIn.isAreaLoaded(pos, 3))
                return;
            if (state.getValue(REVERTS) && rand.nextInt(3) == 0) {
                worldIn.setBlockState(pos, Blocks.GRASS_PATH.getDefaultState());
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        switch (side) {
            case UP:
                return true;
            case NORTH:
            case SOUTH:
            case WEST:
            case EAST:
                IBlockState iblockstate = blockAccess.getBlockState(pos.offset(side));
                Block block = iblockstate.getBlock();
                return !iblockstate.isOpaqueCube() && block != Blocks.FARMLAND && block != Blocks.GRASS_PATH && block != ModBlocks.charedGrassPath && block != ModBlocks.frozenGrassPath && block != ModBlocks.crackledGrassPath;
            default:
                return super.shouldSideBeRendered(blockState, blockAccess, pos, side);
        }
    }

    @Nullable
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return getBaseDirt().getItemDropped(getBaseDirt().getDefaultState(), rand, fortune);
    }

    @Override
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);

        if (worldIn.getBlockState(pos.up()).getMaterial().isSolid()) {
            worldIn.setBlockState(pos, getBaseDirt().getDefaultState());
        }
    }

    private void updateBlockState(World worldIn, BlockPos pos) {
        if (worldIn.getBlockState(pos.up()).getMaterial().isSolid()) {
            worldIn.setBlockState(pos, getBaseDirt().getDefaultState());
        }
    }

    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(REVERTS, meta == 1);
    }

    public int getMetaFromState(IBlockState state) {
        return state.getValue(REVERTS) ? 1 : 0;
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, REVERTS);
    }

    private String createTranslationKey() {
        if (type == Type.CHARED) {
            return "iceandfire.charedGrassPath";
        } else if (type == Type.FROZEN) {
            return "iceandfire.frozenGrassPath";
        }
        return "iceandfire.crackledGrassPath";
    }

    private SoundType createSoundType() {
        if (type == Type.FROZEN) {
            return SoundType.GLASS;
        }
        return SoundType.GROUND;
    }

    private String getRegistryKey() {
        if (type == Type.CHARED) {
            return "chared_grass_path";
        } else if (type == Type.FROZEN) {
            return "frozen_grass_path";
        }
        return "crackled_grass_path";
    }

    private Block getBaseDirt() {
        if (type == Type.CHARED) {
            return ModBlocks.charedDirt;
        } if (type == Type.FROZEN) {
            return ModBlocks.frozenDirt;
        }
        return ModBlocks.crackledDirt;
    }
}
