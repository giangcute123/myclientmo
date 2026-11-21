package com.catlean;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public abstract class Module {
    public final String name;
    public final Category category;
    private boolean enabled;

    protected Module(String name, Category category, boolean defaultOn) {
        this.name = name;
        this.category = category;
        this.enabled = defaultOn;
    }

    public boolean isEnabled() { return enabled; }

    public void setEnabled(boolean on) {
        if (on == enabled) return;
        enabled = on;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (on) onEnable(mc); else onDisable(mc);
    }

    public void toggle() { setEnabled(!enabled); }

    protected void onEnable(MinecraftClient mc) {}
    protected void onDisable(MinecraftClient mc) {}
    public void onTick(MinecraftClient mc) {}

    public String hudText(MinecraftClient mc) { return null; }

    public void renderHud(DrawContext ctx, MinecraftClient mc) {}
}
