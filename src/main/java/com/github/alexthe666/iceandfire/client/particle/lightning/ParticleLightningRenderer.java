package com.github.alexthe666.iceandfire.client.particle.lightning;

import com.github.alexthe666.iceandfire.IceAndFire;
import com.github.alexthe666.iceandfire.client.particle.ParticleLightning;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayDeque;
import java.util.Deque;

/*
 * This class was created by <ChickenBones>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
public final class ParticleLightningRenderer {

    private ParticleLightningRenderer() {}

    private static final int BATCH_THRESHOLD = 200;
    private static final ResourceLocation outsideResource = new ResourceLocation("iceandfire:textures/models/lightningdragon/wisp_large.png");
    private static final ResourceLocation insideResource = new ResourceLocation("iceandfire:textures/models/lightningdragon/wisp_small.png");
    public static final Deque<ParticleLightning> queuedLightningBolts = new ArrayDeque<>();

    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        if (queuedLightningBolts.isEmpty()) {
            return;
        }

        float frame = event.getPartialTicks();
        Entity entity = Minecraft.getMinecraft().player;
        TextureManager render = Minecraft.getMinecraft().renderEngine;

        double interpPosX = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * frame;
        double interpPosY = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * frame;
        double interpPosZ = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * frame;

        GlStateManager.pushMatrix();
        GlStateManager.translate(-interpPosX, -interpPosY, -interpPosZ);

        Tessellator tessellator = Tessellator.getInstance();

        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        render.bindTexture(outsideResource);
        int counter = 0;

        tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
        for (ParticleLightning bolt : queuedLightningBolts) {
            bolt.renderBolt(0, false);
            if(counter % BATCH_THRESHOLD == BATCH_THRESHOLD - 1) {
                tessellator.draw();
                tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
            }
            counter++;
        }
        tessellator.draw();

        render.bindTexture(insideResource);
        counter = 0;

        tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
        for (ParticleLightning bolt : queuedLightningBolts) {
            bolt.renderBolt(1, true);
            if(counter % BATCH_THRESHOLD == BATCH_THRESHOLD - 1) {
                tessellator.draw();
                tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_LMAP_COLOR);
            }
            counter++;
        }
        tessellator.draw();

        queuedLightningBolts.clear();

        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);

        GlStateManager.translate(interpPosX, interpPosY, interpPosZ);
        GlStateManager.popMatrix();
    }
}
