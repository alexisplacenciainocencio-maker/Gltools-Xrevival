package com.nightmare.gltools;

import androidx.annotation.NonNull;
import com.nightmare.gltools.spoof.SpoofMode;
import org.json.JSONObject;

public class Profile {

    private String name;
    private SpoofMode spoofMode;
    private int ramMb;
    private int resolutionPercent;
    private int msaaLevel;
    private int textureQuality;
    private int anisoLevel;
    private String eglValue;
    private String vulkanValue;
    private String deviceModel;
    private String brand;
    private String manufacturer;
    private String board;
    private String hardware;
    private int sdkValue;

    public Profile() {}

    public static Profile getDefault() {
        Profile p = new Profile();
        p.name = "Predeterminado";
        p.spoofMode = SpoofMode.NONE;
        p.ramMb = 0;
        p.resolutionPercent = 100;
        p.msaaLevel = 0;
        p.textureQuality = 100;
        p.anisoLevel = 0;
        p.eglValue = "";
        p.vulkanValue = "";
        p.deviceModel = "";
        p.brand = "";
        p.manufacturer = "";
        p.board = "";
        p.hardware = "";
        p.sdkValue = 0;
        return p;
    }

    // Getters
    public String getName() { return name; }
    public SpoofMode getSpoofMode() { return spoofMode; }
    public int getRamMb() { return ramMb; }
    public int getResolutionPercent() { return resolutionPercent; }
    public int getMsaaLevel() { return msaaLevel; }
    public int getTextureQuality() { return textureQuality; }
    public int getAnisoLevel() { return anisoLevel; }
    public String getEglValue() { return eglValue; }
    public String getVulkanValue() { return vulkanValue; }
    public String getDeviceModel() { return deviceModel; }
    public String getBrand() { return brand; }
    public String getManufacturer() { return manufacturer; }
    public String getBoard() { return board; }
    public String getHardware() { return hardware; }
    public int getSdkValue() { return sdkValue; }
    public String getGpuRenderer() { return spoofMode != null ? spoofMode.getDisplayName() : ""; }
    public String getGpuVendor() { return spoofMode != null ? "GLTools2026" : ""; }

    // Setters
    public void setName(String n) { this.name = n; }
    public void setSpoofMode(SpoofMode s) { this.spoofMode = s; }
    public void setRamMb(int r) { this.ramMb = r; }
    public void setResolutionPercent(int r) { this.resolutionPercent = r; }
    public void setMsaaLevel(int m) { this.msaaLevel = m; }
    public void setTextureQuality(int t) { this.textureQuality = t; }
    public void setAnisoLevel(int a) { this.anisoLevel = a; }
    public void setEglValue(String e) { this.eglValue = e; }
    public void setVulkanValue(String v) { this.vulkanValue = v; }
    public void setDeviceModel(String d) { this.deviceModel = d; }
    public void setBrand(String b) { this.brand = b; }
    public void setManufacturer(String m) { this.manufacturer = m; }
    public void setBoard(String b) { this.board = b; }
    public void setHardware(String h) { this.hardware = h; }
    public void setSdkValue(int s) { this.sdkValue = s; }

    public JSONObject toJson() {
        JSONObject j = new JSONObject();
        try {
            j.put("name", name);
            j.put("spoofMode", spoofMode != null ? spoofMode.name() : "NONE");
            j.put("ramMb", ramMb);
            j.put("resolutionPercent", resolutionPercent);
            j.put("msaaLevel", msaaLevel);
            j.put("textureQuality", textureQuality);
            j.put("anisoLevel", anisoLevel);
            j.put("eglValue", eglValue);
            j.put("vulkanValue", vulkanValue);
            j.put("deviceModel", deviceModel);
            j.put("brand", brand);
            j.put("manufacturer", manufacturer);
            j.put("board", board);
            j.put("hardware", hardware);
            j.put("sdkValue", sdkValue);
        } catch (Exception e) {}
        return j;
    }

    public static Profile fromJson(JSONObject j) {
        Profile p = new Profile();
        try {
            p.name = j.optString("name", "Sin nombre");
            p.spoofMode = SpoofMode.valueOf(j.optString("spoofMode", "NONE"));
            p.ramMb = j.optInt("ramMb", 0);
            p.resolutionPercent = j.optInt("resolutionPercent", 100);
            p.msaaLevel = j.optInt("msaaLevel", 0);
            p.textureQuality = j.optInt("textureQuality", 100);
            p.anisoLevel = j.optInt("anisoLevel", 0);
            p.eglValue = j.optString("eglValue", "");
            p.vulkanValue = j.optString("vulkanValue", "");
            p.deviceModel = j.optString("deviceModel", "");
            p.brand = j.optString("brand", "");
            p.manufacturer = j.optString("manufacturer", "");
            p.board = j.optString("board", "");
            p.hardware = j.optString("hardware", "");
            p.sdkValue = j.optInt("sdkValue", 0);
        } catch (Exception e) {}
        return p;
    }

    @NonNull
    @Override
    public String toString() {
        return name + " [" + (spoofMode != null ? spoofMode.getDisplayName() : "N/A") + "]";
    }
}
