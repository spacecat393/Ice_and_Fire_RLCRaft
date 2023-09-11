package com.github.alexthe666.iceandfire.client.texture;

import com.github.alexthe666.iceandfire.IceAndFire;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.io.IOUtils;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.IOException;

@SideOnly(Side.CLIENT)
public class DesaturatedStonedTexture extends AbstractTexture {

    private final ResourceLocation primaryTexture;
    private final ResourceLocation stoneTexture;

    public DesaturatedStonedTexture(ResourceLocation primaryTexture, ResourceLocation stoneTexture) {
        this.primaryTexture = primaryTexture;
        this.stoneTexture = stoneTexture;
    }

    @Override
    public void loadTexture(IResourceManager resourceManager) throws IOException {
        this.deleteGlTexture();
        BufferedImage finalImage = null;
        IResource iresource = null;
        try {
            iresource = resourceManager.getResource(primaryTexture);
            BufferedImage source1 = TextureUtil.readBufferedImage(iresource.getInputStream());
            ColorConvertOp colorConvert = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
            colorConvert.filter(source1, source1);
            finalImage = new BufferedImage(source1.getWidth(), source1.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D graph = finalImage.createGraphics();
            graph.drawImage(source1, 0, 0, null);
            iresource.close();
            iresource = resourceManager.getResource(stoneTexture);
            BufferedImage source2 = TextureUtil.readBufferedImage(iresource.getInputStream());
            graph.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.8F));
            graph.drawImage(source2.getScaledInstance(finalImage.getWidth(), finalImage.getHeight(), Image.SCALE_DEFAULT), 0, 0, null);
        } catch (IOException ioexception) {
            IceAndFire.logger.error("Couldn't load desaturated layered image", ioexception);
        } finally {
            IOUtils.closeQuietly(iresource);
        }
        TextureUtil.uploadTextureImage(this.getGlTextureId(), finalImage);
    }
}