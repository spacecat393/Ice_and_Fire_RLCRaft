package com.github.alexthe666.iceandfire.event;

import com.github.alexthe666.iceandfire.IceAndFireConfig;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class EventNewMenu {

	public static ResourceLocation[] panorama = new ResourceLocation[]{new ResourceLocation("iceandfire:textures/gui/panorama_0.png"), new ResourceLocation("textures/gui/title/background/panorama_1.png"), new ResourceLocation("textures/gui/title/background/panorama_2.png"), new ResourceLocation("textures/gui/title/background/panorama_3.png"), new ResourceLocation("textures/gui/title/background/panorama_4.png"), new ResourceLocation("textures/gui/title/background/panorama_5.png")};
	private static Field PATHS;

	@SubscribeEvent
	public void openMainMenu(GuiOpenEvent event) {
		if (event.getGui() instanceof GuiMainMenu && IceAndFireConfig.CLIENT_SETTINGS.customMainMenu) {
			GuiMainMenu mainMenu = (GuiMainMenu) event.getGui();
			try {
				if(PATHS == null) {
					PATHS = ObfuscationReflectionHelper.findField(GuiMainMenu.class, "field_73978_o");
					PATHS.setAccessible(true);
					Field modifier = Field.class.getDeclaredField("modifiers");
					modifier.setAccessible(true);
					modifier.setInt(PATHS, PATHS.getModifiers() & ~Modifier.FINAL);
				}
				PATHS.set(mainMenu, panorama);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}