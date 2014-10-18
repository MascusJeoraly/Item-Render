/*
 * Copyright (c) 2014 Yu Fang
 *
 * This project is Open Source and distributed under The MIT License (MIT)
 * (http://opensource.org/licenses/MIT)
 *
 * You should have received a copy of the The MIT License along with
 * this project.   If not, see <http://opensource.org/licenses/MIT>.
 */

package itemrender.client;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.input.Keyboard;

/**
 * Created by Fang0716 on 7/2/2014.
 * <p/>
 * Just a warning.
 *
 * @author Meow J
 */
public class KeybindWarn {

    private static final String desc = "OpenGL Error";
    private static final int keyValues = Keyboard.KEY_NONE;
    public final KeyBinding key;

    public KeybindWarn() {
        key = new KeyBinding(desc, keyValues, "Item Render");
        ClientRegistry.registerKeyBinding(key);
    }
}
