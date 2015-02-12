package itemrender;

import itemrender.client.KeybindRenderEntity;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class CommandItemRender extends CommandBase {

    @Override
    public String getName() {
        return "itemrender";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/itemrender scale [value]";
    }

    @Override
    public void execute(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "/itemrender scale [value]"));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "Use this command to control entity rendering scale."));
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "Scale Range: (0.0, 2.0]. Default: 1.0. Current: " + KeybindRenderEntity.EntityRenderScale));
        } else if (args[0].equalsIgnoreCase("scale")) {
            if (args.length == 2) {
                float value = Float.valueOf(args[1]);
                if (value > 0.0F && value <= 2.0F) {
                    KeybindRenderEntity.EntityRenderScale = Float.valueOf(args[1]);
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Scale: " + value));
                } else {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Scale Range: (0.0, 2.0]"));
                }
            } else {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.AQUA + "Current Scale: " + KeybindRenderEntity.EntityRenderScale));
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Execute /itemrender scale [value] to tweak entity rendering scale."));
            }
        }
    }
}
