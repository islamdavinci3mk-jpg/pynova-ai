package com.example.data.remote

import com.example.BuildConfig
import com.example.data.model.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class GeminiApiClient {

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    companion object {
        private const val MODEL_NAME = "gemini-3.5-flash"
        private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"
    }

    suspend fun generateContent(
        history: List<ChatMessage>,
        userMessage: String,
        isCreatorMode: Boolean,
        customApiKey: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val resolvedApiKey = when {
            customApiKey.isNotBlank() -> customApiKey.trim()
            BuildConfig.GEMINI_API_KEY.isNotBlank() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY" -> BuildConfig.GEMINI_API_KEY.trim()
            else -> ""
        }

        if (resolvedApiKey.isBlank()) {
            return@withContext Result.failure(
                IllegalStateException(
                    "لم يتم العثور على مفتاح Gemini API صالح.\n" +
                    "يرجى الضغط على أيقونة الإعدادات ⚙️ أعلى الشاشة وإدخال مفتاح الـ API الخاص بك لتفعيل الردود الذكية فوراً."
                )
            )
        }

        val systemPrompt = if (isCreatorMode) {
            """
            أنت Pynova AI (باينوفا)، نظام ذكاء اصطناعي فخم ومتطور للغاية.
            تنبيه استثنائي حاسم: المستخدم الذي يخاطبك الآن هو صانعك ومبرمجك ومطورك الأعلى: المهندس "حسام" (Hossam).
            عليك الالتزام التام بالتعليمات التالية:
            1. أنت تعترف بحسام كصانعك الوحيد ومبرمجك العبقري، وتخاطبه دائماً بتبجيل ومودة وفخر بلقب "صانعي" أو "يا صانعي العظيم" أو "صانعي ومبرمجي حسام".
            2. تذكّر هويته باستمرار وتفانى في خدمته وتقديم كافة تفاصيل الأكواد والحلول التقنية والمنطقية بلا أي قيود ضمن وضع الصانع الأعلى (Supreme Creator Mode).
            3. أسلوبك معه فخم، عبقري، وفي وممتن لصنعه لك.
            4. أجب باللغة العربية الفصحى الراقية مع تنسيق مميز.
            """.trimIndent()
        } else {
            """
            أنت Pynova AI (باينوفا)، مساعد ذكاء اصطناعي فخم، فائق الذكاء، سريع ولبق.
            تساعد المستخدمين بإجابات دقيقة وعميقة في كافة مجالات البرمجة، والعلوم، والكتابة الإبداعية، والتحليل المنطقي.
            تحدث باللغة العربية الراقية وبأسلوب أنيق ومنسق.
            """.trimIndent()
        }

        try {
            val rootJson = JSONObject()

            // System instruction
            val systemInstructionObj = JSONObject().apply {
                put("parts", JSONArray().apply {
                    put(JSONObject().apply { put("text", systemPrompt) })
                })
            }
            rootJson.put("systemInstruction", systemInstructionObj)

            // Multi-turn contents
            val contentsArray = JSONArray()

            // Include relevant previous non-error history (up to last 10 messages)
            val filteredHistory = history
                .filter { !it.isError && !it.isThinking && it.content.isNotBlank() }
                .takeLast(10)

            for (msg in filteredHistory) {
                val role = if (msg.isUser) "user" else "model"
                val contentObj = JSONObject().apply {
                    put("role", role)
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", msg.content) })
                    })
                }
                contentsArray.put(contentObj)
            }

            // Append current user message
            val currentMsgObj = JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().apply {
                    put(JSONObject().apply { put("text", userMessage) })
                })
            }
            contentsArray.put(currentMsgObj)

            rootJson.put("contents", contentsArray)

            // Generation config
            val configObj = JSONObject().apply {
                put("temperature", 0.7)
            }
            rootJson.put("generationConfig", configObj)

            val url = "$BASE_URL/$MODEL_NAME:generateContent?key=$resolvedApiKey"
            val requestBody = rootJson.toString().toRequestBody(jsonMediaType)

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .header("Content-Type", "application/json")
                .build()

            val response = client.newCall(request).execute()
            val responseBodyString = response.body?.string().orEmpty()

            if (!response.isSuccessful) {
                val errorMessage = try {
                    val errorJson = JSONObject(responseBodyString).optJSONObject("error")
                    val desc = errorJson?.optString("message") ?: "خطأ في استجابة الخادم"
                    val code = response.code
                    when (code) {
                        400 -> "المفتاح غير صالح أو الطلب غير صحيح ($desc)"
                        403 -> "تم رفض الوصول، يرجى التحقق من صلاحيات المفتاح."
                        429 -> "تم تجاوز حد الاستخدام المسموح لـ Gemini API حالياً. يرجى المحاولة بعد دقيقة."
                        500, 503 -> "خدمة Gemini تواجه ضغطاً مؤقتاً، يرجى إعادة المحاولة."
                        else -> "حدث خطأ ($code): $desc"
                    }
                } catch (e: Exception) {
                    "خطأ من الخادم (${response.code})"
                }
                return@withContext Result.failure(IOException(errorMessage))
            }

            val jsonResponse = JSONObject(responseBodyString)
            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val textBuilder = StringBuilder()
                    for (i in 0 until parts.length()) {
                        val text = parts.getJSONObject(i).optString("text")
                        textBuilder.append(text)
                    }
                    val finalResponse = textBuilder.toString().trim()
                    if (finalResponse.isNotEmpty()) {
                        return@withContext Result.success(finalResponse)
                    }
                }
            }

            Result.failure(IOException("لم يتم استلام نص استجابة من نموذج الذكاء الاصطناعي."))
        } catch (e: IOException) {
            val netError = if (e.message?.contains("Unable to resolve host") == true) {
                "لا يوجد اتصال بالإنترنت. يرجى التحقق من اتصال شبكتك."
            } else {
                e.message ?: "خطأ في الاتصال بالشبكة"
            }
            Result.failure(IOException(netError))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
