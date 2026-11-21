package com.catlean;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class ClickGuiScreen extends Screen {
    private static Category selected = Category.PERFORMANCE;

    public ClickGuiScreen() {
        super(Text.literal("Catlean Client"));
    }

    private static Text label(Module m) {
        return Text.literal(m.name + "  " + (m.isEnabled() ? "\u00a7a[ON]" : "\u00a7c[OFF]"));
    }

    @Override
    protected void init() {
        Category[] cats = Category.values();
        int tabW = 96, gap = 4;
        int total = cats.length * tabW + (cats.length - 1) * gap;
        int x = (this.width - total) / 2;
        for (Category c : cats) {
            String t = (c == selected ? "\u00a7d> " : "") + c.label;
            addDrawableChild(ButtonWidget.builder(Text.literal(t), b -> {
                selected = c;
                this.clearAndInit();
            }).dimensions(x, 34, tabW, 20).build());
            x += tabW + gap;
        }

        int y = 64;
        int mx = this.width / 2 - 100;
        for (Module m : ModuleManager.of(selected)) {
            addDrawableChild(ButtonWidget.builder(label(m), b -> {
                m.toggle();
                b.setMessage(label(m));
                ModuleManager.save();
            }).dimensions(mx, y, 200, 20).build());
            y += 24;
        }
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        super.render(ctx, mouseX, mouseY, delta);
        ctx.drawCenteredTextWithShadow(this.textRenderer, "Catlean Client", this.width / 2, 14, 0xFFB57BFF);
    }

    @Override
    public boolean shouldPause() { return false; }
}
