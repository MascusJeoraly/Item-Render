package itemrender;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import itemrender.client.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GLContext;

@Mod(modid = ItemRenderMod.MODID, name = "Item Render", version = "1.5", dependencies = "required-after:Forge@[10.12.2.1147,);", guiFactory = "itemrender.ItemRenderGuiFactory")
public class ItemRenderMod {

    public static final String MODID = "ItemRender";

    @Mod.Instance("ItemRender")
    public static ItemRenderMod instance;

    private RenderTickHandler renderTickHandler = new RenderTickHandler();

    public static Configuration cfg;

    public static final int DEFAULT_MAIN_BLOCK_SIZE = 128;
    public static int mainBlockSize;

    public static final int DEFAULT_GRID_BLOCK_SIZE = 32;
    public static int gridBlockSize;

    public static final int DEFAULT_MAIN_ENTITY_SIZE = 512;
    public static int mainEntitySize;

    public static final int DEFAULT_GRID_ENTITY_SIZE = 128;
    public static int gridEntitySize;

    public static final int DEFAULT_PLAYER_SIZE = 1024;
    public static int playerSize;

    public static boolean gl32_enabled = false;

    @Mod.EventHandler
    @SideOnly(Side.CLIENT)
    public void preInit(FMLPreInitializationEvent event) {
        gl32_enabled = GLContext.getCapabilities().OpenGL32;

        // Config
        cfg = new Configuration(event.getSuggestedConfigurationFile());
        syncConfig();
    }

    @Mod.EventHandler
    @SideOnly(Side.CLIENT)
    public void init(FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(instance);

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


    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.modID.equals(ItemRenderMod.MODID))
            syncConfig();
    }

    public static void syncConfig() {
        mainBlockSize = cfg.get(Configuration.CATEGORY_GENERAL, "RenderBlockMain", DEFAULT_MAIN_BLOCK_SIZE, "Main size of export block image").getInt();
        gridBlockSize = cfg.get(Configuration.CATEGORY_GENERAL, "RenderBlockGrid", DEFAULT_GRID_BLOCK_SIZE, "Grid size of export block image").getInt();
        mainEntitySize = cfg.get(Configuration.CATEGORY_GENERAL, "RenderEntityMain", DEFAULT_MAIN_ENTITY_SIZE, "Main size of export entity image").getInt();
        gridEntitySize = cfg.get(Configuration.CATEGORY_GENERAL, "RenderEntityGrid", DEFAULT_GRID_ENTITY_SIZE, "Grid size of export entity image").getInt();
        playerSize = cfg.get(Configuration.CATEGORY_GENERAL, "RenderPlayer", DEFAULT_PLAYER_SIZE, "Size of export player image").getInt();
        if (cfg.hasChanged())
            cfg.save();
    }
}
