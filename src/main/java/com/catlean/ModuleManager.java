package com.catlean;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public final class ModuleManager {
    public static final List<Module> ALL = new ArrayList<>();

    private ModuleManager() {}

    public static void init() {
        ALL.add(new Modules.FpsBoost());
        ALL.add(new Modules.FpsHud());
        ALL.add(new Modules.CpsHud());
        ALL.add(new Modules.CoordsHud());
        ALL.add(new Modules.PingHud());
        ALL.add(new Modules.Keystrokes());
        ALL.add(new Modules.Fullbright());
        ALL.add(new Modules.ToggleSprint());
        ALL.add(new Modules.Zoom());
        load();
    }

    public static List<Module> of(Category c) {
        List<Module> out = new ArrayList<>();
        for (Module m : ALL) if (m.category == c) out.add(m);
        return out;
    }

    public static void tick(MinecraftClient mc) {
        Cps.tick(mc);
        for (Module m : ALL) if (m.isEnabled()) m.onTick(mc);
    }

    public static void renderHud(DrawContext ctx, MinecraftClient mc) {
        if (mc.options.hudHidden || mc.player == null) return;
        int y = 6;
        for (Module m : ALL) {
            if (!m.isEnabled()) continue;
            String t = m.hudText(mc);
            if (t != null) {
                int w = mc.textRenderer.getWidth(t);
                ctx.fill(4, y - 2, 4 + w + 4, y + 10, 0x66000000);
                ctx.drawTextWithShadow(mc.textRenderer, t, 6, y, 0xFFE0C8FF);
                y += 13;
            }
            m.renderHud(ctx, mc);
        }
    }

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("catlean.properties");
    }

    public static void save() {
        Properties p = new Properties();
        for (Module m : ALL) p.setProperty(m.name, Boolean.toString(m.isEnabled()));
        try (OutputStream o = Files.newOutputStream(file())) {
            p.store(o, "Catlean Client");
        } catch (IOException ignored) {}
    }

    private static void load() {
        Path f = file();
        if (!Files.exists(f)) return;
        Properties p = new Properties();
        try (InputStream i = Files.newInputStream(f)) {
            p.load(i);
        } catch (IOException e) { return; }
        for (Module m : ALL) {
            String v = p.getProperty(m.name);
            if (v != null) m.setEnabled(Boolean.parseBoolean(v));
        }
    }
}
