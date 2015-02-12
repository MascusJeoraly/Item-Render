package itemrender;


import itemrender.client.*;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GLContext;

@Mod(modid = ItemRenderMod.MODID, name = "Item Render", version = "2.0-alpha"
        , dependencies = "required-after:Forge@[10.12.2.1147,);", guiFactory = "itemrender.ItemRenderGuiFactory")
public class ItemRenderMod {

    public static final String MODID = "ItemRender";

    @Mod.Instance("ItemRender")
    public static ItemRenderMod instance;
    @SideOnly(Side.CLIENT)
    private RenderTickHandler renderTickHandler = new RenderTickHandler();

    public static Configuration cfg;

    public static final int DEFAULT_MAIN_BLOCK_SIZE = 128;
    public static int mainBlockSize = DEFAULT_MAIN_BLOCK_SIZE;

    public static final int DEFAULT_GRID_BLOCK_SIZE = 32;
    public static int gridBlockSize = DEFAULT_GRID_BLOCK_SIZE;

    public static final int DEFAULT_MAIN_ENTITY_SIZE = 512;
    public static int mainEntitySize = DEFAULT_MAIN_ENTITY_SIZE;

    public static final int DEFAULT_GRID_ENTITY_SIZE = 128;
    public static int gridEntitySize = DEFAULT_GRID_ENTITY_SIZE;

    public static final int DEFAULT_PLAYER_SIZE = 1024;
    public static int playerSize = DEFAULT_PLAYER_SIZE;

    public static boolean gl32_enabled = false;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        if (event.getSide().isServer())
            return;
        gl32_enabled = GLContext.getCapabilities().OpenGL32;

        // Config
        cfg = new Configuration(event.getSuggestedConfigurationFile());
        syncConfig();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandItemRender());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if (event.getSide().isServer())
            return;
        FMLCommonHandler.instance().bus().register(instance);

        FMLCommonHandler.instance().bus().register(renderTickHandler);

        if (gl32_enabled) {
            KeybindRenderInventoryBlock defaultRender = new KeybindRenderInventoryBlock(mainBlockSize, "", Keyboard.KEY_LBRACKET, "Render Block (" + "Broken" + ")");
            RenderTickHandler.keybindToRender = defaultRender;
            FMLCommonHandler.instance().bus().register(new KeybindRenderEntity(mainEntitySize, "", Keyboard.KEY_SEMICOLON, "Render Entity (" + mainEntitySize + ")"));
            FMLCommonHandler.instance().bus().register(new KeybindRenderEntity(gridEntitySize, "_grid", Keyboard.KEY_APOSTROPHE, "Render Entity (" + gridEntitySize + ")"));
            FMLCommonHandler.instance().bus().register(defaultRender);
            FMLCommonHandler.instance().bus().register(new KeybindRenderInventoryBlock(gridBlockSize, "_grid", Keyboard.KEY_RBRACKET, "Render Block (" + "Broken" + ")"));
            FMLCommonHandler.instance().bus().register(new KeybindToggleRender());
            FMLCommonHandler.instance().bus().register(new KeybindRenderCurrentPlayer(playerSize, ""));
        } else {
            FMLCommonHandler.instance().bus().register(new KeybindWarn());
            FMLLog.log("Item Render", Level.ERROR, "[Item Render] OpenGL Error, please upgrade your drivers or system.");
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
