package com.catlean;

public enum Category {
    PERFORMANCE("Performance"),
    HUD("HUD / PvP"),
    UTILITY("Utility");

    public final String label;
    Category(String label) { this.label = label; }
}
