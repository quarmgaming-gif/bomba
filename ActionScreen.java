package pl.bombamod;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

/** Okno po kliknięciu gracza: nick, ekwipunek i przycisk akcji. */
public class ActionScreen extends Screen {
    private static final String[] LABELS = {"Hełm", "Napierśnik", "Spodnie", "Buty", "W łapce"};

    private final Screen parent;
    private final PlayerListEntry entry;

    public ActionScreen(Screen parent, PlayerListEntry entry) {
        super(Text.literal("Wybierz akcję"));
        this.parent = parent;
        this.entry = entry;
    }

    @Override
    protected void init() {
        int cx = width / 2;
        int y = height / 2 + 50;
        addDrawableChild(ButtonWidget.builder(Text.literal("Wyślij wiadomość"), b -> {
            MinecraftClient mc = MinecraftClient.getInstance();
            if (mc.player != null) {
                mc.player.networkHandler.sendChatCommand(
                        String.format(BombaMod.COMMAND, entry.getProfile().getName()));
            }
            mc.setScreen(null);
        }).dimensions(cx - 100, y, 200, 22).build());

        addDrawableChild(ButtonWidget.builder(Text.literal("Wstecz"), b ->
                MinecraftClient.getInstance().setScreen(parent)
        ).dimensions(cx - 40, y + 28, 80, 20).build());
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, width, height, 0xD0101820);
        int cx = width / 2;
        int top = height / 2 - 80;

        PlayerSkinDrawer.draw(ctx, entry.getSkinTextures(), cx - 100, top, 32);
        ctx.drawText(textRenderer, entry.getProfile().getName(), cx - 60, top + 5, 0xFFFFFFFF, true);
        ctx.drawText(textRenderer, "Wybierz akcję", cx - 60, top + 18, 0xFF7F90A3, false);
        ctx.fill(cx - 100, top + 40, cx + 100, top + 41, 0xFF243242);

        PlayerEntity p = PlayersScreen.playerFor(entry);
        if (p == null) {
            ctx.drawText(textRenderer, "Ekwipunek niedostępny (gracz poza zasięgiem)", cx - 100, top + 50, 0xFF7F90A3, false);
        } else {
            ItemStack[] g = PlayersScreen.gear(p);
            for (int i = 0; i < g.length; i++) {
                int y = top + 48 + i * 14;
                String name = g[i].isEmpty() ? "puste" : g[i].getName().getString();
                ctx.drawText(textRenderer, LABELS[i] + ": " + name, cx - 100, y, g[i].isEmpty() ? 0xFF7F90A3 : 0xFFFFFFFF, false);
            }
        }
        super.render(ctx, mouseX, mouseY, delta);
    }
}
