package com.nightmare.gltools;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.nightmare.gltools.spoof.SpoofMode;
import com.nightmare.gltools.utils.GLLog;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ProfileManager {

    private static final String PREFS_NAME = "gltools_profiles";
    private static final String KEY_PROFILES = "profiles";
    private static final String KEY_ACTIVE = "active_profile";
    private static SharedPreferences prefs;
    private static Profile activeProfile;

    public static void init(@NonNull Context ctx) {
        if (prefs == null) {
            prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            loadActive();
        }
    }

    @Nullable
    public static Profile getActive() {
        return activeProfile;
    }

    public static void setActive(@Nullable Profile p) {
        activeProfile = p;
        if (prefs != null && p != null) {
            prefs.edit().putString(KEY_ACTIVE, p.toJson().toString()).apply();
        }
    }

    public static List<Profile> listProfiles() {
        List<Profile> list = new ArrayList<>();
        if (prefs == null) return list;
        String raw = prefs.getString(KEY_PROFILES, "[]");
        try {
            JSONArray arr = new JSONArray(raw);
            for (int i = 0; i < arr.length(); i++) {
                list.add(Profile.fromJson(arr.getJSONObject(i)));
            }
        } catch (Exception e) {
            GLLog.e("ProfileManager", "Error parseando perfiles", e);
        }
        return list;
    }

    public static void saveProfile(@NonNull Profile p) {
        if (prefs == null) return;
        List<Profile> list = listProfiles();
        // Reemplazar si existe
        boolean found = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getName().equals(p.getName())) {
                list.set(i, p);
                found = true;
                break;
            }
        }
        if (!found) list.add(p);
        JSONArray arr = new JSONArray();
        for (Profile pr : list) arr.put(pr.toJson());
        prefs.edit().putString(KEY_PROFILES, arr.toString()).apply();
    }

    public static void deleteProfile(String name) {
        if (prefs == null) return;
        List<Profile> list = listProfiles();
        list.removeIf(p -> p.getName().equals(name));
        JSONArray arr = new JSONArray();
        for (Profile pr : list) arr.put(pr.toJson());
        prefs.edit().putString(KEY_PROFILES, arr.toString()).apply();
    }

    private static void loadActive() {
        if (prefs == null) return;
        String raw = prefs.getString(KEY_ACTIVE, null);
        if (raw != null) {
            try {
                activeProfile = Profile.fromJson(new JSONObject(raw));
            } catch (Exception e) {
                GLLog.e("ProfileManager", "Error cargando perfil activo", e);
            }
        }
        if (activeProfile == null) {
            activeProfile = Profile.getDefault();
        }
    }
}
