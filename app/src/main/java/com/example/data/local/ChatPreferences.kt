package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.ChatMessage
import org.json.JSONArray
import org.json.JSONObject

class ChatPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("pynova_ai_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_CREATOR_MODE = "is_creator_mode_active"
        private const val KEY_CREATOR_NAME = "creator_name"
        private const val KEY_CUSTOM_API_KEY = "custom_api_key"
        private const val KEY_SAVED_MESSAGES = "saved_messages"
    }

    var isCreatorModeActive: Boolean
        get() = prefs.getBoolean(KEY_CREATOR_MODE, false)
        set(value) = prefs.edit().putBoolean(KEY_CREATOR_MODE, value).apply()

    var creatorName: String
        get() = prefs.getString(KEY_CREATOR_NAME, "حسام") ?: "حسام"
        set(value) = prefs.edit().putString(KEY_CREATOR_NAME, value).apply()

    var customApiKey: String
        get() = prefs.getString(KEY_CUSTOM_API_KEY, "") ?: ""
        set(value) = prefs.edit().putString(KEY_CUSTOM_API_KEY, value).apply()

    fun saveMessages(messages: List<ChatMessage>) {
        val jsonArray = JSONArray()
        // Save at most the latest 50 messages to keep persistence snappy
        val sublist = messages.takeLast(50)
        for (msg in sublist) {
            if (msg.isThinking) continue // Don't persist temporary thinking messages
            val obj = JSONObject().apply {
                put("id", msg.id)
                put("content", msg.content)
                put("isUser", msg.isUser)
                put("timestamp", msg.timestamp)
                put("isCreatorResponse", msg.isCreatorResponse)
                put("isError", msg.isError)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_SAVED_MESSAGES, jsonArray.toString()).apply()
    }

    fun loadMessages(): List<ChatMessage> {
        val raw = prefs.getString(KEY_SAVED_MESSAGES, null) ?: return emptyList()
        val result = mutableListOf<ChatMessage>()
        try {
            val jsonArray = JSONArray(raw)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                result.add(
                    ChatMessage(
                        id = obj.optString("id"),
                        content = obj.optString("content"),
                        isUser = obj.optBoolean("isUser"),
                        timestamp = obj.optLong("timestamp"),
                        isCreatorResponse = obj.optBoolean("isCreatorResponse"),
                        isError = obj.optBoolean("isError"),
                        isThinking = false
                    )
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    fun clearHistory() {
        prefs.edit().remove(KEY_SAVED_MESSAGES).apply()
    }
}
