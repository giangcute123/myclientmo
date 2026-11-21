package com.catlean;

import java.util.ArrayDeque;
import net.minecraft.client.MinecraftClient;

public final class Cps {
    private static final ArrayDeque<Long> LEFT = new ArrayDeque<>();
    private static final ArrayDeque<Long> RIGHT = new ArrayDeque<>();
    private static boolean lastL, lastR;

    private Cps() {}

    public static void tick(MinecraftClient mc) {
        boolean l = mc.options.attackKey.isPressed();
        boolean r = mc.options.useKey.isPressed();
        long now = System.currentTimeMillis();
        if (l && !lastL) LEFT.add(now);
        if (r && !lastR) RIGHT.add(now);
        lastL = l;
        lastR = r;
        prune(LEFT, now);
        prune(RIGHT, now);
    }

    private static void prune(ArrayDeque<Long> q, long now) {
        while (!q.isEmpty() && now - q.peekFirst() > 1000) q.pollFirst();
    }

    public static int left() { return LEFT.size(); }
    public static int right() { return RIGHT.size(); }
}
