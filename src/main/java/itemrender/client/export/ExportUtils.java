/*
 * Copyright (c) 2015 Jerrell Fang
 *
 * This project is Open Source and distributed under The MIT License (MIT)
 * (http://opensource.org/licenses/MIT)
 *
 * You should have received a copy of the The MIT License along with
 * this project.   If not, see <http://opensource.org/licenses/MIT>.
 */

package itemrender.client.export;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import itemrender.ItemRenderMod;
import itemrender.client.rendering.FBOHelper;
import itemrender.client.rendering.Renderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Meow J on 8/17/2015.
 *
 * @author Meow J
 */
public class ExportUtils {
    public static ExportUtils INSTANCE;

    private FBOHelper fboSmall;
    private FBOHelper fboLarge;
    private RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
    private List<ItemData> itemDataList = new ArrayList<ItemData>();

    public ExportUtils() {
        // Hardcoded value for mcmod.cn only, don't change this unless the website updates
        fboSmall = new FBOHelper(32);
        fboLarge = new FBOHelper(128);
    }


    public String getLocalizedName(ItemStack itemStack) {
        return itemStack.getDisplayName();
    }

    public String getType(ItemStack itemStack) {
        return (itemStack.getItem() instanceof ItemBlock) ? "Block" : "Item";
    }

    public String getSmallIcon(ItemStack itemStack) {
        return Renderer.getItemBase64(itemStack, fboSmall, itemRenderer);
    }

    public String getLargeIcon(ItemStack itemStack) {
        return Renderer.getItemBase64(itemStack, fboLarge, itemRenderer);
    }

    private String getItemOwner(ItemStack itemStack) {
        ResourceLocation registryName = itemStack.getItem().getRegistryName();
        return registryName == null ? "unnamed" : registryName.getResourceDomain();
    }

    public void exportMods() throws IOException {
        Minecraft minecraft = FMLClientHandler.instance().getClient();
        itemDataList.clear();
        List<String> modList = new ArrayList<String>();

        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        ItemData itemData;
        String identifier;

        for (ItemStack itemStack : ItemList.items) {
            if (itemStack == null) continue;
            if (getItemOwner(itemStack).equals("minecraft") && !ItemRenderMod.exportVanillaItems) continue;

            identifier = itemStack.getItem().getUnlocalizedName() + "@" + itemStack.getMetadata();
            if (ItemRenderMod.blacklist.contains(identifier)) continue;

            itemData = new ItemData(itemStack);
            itemDataList.add(itemData);
            if (!modList.contains(getItemOwner(itemStack))) modList.add(getItemOwner(itemStack));
        }

        // Since refreshResources takes a long time, only refresh once for all the items
        minecraft.getLanguageManager().setCurrentLanguage(new Language("zh_CN", "中国", "简体中文", false));
        minecraft.gameSettings.language = "zh_CN";
        minecraft.refreshResources();
        minecraft.gameSettings.saveOptions();

        for (ItemData data : itemDataList) {
            if (ItemRenderMod.debugMode)
                ItemRenderMod.instance.log.info(I18n.format("itemrender.msg.addCN", data.getItemStack().getItem().getUnlocalizedName() + "@" + data.getItemStack().getMetadata()));
            data.setName(this.getLocalizedName(data.getItemStack()));
        }

        minecraft.getLanguageManager().setCurrentLanguage(new Language("en_US", "US", "English", false));
        minecraft.gameSettings.language = "en_US";
        minecraft.refreshResources();
        minecraft.fontRenderer.setUnicodeFlag(false);
        minecraft.gameSettings.saveOptions();

        for (ItemData data : itemDataList) {
            if (ItemRenderMod.debugMode)
                ItemRenderMod.instance.log.info(I18n.format("itemrender.msg.addEN", data.getItemStack().getItem().getUnlocalizedName() + "@" + data.getItemStack().getMetadata()));
            data.setEnglishName(this.getLocalizedName(data.getItemStack()));
        }

        File export;

        for (String modid : modList) {
            export = new File(minecraft.mcDataDir, String.format("export/%s.json", modid.replaceAll("[^A-Za-z0-9()\\[\\]]", "")));
            if (!export.getParentFile().exists()) export.getParentFile().mkdirs();
            if (!export.exists()) export.createNewFile();
            PrintWriter pw = new PrintWriter(export, "UTF-8");

            for (ItemData data : itemDataList) {
                if (modid.equals(getItemOwner(data.getItemStack())))
                    pw.println(gson.toJson(data));
            }
            pw.close();
        }
    }
}
