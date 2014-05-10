package itemrender;

import itemrender.client.KeybindRenderCurrentPlayer;
import itemrender.client.KeybindRenderEntity;
import itemrender.client.KeybindRenderInventoryBlock;
import itemrender.client.KeybindToggleRender;
import itemrender.client.RenderTickHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GLContext;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid = "itemrender", name = "Item Render", version = "1.4")
public class ItemRenderMod {

	@Mod.Instance("itemrender")
	public static ItemRenderMod instance;
	private RenderTickHandler renderTickHandler = new RenderTickHandler();

	public static final int DEFAULT_MAIN_BLOCK_SIZE = 128;
	private int mainBlockSize;
	public static final int DEFAULT_GRID_BLOCK_SIZE = 32;
	private int gridBlockSize;
	public static final int DEFAULT_MAIN_ENTITY_SIZE = 512;
	private int mainEntitySize;
	public static final int DEFAULT_GRID_ENTITY_SIZE = 128;
	private int gridEntitySize;
	public static final int DEFAULT_PLAYER_SIZE = 1024;
	private int playerSize;

	public static boolean gl32_enabled = false;

	// Won't crash again
	@Mod.EventHandler
	@SideOnly(Side.CLIENT)
	public void preInit(FMLPreInitializationEvent e) {
		gl32_enabled = GLContext.getCapabilities().OpenGL32;

		// Config
		Configuration cfg = new Configuration(e.getSuggestedConfigurationFile());
		cfg.load();
		mainBlockSize = cfg.get("Generals", "RenderBlockMain", DEFAULT_MAIN_BLOCK_SIZE).getInt();
		gridBlockSize = cfg.get("Generals", "RenderBlockGrid", DEFAULT_GRID_BLOCK_SIZE).getInt();
		mainEntitySize = cfg.get("Generals", "RenderEntityMain", DEFAULT_MAIN_ENTITY_SIZE).getInt();
		gridEntitySize = cfg.get("Generals", "RenderEntityGrid", DEFAULT_GRID_ENTITY_SIZE).getInt();
		playerSize = cfg.get("Generals", "RenderPlayer", DEFAULT_PLAYER_SIZE).getInt();
		cfg.save();

		MinecraftForge.EVENT_BUS.register(renderTickHandler);
		FMLCommonHandler.instance().bus().register(renderTickHandler);
		if (gl32_enabled) {
			KeybindRenderInventoryBlock defaultRender = new KeybindRenderInventoryBlock(mainBlockSize, "", Keyboard.KEY_LBRACKET, "Render Block (" + mainBlockSize + ")");
			RenderTickHandler.keybindToRender = defaultRender;
			FMLCommonHandler.instance().bus().register(new KeybindRenderEntity(mainEntitySize, "", Keyboard.KEY_SEMICOLON, "Render Entity (" + mainEntitySize + ")"));
			FMLCommonHandler.instance().bus().register(new KeybindRenderEntity(gridEntitySize, "_grid", Keyboard.KEY_APOSTROPHE, "Render Entity (" + gridEntitySize + ")"));
			FMLCommonHandler.instance().bus().register(defaultRender);
			FMLCommonHandler.instance().bus().register(new KeybindRenderInventoryBlock(gridBlockSize, "_grid", Keyboard.KEY_RBRACKET, "Render Block (" + gridBlockSize + ")"));
			FMLCommonHandler.instance().bus().register(new KeybindToggleRender());
			FMLCommonHandler.instance().bus().register(new KeybindRenderCurrentPlayer(playerSize, ""));
		}
	}
}
