package itemrender.client;


import itemrender.client.rendering.FBOHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.File;

@SideOnly(Side.CLIENT)
public class KeybindRenderCurrentPlayer {

    /**
     * Key descriptions
     */
    private static final String desc = "Render Current Player";
    /**
     * Default key values
     */
    private static final int keyValues = Keyboard.KEY_P;
    public final KeyBinding key;
    public FBOHelper fbo;

    public KeybindRenderCurrentPlayer(int textureSize) {
        fbo = new FBOHelper(textureSize);
        key = new KeyBinding(desc, keyValues, "Item Render");
        ClientRegistry.registerKeyBinding(key);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (FMLClientHandler.instance().isGUIOpen(GuiChat.class))
            return;
        if (key.isPressed()) {
            Minecraft minecraft = FMLClientHandler.instance().getClient();
            Entity player = ReflectionHelper.getPrivateValue(Minecraft.class, minecraft, "field_175622_Z", "renderViewEntity");
            if (player != null) {
                EntityLivingBase current = (EntityLivingBase) player;
                fbo.begin();

                AxisAlignedBB aabb = current.getEntityBoundingBox();
                double minX = aabb.minX - current.posX;
                double maxX = aabb.maxX - current.posX;
                double minY = aabb.minY - current.posY;
                double maxY = aabb.maxY - current.posY;
                double minZ = aabb.minZ - current.posZ;
                double maxZ = aabb.maxZ - current.posZ;

                double minBound = Math.min(minX, Math.min(minY, minZ));
                double maxBound = Math.max(maxX, Math.max(maxY, maxZ));

                double boundLimit = Math.max(Math.abs(minBound), Math.abs(maxBound));

                GL11.glMatrixMode(GL11.GL_PROJECTION);
                GL11.glPushMatrix();
                GL11.glLoadIdentity();
                GL11.glOrtho(-boundLimit * 0.75, boundLimit * 0.75, -boundLimit * 1.25, boundLimit * 0.25, -100.0, 100.0);

                GL11.glMatrixMode(GL11.GL_MODELVIEW);

                KeybindRenderEntity.renderEntity(current, true);

                GL11.glMatrixMode(GL11.GL_PROJECTION);
                GL11.glPopMatrix();

                fbo.end();
                fbo.saveToFile(new File(minecraft.mcDataDir, String.format("rendered/player.png")));
                fbo.restoreTexture();
            }
        }
    }

}
