package com.gl.revival

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class ProfileManager(ctx: Context?) {
    private val prefs: SharedPreferences? = ctx?.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    val activeProfile: Profile?
        get() {
            if (prefs == null) return null
            val id = prefs.getString(KEY_ACTIVE, null) ?: run {
                val def = createDefault().apply { name = "Default" }
                saveProfile(def)
                setActive(def.id)
                return def
            }
            return getProfileById(id)
        }

    val activeProfileId: String?
        get() = prefs?.getString(KEY_ACTIVE, null)

    fun setActive(id: String) {
        prefs?.edit()?.putString(KEY_ACTIVE, id)?.apply()
    }

    fun createDefault(): Profile = Profile(id = UUID.randomUUID().toString())

    fun saveProfile(p: Profile) {
        if (prefs == null) return
        val list = listProfiles().toMutableList()
        val idx = list.indexOfFirst { it.id == p.id }
        if (idx >= 0) list[idx] = p else list.add(p)
        prefs.edit().putString(KEY_LIST, toJson(list).toString()).apply()
    }

    fun deleteProfile(id: String) {
        if (prefs == null) return
        val list = listProfiles().filterNot { it.id == id }
        prefs.edit().putString(KEY_LIST, toJson(list).toString()).apply()
        if (id == activeProfileId) prefs.edit().remove(KEY_ACTIVE).apply()
    }

    fun listProfiles(): List<Profile> {
        if (prefs == null) return emptyList()
        val raw = prefs.getString(KEY_LIST, "[]") ?: "[]"
        return fromJson(raw)
    }

    fun getProfileById(id: String): Profile? = listProfiles().find { it.id == id }

    private fun toJson(list: List<Profile>): JSONArray = JSONArray().apply {
        list.forEach { put(it.toJson()) }
    }

    private fun fromJson(raw: String): List<Profile> =
        try {
            val arr = JSONArray(raw)
            (0 until arr.length()).map { Profile.fromJson(arr.getJSONObject(it)) }
        } catch (e: Exception) {
            GLLog.w("ProfileManager", "Parse error: ${e.message}")
            emptyList()
        }

    companion object {
        private const val PREFS = "glrevival_profiles"
        private const val KEY_ACTIVE = "active_profile_id"
        private const val KEY_LIST = "profile_list"
    }
}
