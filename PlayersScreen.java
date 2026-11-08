package pl.bombamod;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/** Lista graczy z serwera: głowa, nick, ping i ekwipunek (jeśli gracz jest w zasięgu renderowania). */
public class PlayersScreen extends Screen {
    private static final int ROW_H = 30;
    private static final int PANEL_W = 380;
    private static final int TOP = 40;

    private TextFieldWidget search;
    private int scroll = 0;

    public PlayersScreen() {
        super(Text.literal("Gracze"));
    }

    @Override
    protected void init() {
        int left = width / 2 - PANEL_W / 2;
        search = new TextFieldWidget(textRenderer, left + PANEL_W - 150, 12, 150, 18, Text.literal("Szukaj"));
        search.setPlaceholder(Text.literal("Wyszukaj gracza..."));
        search.setChangedListener(s -> scroll = 0);
        addDrawableChild(search);
    }

    private List<PlayerListEntry> players() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.getNetworkHandler() == null) return List.of();
        String q = search == null ? "" : search.getText().toLowerCase(Locale.ROOT);
        List<PlayerListEntry> list = new ArrayList<>();
        for (PlayerListEntry e : mc.getNetworkHandler().getPlayerList()) {
            if (e.getProfile().getName().toLowerCase(Locale.ROOT).contains(q)) list.add(e);
        }
        list.sort(Comparator.comparing(e -> e.getProfile().getName().toLowerCase(Locale.ROOT)));
        return list;
    }

    static PlayerEntity playerFor(PlayerListEntry e) {
        MinecraftClient mc = MinecraftClient.getInstance();
        return mc.world == null ? null : mc.world.getPlayerByUuid(e.getProfile().getId());
    }

    static ItemStack[] gear(PlayerEntity p) {
        return new ItemStack[]{
                p.getEquippedStack(EquipmentSlot.HEAD),
                p.getEquippedStack(EquipmentSlot.CHEST),
                p.getEquippedStack(EquipmentSlot.LEGS),
                p.getEquippedStack(EquipmentSlot.FEET),
                p.getMainHandStack()
        };
    }

    private int visibleRows() {
        return Math.max(1, (height - TOP - 12) / ROW_H);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, width, height, 0xD0101820);
        int left = width / 2 - PANEL_W / 2;
        ctx.drawText(textRenderer, "Gracze", left, 17, 0xFFFFFFFF, true);

        List<PlayerListEntry> list = players();
        int vis = visibleRows();
        scroll = Math.max(0, Math.min(scroll, Math.max(0, list.size() - vis)));

        for (int i = 0; i < vis && i + scroll < list.size(); i++) {
            PlayerListEntry e = list.get(i + scroll);
            int y = TOP + i * ROW_H;
            boolean hover = mouseX >= left && mouseX < left + PANEL_W && mouseY >= y && mouseY < y + ROW_H - 2;
            ctx.fill(left, y, left + PANEL_W, y + ROW_H - 2, hover ? 0xFF243242 : 0xFF18222E);
            PlayerSkinDrawer.draw(ctx, e.getSkinTextures(), left + 5, y + 4, 20);
            ctx.drawText(textRenderer, e.getProfile().getName(), left + 32, y + 5, 0xFFFFFFFF, true);
            ctx.drawText(textRenderer, e.getLatency() + " ms", left + 32, y + 16, 0xFF7F90A3, false);

            PlayerEntity p = playerFor(e);
            if (p != null) {
                ItemStack[] g = gear(p);
                for (int k = 0; k < g.length; k++) {
                    ctx.drawItem(g[k], left + PANEL_W - 5 * 18 - 2 + k * 18, y + 5);
                }
            }
        }
        super.render(ctx, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;
        int left = width / 2 - PANEL_W / 2;
        if (mouseX < left || mouseX >= left + PANEL_W || mouseY < TOP) return false;
        int row = (int) ((mouseY - TOP) / ROW_H);
        List<PlayerListEntry> list = players();
        int idx = row + scroll;
        if (row < visibleRows() && idx >= 0 && idx < list.size()) {
            MinecraftClient.getInstance().setScreen(new ActionScreen(this, list.get(idx)));
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scroll -= (int) Math.signum(verticalAmount);
        return true;
    }
}
