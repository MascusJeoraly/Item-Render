/*
 * Copyright (c) 2015 Jerrell Fang
 *
 * This project is Open Source and distributed under The MIT License (MIT)
 * (http://opensource.org/licenses/MIT)
 *
 * You should have received a copy of the The MIT License along with
 * this project.   If not, see <http://opensource.org/licenses/MIT>.
 */
package itemrender.client.rendering;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.codec.binary.Base64;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL32;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;

@SideOnly(Side.CLIENT)
public class FBOHelper {
    public int renderTextureSize = 128;
    public int framebufferID = -1;
    public int depthbufferID = -1;
    public int textureID = -1;

    private IntBuffer lastViewport;
    private int lastTexture;
    private int lastFramebuffer;

    public FBOHelper(int textureSize) {
        renderTextureSize = textureSize;

        createFramebuffer();
    }

    public void resize(int newSize) {
        deleteFramebuffer();
        renderTextureSize = newSize;
        createFramebuffer();
    }

    public void begin() {
        // Remember current framebuffer.
        lastFramebuffer = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);

        // Render to our texture
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebufferID);

        // Remember viewport info.
        lastViewport = GLAllocation.createDirectIntBuffer(16);
        GL11.glGetInteger(GL11.GL_VIEWPORT, lastViewport);
        GL11.glViewport(0, 0, renderTextureSize, renderTextureSize);

        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();

        // Remember current texture.
        lastTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);

        GlStateManager.clearColor(0, 0, 0, 0);
        GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        GlStateManager.cullFace(GL11.GL_FRONT);
        GlStateManager.enableDepth();
        GlStateManager.enableLighting();
        GlStateManager.enableRescaleNormal();
    }

    public void end() {
        GlStateManager.cullFace(GL11.GL_BACK);
        GlStateManager.disableDepth();
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableLighting();

        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.popMatrix();

        // Revert to last viewport
        GL11.glViewport(lastViewport.get(0), lastViewport.get(1), lastViewport.get(2), lastViewport.get(3));

        // Revert to default framebuffer
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, lastFramebuffer);

        // Revert to last texture
        GlStateManager.bindTexture(lastTexture);
    }

    public void bind() {
        GlStateManager.bindTexture(textureID);
    }

    // This is only a separate function because the texture gets messed with
    // after you're done rendering to read the FBO
    public void restoreTexture() {
        GlStateManager.bindTexture(lastTexture);
    }

    public void saveToFile(File file) {
        // Bind framebuffer texture
        GlStateManager.bindTexture(textureID);

        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

        int width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        int height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

        IntBuffer texture = BufferUtils.createIntBuffer(width * height);
        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, texture);

        int[] texture_array = new int[width * height];
        texture.get(texture_array);

        BufferedImage image = new BufferedImage(renderTextureSize, renderTextureSize, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, renderTextureSize, renderTextureSize, texture_array, 0, width);

        file.mkdirs();
        try {
            ImageIO.write(image, "png", file);
        } catch (Exception e) {
            // Do nothing
        }
    }

    public String getBase64() {
        // Bind framebuffer texture
        GlStateManager.bindTexture(textureID);

        GL11.glPixelStorei(GL11.GL_PACK_ALIGNMENT, 1);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

        int width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        int height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

        IntBuffer texture = BufferUtils.createIntBuffer(width * height);
        GL11.glGetTexImage(GL11.GL_TEXTURE_2D, 0, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, texture);

        int[] texture_array = new int[width * height];
        texture.get(texture_array);

        BufferedImage image = new BufferedImage(renderTextureSize, renderTextureSize, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, renderTextureSize, renderTextureSize, texture_array, 0, width);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "PNG", out);
        } catch (IOException e) {
            // Do nothing
        }

        return Base64.encodeBase64String(out.toByteArray());
    }

    private void createFramebuffer() {
        framebufferID = GL30.glGenFramebuffers();
        textureID = GL11.glGenTextures();
        int currentTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, framebufferID);

        // Set our texture up, empty.
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, renderTextureSize, renderTextureSize, 0, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE, (java.nio.ByteBuffer) null);

        // Restore old texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, currentTexture);

        // Create depth buffer
        depthbufferID = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthbufferID);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL11.GL_DEPTH_COMPONENT, renderTextureSize, renderTextureSize);

        // Bind depth buffer to the framebuffer
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, depthbufferID);

        // Bind our texture to the framebuffer
        GL32.glFramebufferTexture(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, textureID, 0);

        // Revert to default framebuffer
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    private void deleteFramebuffer() {
        GL30.glDeleteFramebuffers(framebufferID);
        GL11.glDeleteTextures(textureID);
        GL30.glDeleteRenderbuffers(depthbufferID);
    }
}
