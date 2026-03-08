package de.guntram.mcmod.easierchests;

import java.io.File;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class EasierChests implements ClientModInitializer
{
    static final String MODID="easierchests";
    static final String MODNAME="EasierChests";

    private static final KeyBinding.Category CATEGORY = KeyBinding.Category.create(Identifier.of(MODID, "key_categories"));

    public static KeyBinding keySortChest, keyMoveToChest,
                             keySortPlInv, keyMoveToPlInv,
                             keySearchBox;

    @Override
    public void onInitializeClient() {
        ConfigurationHandler confHandler = ConfigurationHandler.getInstance();
        File configDir = new File("config");
        confHandler.load(new File(configDir, MODID + ".properties"));
        FrozenSlotDatabase.init(configDir);

        keySortChest  = registerKey("sortchest",  GLFW.GLFW_KEY_KP_7);
        keyMoveToChest = registerKey("matchup",   GLFW.GLFW_KEY_KP_8);
        keySortPlInv  = registerKey("sortplayer", GLFW.GLFW_KEY_KP_1);
        keyMoveToPlInv = registerKey("matchdown", GLFW.GLFW_KEY_KP_2);
        keySearchBox  = registerKey("searchbox",  GLFW.GLFW_KEY_UNKNOWN);
    }

    private KeyBinding registerKey(String key, int code) {
        KeyBinding result = new KeyBinding("key.easierchests."+key, InputUtil.Type.KEYSYM, code, CATEGORY);
        KeyBindingHelper.registerKeyBinding(result);
        return result;
    }
}
