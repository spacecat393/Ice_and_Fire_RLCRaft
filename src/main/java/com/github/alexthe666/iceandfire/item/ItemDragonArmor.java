package com.github.alexthe666.iceandfire.item;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.StatCollector;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemDragonArmor extends Item {

	public int type;
	public String name;

	public ItemDragonArmor(int type, String name) {
		this.type = type;
		this.name = name;
		this.setUnlocalizedName("iceandfire." + name);
		this.setCreativeTab(IceAndFire.TAB);
		this.maxStackSize = 1;
		this.setRegistryName(IceAndFire.MODID, name);

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, NonNullList<ItemStack> items) {
		items.add(new ItemStack(this, 1, 0));
		items.add(new ItemStack(this, 1, 1));
		items.add(new ItemStack(this, 1, 2));
		items.add(new ItemStack(this, 1, 3));

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
		String words;
		switch (stack.getMetadata()) {
			default:
				words = "dragon.armor_head";
				break;
			case 1:
				words = "dragon.armor_neck";
				break;
			case 2:
				words = "dragon.armor_body";
				break;
			case 3:
				words = "dragon.armor_tail";
				break;
		}
		tooltip.add(StatCollector.translateToLocal(words));
	}
}
