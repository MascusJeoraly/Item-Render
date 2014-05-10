package itemrender.client;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent.KeyInputEvent;

public class KeybindToggleRender {

	/** Key descriptions */
	private static final String desc = "Toggle Render";
	/** Default key values */
	private static final int keyValues = Keyboard.KEY_O;
	public final KeyBinding key;

	public KeybindToggleRender() {
		key = new KeyBinding(desc, keyValues, "Item Render");
		ClientRegistry.registerKeyBinding(key);
	}

	@SubscribeEvent
	public void onKeyInput(KeyInputEvent event) {
		if (FMLClientHandler.instance().isGUIOpen(GuiChat.class))
			return;
		if (key.isPressed()) {
			RenderTickHandler.renderPreview = !RenderTickHandler.renderPreview;
		}
	}
}
