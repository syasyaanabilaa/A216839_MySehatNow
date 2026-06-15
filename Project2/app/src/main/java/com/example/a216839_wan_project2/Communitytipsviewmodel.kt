package com.example.a216839_wan_project2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.a216839_wan_project2.data.firebase.FirestoreTipsRepository
import com.example.a216839_wan_project2.data.firebase.SavedTip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class GeneratedTip(
    val tip     : String,
    val category: String
)

// ── Groq API config ───────────────────────────────────────────────────────────
// 1. Sign up free at https://console.groq.com
// 2. Go to API Keys → Create API Key
// 3. Paste your key below — replace YOUR_GROQ_API_KEY
private const val GROQ_API_KEY = "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"
private const val GROQ_URL     = "https://api.groq.com/openai/v1/chat/completions"
private const val GROQ_MODEL = "llama-3.3-70b-versatile"

private val httpClient = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(60, TimeUnit.SECONDS)
    .writeTimeout(30, TimeUnit.SECONDS)
    .build()

// ── Detect health category from query ────────────────────────────────────────
private fun detectCategory(query: String): String {
    val q = query.lowercase()
    return when {
        listOf("sleep","tidur","insomnia","rest","rehat","nap","night","tired","penat","fatigue")
            .any { q.contains(it) } -> "Sleep"
        listOf("eat","makan","food","diet","weight","water","nutrition","vitamin","protein",
            "sugar","calori","carb","fruit","buah","sayur","rice","nasi","meal")
            .any { q.contains(it) } -> "Nutrition"
        listOf("exercise","senaman","workout","gym","run","lari","walk","jalan","swim",
            "yoga","cardio","strength","muscle","sport","sukan","fit","active")
            .any { q.contains(it) } -> "Exercise"
        listOf("stress","anxiety","mental","mood","sad","depress","mindful","meditate",
            "relax","calm","focus","burnout","lonely","emotion","tekanan")
            .any { q.contains(it) } -> "Mental Health"
        listOf("hygiene","wash","clean","teeth","brush","hand","shower","mandi",
            "skin","acne","germ","nail","bacteria","kebersihan")
            .any { q.contains(it) } -> "Hygiene"
        else -> "General"
    }
}

// ── Groq API call ─────────────────────────────────────────────────────────────
private suspend fun fetchTipsFromGroq(query: String): List<GeneratedTip> =
    withContext(Dispatchers.IO) {

        val category = detectCategory(query)

        // System prompt tells the model exactly what format to return
        val systemPrompt = """
            You are a certified health advisor. When given a health topic, you always respond
            with ONLY a valid JSON array of exactly 5 health tips. No extra text, no markdown,
            no code fences, no explanation — pure JSON only.
            
            Format:
            [
              {"tip": "Tip text here.", "category": "CATEGORY"},
              {"tip": "Tip text here.", "category": "CATEGORY"},
              {"tip": "Tip text here.", "category": "CATEGORY"},
              {"tip": "Tip text here.", "category": "CATEGORY"},
              {"tip": "Tip text here.", "category": "CATEGORY"}
            ]
            
            Rules:
            - Replace CATEGORY with one of: Sleep, Nutrition, Exercise, Mental Health, Hygiene, General
            - Each tip must be 1-2 sentences, practical and actionable
            - No bullet points or numbering inside the tip text itself
        """.trimIndent()

        val userMessage = "Give me 5 health tips about: $query. Category: $category"

        // Build the JSON request body (OpenAI-compatible format)
        val requestBody = JSONObject().apply {
            put("model", GROQ_MODEL)
            put("temperature", 0.7)
            put("max_tokens", 1024)
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", systemPrompt)
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", userMessage)
                })
            })
        }

        val request = Request.Builder()
            .url(GROQ_URL)
            .post(requestBody.toString().toRequestBody("application/json".toMediaType()))
            .addHeader("Authorization", "Bearer $GROQ_API_KEY")
            .addHeader("Content-Type", "application/json")
            .build()

        val response   = httpClient.newCall(request).execute()
        val bodyString = response.body?.string()
            ?: throw Exception("Empty response from Groq")

        if (!response.isSuccessful) {
            throw Exception("Groq API error ${response.code}: $bodyString")
        }

        // Parse Groq response — same structure as OpenAI
        val root    = JSONObject(bodyString)
        val content = root
            .getJSONArray("choices")
            .getJSONObject(0)
            .getJSONObject("message")
            .getString("content")
            .trim()

        // Strip any accidental markdown fences just in case
        val cleanJson = content
            .removePrefix("```json")
            .removePrefix("```")
            .removeSuffix("```")
            .trim()

        // Parse the JSON array of tips
        val tipsArray = JSONArray(cleanJson)
        val tips      = mutableListOf<GeneratedTip>()
        for (i in 0 until tipsArray.length()) {
            val obj = tipsArray.getJSONObject(i)
            tips.add(
                GeneratedTip(
                    tip      = obj.getString("tip"),
                    category = obj.optString("category", category)
                )
            )
        }
        tips
    }

// ── ViewModel ─────────────────────────────────────────────────────────────────
class CommunityTipsViewModel(app: Application) : AndroidViewModel(app) {

    private val _searchQuery   = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearching   = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _searchResults = MutableStateFlow<List<GeneratedTip>>(emptyList())
    val searchResults: StateFlow<List<GeneratedTip>> = _searchResults.asStateFlow()

    private val _hasSearched   = MutableStateFlow(false)
    val hasSearched: StateFlow<Boolean> = _hasSearched.asStateFlow()

    private val _savedTips     = MutableStateFlow<List<SavedTip>>(emptyList())
    val savedTips: StateFlow<List<SavedTip>> = _savedTips.asStateFlow()

    private val _savedTipTexts = MutableStateFlow<Set<String>>(emptySet())
    val savedTipTexts: StateFlow<Set<String>> = _savedTipTexts.asStateFlow()

    private val _message       = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    private val _savingTips    = MutableStateFlow<Set<String>>(emptySet())
    val savingTips: StateFlow<Set<String>> = _savingTips.asStateFlow()

    init {
        viewModelScope.launch { loadSavedTips() }
    }

    private fun loadSavedTips() {
        viewModelScope.launch {
            FirestoreTipsRepository.getSavedTipsFlow().collect { list ->
                _savedTips.value     = list
                _savedTipTexts.value = list.map { it.tip }.toSet()
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) clearSearch()
    }

    fun clearSearch() {
        _searchQuery.value   = ""
        _searchResults.value = emptyList()
        _hasSearched.value   = false
    }

    // ── Calls Groq API to generate real AI health tips ────────────────────────
    fun searchTipsWithAI(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch {
            _isSearching.value   = true
            _hasSearched.value   = true
            _searchResults.value = emptyList()

            try {
                val tips = fetchTipsFromGroq(query)
                _searchResults.value = tips

                if (tips.isEmpty()) {
                    _message.value = "No tips returned. Try a different topic."
                }
            } catch (e: Exception) {
                _searchResults.value = emptyList()
                // Log the real error
                android.util.Log.e("GroqAPI", "Error: ${e.message}", e)
                _message.value = "Error: ${e.message}" // show real error temporarily
            } finally {
                _isSearching.value = false
            }
        }
    }

    fun saveTip(tip: GeneratedTip) {
        val key = tip.tip
        if (_savingTips.value.contains(key)) return
        if (_savedTipTexts.value.contains(key)) return

        viewModelScope.launch {
            _savingTips.value = _savingTips.value + key
            val result = FirestoreTipsRepository.saveTip(
                tip      = tip.tip,
                category = tip.category,
                query    = _searchQuery.value
            )
            _savingTips.value = _savingTips.value - key
            if (result.isSuccess) {
                _savedTipTexts.value = _savedTipTexts.value + key
                _message.value       = "Tip saved! ✓"
            } else {
                _message.value = "Failed to save tip."
            }
        }
    }

    fun deleteSavedTip(tipId: String) {
        viewModelScope.launch {
            FirestoreTipsRepository.deleteSavedTip(tipId)
        }
    }

    fun clearMessage() { _message.value = null }
}
