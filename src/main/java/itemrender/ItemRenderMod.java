package itemrender;

import itemrender.client.KeybindRenderCurrentPlayer;
import itemrender.client.KeybindRenderEntity;
import itemrender.client.KeybindRenderInventoryBlock;
import itemrender.client.KeybindToggleRender;
import itemrender.client.RenderTickHandler;
import net.minecraftforge.common.MinecraftForge;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GLContext;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = "itemrender")
public class ItemRenderMod {

	@Instance("itemrender")
	public static ItemRenderMod instance;
	private RenderTickHandler renderTickHandler = new RenderTickHandler();

	public static boolean gl32_enabled = false;

	// Won't crash again
	@EventHandler
	@SideOnly(Side.CLIENT)
	public void preInit(FMLPreInitializationEvent e) {
		gl32_enabled = GLContext.getCapabilities().OpenGL32;

		MinecraftForge.EVENT_BUS.register(renderTickHandler);
		FMLCommonHandler.instance().bus().register(renderTickHandler);
		if (gl32_enabled) {
			KeybindRenderInventoryBlock defaultRender = new KeybindRenderInventoryBlock(128, "", Keyboard.KEY_LBRACKET, "Render Block (128)");
			RenderTickHandler.keybindToRender = defaultRender;
			FMLCommonHandler.instance().bus().register(new KeybindRenderEntity(128, "", Keyboard.KEY_SEMICOLON, "Render Entity (128)"));
			FMLCommonHandler.instance().bus().register(new KeybindRenderEntity(32, "_grid", Keyboard.KEY_APOSTROPHE, "Render Entity (32)"));
			FMLCommonHandler.instance().bus().register(defaultRender);
			FMLCommonHandler.instance().bus().register(new KeybindRenderInventoryBlock(32, "_grid", Keyboard.KEY_RBRACKET, "Render Block (32)"));
			FMLCommonHandler.instance().bus().register(new KeybindToggleRender());
			FMLCommonHandler.instance().bus().register(new KeybindRenderCurrentPlayer(128, ""));
		}
	}

	@EventHandler
	public void init(FMLInitializationEvent e) {
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
	}
}
