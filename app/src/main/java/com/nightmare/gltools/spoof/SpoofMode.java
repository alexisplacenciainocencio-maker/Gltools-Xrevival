package com.nightmare.gltools.spoof;

public enum SpoofMode {
    NONE("Sin spoofing", 0),
    ADRENO_640("Adreno 640 (Snapdragon 855)", 640),
    ADRENO_650("Adreno 650 (Snapdragon 865)", 650),
    ADRENO_660("Adreno 660 (Snapdragon 888)", 660),
    ADRENO_730("Adreno 730 (Snapdragon 8 Gen 1)", 730),
    MALI_G78("Mali-G78 (Exynos 2100)", 780),
    MALI_G710("Mali-G710 (Dimensity 9000)", 710),
    SHIZUKU("Shizuku Bridge (rootless)", -1);  // P3: modo Shizuku añadido

    private final String displayName;
    private final int gpuId;

    SpoofMode(String displayName, int gpuId) {
        this.displayName = displayName;
        this.gpuId = gpuId;
    }

    public String getDisplayName() { return displayName; }
    public int getGpuId() { return gpuId; }
    public boolean isShizuku() { return this == SHIZUKU; }
}
