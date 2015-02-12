package itemrender.client;


import itemrender.client.rendering.FBOHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.io.File;

@SideOnly(Side.CLIENT)
public class KeybindRenderInventoryBlock {

    public final KeyBinding key;
    /**
     * Key descriptions
     */
    private final String desc;
    /**
     * Default key values
     */
    private final int keyValue;
    public FBOHelper fbo;
    private String filenameSuffix = "";
    private RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();

    public KeybindRenderInventoryBlock(int textureSize, String filename_suffix, int keyval, String des) {
        fbo = new FBOHelper(textureSize);
        filenameSuffix = filename_suffix;
        keyValue = keyval;
        desc = des;
        key = new KeyBinding(desc, keyValue, "Item Render");
        ClientRegistry.registerKeyBinding(key);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (FMLClientHandler.instance().isGUIOpen(GuiChat.class))
            return;
        if (key.isPressed()) {
            Minecraft minecraft = FMLClientHandler.instance().getClient();
            if (minecraft.thePlayer != null) {
                ItemStack current = minecraft.thePlayer.getCurrentEquippedItem();
                if (current != null && current.getItem() != null) {

                    fbo.begin();

                    GL11.glMatrixMode(GL11.GL_PROJECTION);
                    GL11.glPushMatrix();
                    GL11.glLoadIdentity();
                    GL11.glOrtho(0, 16, 0, 16, -150.0, 150.0);

                    GL11.glMatrixMode(GL11.GL_MODELVIEW);
                    RenderHelper.enableGUIStandardItemLighting();

                    itemRenderer.renderItemIntoGUI(current, 0, 0);

                    GL11.glMatrixMode(GL11.GL_PROJECTION);
                    GL11.glPopMatrix();

                    RenderHelper.disableStandardItemLighting();

                    fbo.end();
                    fbo.saveToFile(new File(minecraft.mcDataDir, String.format("rendered/item_%s_%d%s.png", current.getItem().getUnlocalizedName(), current.getItemDamage(), filenameSuffix)));
                    fbo.restoreTexture();
                }
            }
        }
    }
}
