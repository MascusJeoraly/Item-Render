/*
 * Copyright (c) 2015 Jerrell Fang
 *
 * This project is Open Source and distributed under The MIT License (MIT)
 * (http://opensource.org/licenses/MIT)
 *
 * You should have received a copy of the The MIT License along with
 * this project.   If not, see <http://opensource.org/licenses/MIT>.
 */
package itemrender.client.keybind;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

/**
 * Created by Fang0716 on 7/2/2014.
 * <p/>
 * Just a warning.
 *
 * @author Meow J
 */
@SideOnly(Side.CLIENT)
public class KeybindWarn {
    public KeybindWarn() {
        ClientRegistry.registerKeyBinding(new KeyBinding("OpenGL Error", Keyboard.KEY_NONE, "Item Render"));
    }
}
