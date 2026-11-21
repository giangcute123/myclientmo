package com.catlean;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public final class Modules {
    private Modules() {}

    // ---------------- PERFORMANCE ----------------
    public static class FpsBoost extends Module {
        private boolean bShadows, bVsync;
        private int bMaxFps, bBlend, bView;
        private boolean saved;

        public FpsBoost() { super("FPS Boost", Category.PERFORMANCE, false); }

        @Override protected void onEnable(MinecraftClient mc) {
            var o = mc.options;
            bShadows = o.getEntityShadows().getValue();
            bVsync = o.getEnableVsync().getValue();
            bMaxFps = o.getMaxFps().getValue();
            bBlend = o.getBiomeBlendRadius().getValue();
            bView = o.getViewDistance().getValue();
            saved = true;
            o.getEntityShadows().setValue(false);
            o.getEnableVsync().setValue(false);
            o.getMaxFps().setValue(260);
            o.getBiomeBlendRadius().setValue(0);
            o.getViewDistance().setValue(Math.min(bView, 8));
        }

        @Override protected void onDisable(MinecraftClient mc) {
            if (!saved) return;
            var o = mc.options;
            o.getEntityShadows().setValue(bShadows);
            o.getEnableVsync().setValue(bVsync);
            o.getMaxFps().setValue(bMaxFps);
            o.getBiomeBlendRadius().setValue(bBlend);
            o.getViewDistance().setValue(bView);
        }
    }

    // ---------------- HUD ----------------
    public static class FpsHud extends Module {
        public FpsHud() { super("FPS", Category.HUD, true); }
        @Override public String hudText(MinecraftClient mc) { return "FPS: " + mc.getCurrentFps(); }
    }

    public static class CpsHud extends Module {
        public CpsHud() { super("CPS", Category.HUD, true); }
        @Override public String hudText(MinecraftClient mc) { return "CPS: " + Cps.left() + " | " + Cps.right(); }
    }

    public static class CoordsHud extends Module {
        public CoordsHud() { super("Coordinates", Category.HUD, true); }
        @Override public String hudText(MinecraftClient mc) {
            if (mc.player == null) return null;
            return String.format("XYZ: %.0f %.0f %.0f", mc.player.getX(), mc.player.getY(), mc.player.getZ());
        }
    }

    public static class PingHud extends Module {
        public PingHud() { super("Ping", Category.HUD, false); }
        @Override public String hudText(MinecraftClient mc) {
            if (mc.player == null || mc.getNetworkHandler() == null) return null;
            PlayerListEntry e = mc.getNetworkHandler().getPlayerListEntry(mc.player.getUuid());
            return e == null ? null : "Ping: " + e.getLatency() + " ms";
        }
    }

    public static class Keystrokes extends Module {
        public Keystrokes() { super("Keystrokes", Category.HUD, true); }

        private void key(DrawContext c, MinecraftClient mc, String s, boolean down, int x, int y, int w, int h) {
            c.fill(x, y, x + w, y + h, down ? 0xCC9B5BFF : 0x88000000);
            c.drawCenteredTextWithShadow(mc.textRenderer, s, x + w / 2, y + (h - 8) / 2, 0xFFFFFFFF);
        }

        @Override public void renderHud(DrawContext c, MinecraftClient mc) {
            var o = mc.options;
            int x = 8, y = mc.getWindow().getScaledHeight() / 2 - 20;
            key(c, mc, "W", o.forwardKey.isPressed(), x + 24, y, 22, 22);
            key(c, mc, "A", o.leftKey.isPressed(), x, y + 24, 22, 22);
            key(c, mc, "S", o.backKey.isPressed(), x + 24, y + 24, 22, 22);
            key(c, mc, "D", o.rightKey.isPressed(), x + 48, y + 24, 22, 22);
            key(c, mc, "LMB " + Cps.left(), o.attackKey.isPressed(), x, y + 48, 34, 18);
            key(c, mc, "RMB " + Cps.right(), o.useKey.isPressed(), x + 36, y + 48, 34, 18);
            key(c, mc, "SPACE", o.jumpKey.isPressed(), x, y + 68, 70, 14);
        }
    }

    // ---------------- UTILITY ----------------
    public static class Fullbright extends Module {
        public Fullbright() { super("Fullbright", Category.UTILITY, false); }

        @Override public void onTick(MinecraftClient mc) {
            ClientPlayerEntity p = mc.player;
            if (p == null) return;
            StatusEffectInstance cur = p.getStatusEffect(StatusEffects.NIGHT_VISION);
            if (cur == null || cur.getDuration() < 300) {
                p.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 1200, 0, false, false, false));
            }
        }

        @Override protected void onDisable(MinecraftClient mc) {
            if (mc.player != null) mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
        }
    }

    public static class ToggleSprint extends Module {
        public ToggleSprint() { super("Toggle Sprint", Category.UTILITY, false); }

        @Override public void onTick(MinecraftClient mc) {
            ClientPlayerEntity p = mc.player;
            if (p == null) return;
            if (mc.options.forwardKey.isPressed() && !p.isSneaking() && !p.horizontalCollision
                    && p.getHungerManager().getFoodLevel() > 6) {
                p.setSprinting(true);
            }
        }
    }

    public static class Zoom extends Module {
        private int oldFov = -1;

        public Zoom() { super("Zoom", Category.UTILITY, true); }

        @Override public void onTick(MinecraftClient mc) {
            boolean held = CatleanClient.zoomKey != null && CatleanClient.zoomKey.isPressed() && mc.currentScreen == null;
            var fov = mc.options.getFov();
            if (held) {
                if (oldFov < 0) oldFov = fov.getValue();
                fov.setValue(30);
            } else if (oldFov >= 0) {
                fov.setValue(oldFov);
                oldFov = -1;
            }
        }

        @Override protected void onDisable(MinecraftClient mc) {
            if (oldFov >= 0) { mc.options.getFov().setValue(oldFov); oldFov = -1; }
        }
    }
                }
