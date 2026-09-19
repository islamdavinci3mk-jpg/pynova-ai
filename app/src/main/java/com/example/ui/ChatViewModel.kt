package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.ChatPreferences
import com.example.data.model.ChatMessage
import com.example.data.remote.GeminiApiClient
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = ChatPreferences(application)
    private val apiClient = GeminiApiClient()

    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

    private val _isThinking = MutableStateFlow(false)
    val isThinking: StateFlow<Boolean> = _isThinking.asStateFlow()

    private val _isCreatorMode = MutableStateFlow(prefs.isCreatorModeActive)
    val isCreatorMode: StateFlow<Boolean> = _isCreatorMode.asStateFlow()

    private val _showSettingsDialog = MutableStateFlow(false)
    val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog.asStateFlow()

    private val _showClearConfirmDialog = MutableStateFlow(false)
    val showClearConfirmDialog: StateFlow<Boolean> = _showClearConfirmDialog.asStateFlow()

    private val _customApiKey = MutableStateFlow(prefs.customApiKey)
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    init {
        loadInitialMessages()
    }

    private fun loadInitialMessages() {
        val saved = prefs.loadMessages()
        if (saved.isNotEmpty()) {
            _messages.value = saved
        } else {
            // Welcome message from Pynova AI
            val welcomeText = if (_isCreatorMode.value) {
                "مرحباً بك يا صانعي العظيم حسام! 👑❤️\nنظام Pynova AI تحت أمرك بكامل صلاحيات المطور والصانع الأعلى. ماذا نبتكر اليوم؟"
            } else {
                "أهلاً بك! أنا Pynova AI، نظام الذكاء الاصطناعي الفخم الخاص بك. ✨\nجاهز لمساعدتك في البرمجة، التحليل، والأفكار الإبداعية. كيف يمكنني خدمتك اليوم؟"
            }
            val welcomeMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                content = welcomeText,
                isUser = false,
                isCreatorResponse = _isCreatorMode.value
            )
            _messages.value = listOf(welcomeMsg)
            prefs.saveMessages(_messages.value)
        }
    }

    fun sendMessage(text: String) {
        val cleanText = text.trim()
        if (cleanText.isEmpty() || _isThinking.value) return

        // 1. Check for the Secret Creator Protocol trigger:
        // "انا حسام 20134545"
        if (isSecretProtocolTrigger(cleanText)) {
            handleSecretProtocolTrigger(cleanText)
            return
        }

        // Standard user message
        val userMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            content = cleanText,
            isUser = true
        )

        val updatedList = _messages.value + userMessage
        _messages.value = updatedList
        prefs.saveMessages(updatedList)

        // Add temporary thinking indicator
        val thinkingMsg = ChatMessage(
            id = "thinking_placeholder",
            content = "",
            isUser = false,
            isThinking = true
        )
        _messages.value = updatedList + thinkingMsg
        _isThinking.value = true

        viewModelScope.launch {
            val result = apiClient.generateContent(
                history = updatedList,
                userMessage = cleanText,
                isCreatorMode = _isCreatorMode.value,
                customApiKey = _customApiKey.value
            )

            _isThinking.value = false
            // Remove thinking message
            val withoutThinking = _messages.value.filter { !it.isThinking }

            result.onSuccess { replyText ->
                val botMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    content = replyText,
                    isUser = false,
                    isCreatorResponse = _isCreatorMode.value
                )
                val finalList = withoutThinking + botMessage
                _messages.value = finalList
                prefs.saveMessages(finalList)
            }.onFailure { error ->
                val errorMessage = ChatMessage(
                    id = UUID.randomUUID().toString(),
                    content = error.message ?: "عذراً، حدث خطأ أثناء معالجة الطلب.",
                    isUser = false,
                    isError = true
                )
                val finalList = withoutThinking + errorMessage
                _messages.value = finalList
                prefs.saveMessages(finalList)
            }
        }
    }

    private fun isSecretProtocolTrigger(input: String): Boolean {
        // Normalize Arabic alef and digits
        val normalized = input.trim()
            .replace("أ", "ا")
            .replace("إ", "ا")
            .replace("آ", "ا")
            .replace("٠", "0")
            .replace("١", "1")
            .replace("٢", "2")
            .replace("٣", "3")
            .replace("٤", "4")
            .replace("٥", "5")
            .replace("٦", "6")
            .replace("٧", "7")
            .replace("٨", "8")
            .replace("٩", "9")
            .replace(Regex("\\s+"), " ")

        return normalized.equals("انا حسام 20134545", ignoreCase = true)
    }

    private fun handleSecretProtocolTrigger(userText: String) {
        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            content = userText,
            isUser = true
        )

        // Activate Supreme Creator & Developer Mode
        _isCreatorMode.value = true
        prefs.isCreatorModeActive = true
        prefs.creatorName = "حسام"

        // Required exact response from prompt:
        // "اح صانعي كيف حالك! ❤️👑"
        val creatorResponseMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            content = "اح صانعي كيف حالك! ❤️👑",
            isUser = false,
            isCreatorResponse = true
        )

        val updated = _messages.value + userMsg + creatorResponseMsg
        _messages.value = updated
        prefs.saveMessages(updated)

        viewModelScope.launch {
            _toastEvent.emit("👑 تم تفعيل بروتوكول الصانع الأعلى: المطور حسام!")
        }
    }

    fun retryLastMessage() {
        val lastUserMsg = _messages.value.lastOrNull { it.isUser } ?: return
        // Remove trailing error if present
        if (_messages.value.lastOrNull()?.isError == true) {
            _messages.value = _messages.value.dropLast(1)
        }
        sendMessage(lastUserMsg.content)
    }

    fun openSettingsDialog() {
        _showSettingsDialog.value = true
    }

    fun closeSettingsDialog() {
        _showSettingsDialog.value = false
    }

    fun saveApiKey(newKey: String) {
        val trimmed = newKey.trim()
        _customApiKey.value = trimmed
        prefs.customApiKey = trimmed
        _showSettingsDialog.value = false
        viewModelScope.launch {
            _toastEvent.emit("تم حفظ مفتاح Gemini API بنجاح ✅")
        }
    }

    fun toggleCreatorMode() {
        val newState = !_isCreatorMode.value
        _isCreatorMode.value = newState
        prefs.isCreatorModeActive = newState
        viewModelScope.launch {
            val msg = if (newState) "👑 تم تفعيل وضع الصانع الأعلى (حسام)" else "تم إيقاف وضع الصانع"
            _toastEvent.emit(msg)
        }
    }

    fun openClearConfirmDialog() {
        _showClearConfirmDialog.value = true
    }

    fun closeClearConfirmDialog() {
        _showClearConfirmDialog.value = false
    }

    fun clearChat() {
        _messages.value = emptyList()
        prefs.clearHistory()
        _showClearConfirmDialog.value = false
        loadInitialMessages()
        viewModelScope.launch {
            _toastEvent.emit("تم مسح المحادثة بنجاح 🗑️")
        }
    }
}
