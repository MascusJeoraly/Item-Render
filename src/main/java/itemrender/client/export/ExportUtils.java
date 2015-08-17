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
import itemrender.client.rendering.FBOHelper;
import itemrender.client.rendering.Renderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.Language;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
@SideOnly(Side.CLIENT)
public class ExportUtils {
    public static ExportUtils INSTANCE;

    private FBOHelper fboSmall;
    private FBOHelper fboLarge;
    private RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();
    private List<ItemData> itemDataList = new ArrayList<ItemData>();

    public ExportUtils() {
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

    public List<ItemStack> getItemList(String modid) {
        List<ItemStack> list = new ArrayList<ItemStack>();
        for (ItemStack itemStack : ItemList.items) {
            GameRegistry.UniqueIdentifier uniqueIdentity = GameRegistry.findUniqueIdentifierFor(itemStack.getItem());
            String id = uniqueIdentity == null ? "" : uniqueIdentity.modId;

            if (modid.equals(id)) list.add(itemStack);
        }
        return list;
    }

    public void exportMod(String modid) throws IOException {
        List<ItemStack> stackList = getItemList(modid);
        if (modid.equals("")) modid = "unnamed";
        File export = new File(Minecraft.getMinecraft().mcDataDir, String.format("export/%s.json", modid));
        if (!export.getParentFile().exists()) export.getParentFile().mkdirs();
        if (!export.exists()) export.createNewFile();

        itemDataList.clear();

        Gson gson = new Gson();
        PrintWriter pw = new PrintWriter(export, "UTF-8");
        ItemData itemData;
        for (ItemStack itemStack : stackList) {
            if (itemStack == null) continue;
            itemData = new ItemData(itemStack);
            itemDataList.add(itemData);
        }
        // TODO To be optimized
        Minecraft.getMinecraft().getLanguageManager().setCurrentLanguage(new Language("zh_CN", "中国", "简体中文", false));
        Minecraft.getMinecraft().refreshResources();

        for (ItemData data : itemDataList) {
            data.setName(this.getLocalizedName(data.getItemStack()));
        }

        Minecraft.getMinecraft().getLanguageManager().setCurrentLanguage(new Language("en_US", "US", "English", false));
        Minecraft.getMinecraft().refreshResources();

        for (ItemData data : itemDataList) {
            data.setEnglishName(this.getLocalizedName(data.getItemStack()));
            pw.println(gson.toJson(data));
        }

        pw.close();
    }
}
