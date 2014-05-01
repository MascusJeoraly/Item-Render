package itemrender.client;

import itemrender.client.rendering.FBOHelper;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class KeybindRenderInventoryBlock {

	/** Key descriptions */
	private static final String desc = "Render Block";
	/** Default key values */
	private static final int keyValues = Keyboard.KEY_P;
	public final KeyBinding key;

	public FBOHelper fbo;
	private String filenameSuffix = "";
	private RenderItem itemRenderer = new RenderItem();

	public KeybindRenderInventoryBlock(int textureSize, String filename_suffix) {
		fbo = new FBOHelper(textureSize);
		filenameSuffix = filename_suffix;
		key = new KeyBinding(desc, keyValues, "Item Render");
		ClientRegistry.registerKeyBinding(key);
	}

	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event) {
		// FMLClientHandler.instance().getClient().inGameHasFocus
		if (FMLClientHandler.instance().isGUIOpen(GuiChat.class))
			return;
		if (key.isPressed()) {
			Minecraft minecraft = FMLClientHandler.instance().getClient();
			if (minecraft.thePlayer != null) {
				ItemStack current = minecraft.thePlayer
						.getCurrentEquippedItem();
				if (current != null && current.getItem() != null) {

					fbo.begin();

					GL11.glMatrixMode(GL11.GL_PROJECTION);
					GL11.glPushMatrix();
					GL11.glLoadIdentity();
					GL11.glOrtho(0, 16, 0, 16, -100.0, 100.0);

					GL11.glMatrixMode(GL11.GL_MODELVIEW);

					RenderHelper.enableGUIStandardItemLighting();

					RenderBlocks renderBlocks = ReflectionHelper
							.getPrivateValue(Render.class, itemRenderer,
									"field_147909_c", "renderBlocks");
					if (!ForgeHooksClient.renderInventoryItem(renderBlocks,
							minecraft.renderEngine, current, true, 0.0f,
							(float) 0, (float) 0)) {
						itemRenderer.renderItemIntoGUI(null,
								minecraft.renderEngine, current, 0, 0);
					}

					GL11.glMatrixMode(GL11.GL_PROJECTION);
					GL11.glPopMatrix();

					RenderHelper.disableStandardItemLighting();

					fbo.end();

					fbo.saveToFile(new File(minecraft.mcDataDir, String.format(
							"rendered/item_%s_%d%s.png", current.getItem()
									.getUnlocalizedName(), current
									.getItemDamage(), filenameSuffix)));

					fbo.restoreTexture();
				}
			}
		}
	}
}
