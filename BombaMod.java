package pl.bombamod;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class BombaMod implements ClientModInitializer {
    /** Komenda wysyłana po kliknięciu przycisku akcji. %s to nick gracza. Zmień ją na własną. */
    public static final String COMMAND = "msg %s Czesc!";

    private static KeyBinding openKey;

    @Override
    public void onInitializeClient() {
        openKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.bombamod.open",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                "category.bombamod"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openKey.wasPressed()) {
                if (client.world != null) {
                    client.setScreen(new PlayersScreen());
                }
            }
        });
    }
}
