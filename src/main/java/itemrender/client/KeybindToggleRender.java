package itemrender.client;


import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class KeybindToggleRender {

    /**
     * Key descriptions
     */
    private static final String desc = "Toggle Render";
    /**
     * Default key values
     */
    private static final int keyValues = Keyboard.KEY_O;
    public final KeyBinding key;

    public KeybindToggleRender() {
        key = new KeyBinding(desc, keyValues, "Item Render");
        ClientRegistry.registerKeyBinding(key);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (FMLClientHandler.instance().isGUIOpen(GuiChat.class))
            return;
        if (key.isPressed()) {
            RenderTickHandler.renderPreview = !RenderTickHandler.renderPreview;
        }
    }
}
