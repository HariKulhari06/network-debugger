package com.hari.tracea.core.mock

import android.content.Context
import com.hari.tracea.core.model.HttpMethod
import com.hari.tracea.core.model.MockRule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Singleton engine for managing, persisting, and matching network mock rules.
 */
object MockEngine {
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }
    private var rulesFile: File? = null
    private var mockingEnabledFile: File? = null
    
    private val _rulesState = MutableStateFlow<List<MockRule>>(emptyList())
    val rulesState: StateFlow<List<MockRule>> = _rulesState.asStateFlow()

    private val _mockingEnabledState = MutableStateFlow(true)
    val mockingEnabledState: StateFlow<Boolean> = _mockingEnabledState.asStateFlow()

    /**
     * Initializes the engine and loads saved rules from disk.
     */
    fun initialize(context: Context) {
        rulesFile = File(context.filesDir, "tracea/mock_rules.json").apply {
            parentFile?.mkdirs()
        }
        mockingEnabledFile = File(context.filesDir, "tracea/mocking_enabled.txt")
        if (mockingEnabledFile?.exists() == true) {
            try {
                _mockingEnabledState.value = mockingEnabledFile?.readText()?.toBoolean() ?: true
            } catch (e: Exception) {
                // Ignore
            }
        }
        loadRules()
    }

    /**
     * Set whether API mocking is enabled globally.
     */
    fun setMockingEnabled(enabled: Boolean) {
        _mockingEnabledState.value = enabled
        try {
            mockingEnabledFile?.writeText(enabled.toString())
        } catch (e: Exception) {
            // Ignore
        }
    }

    private fun loadRules() {
        val file = rulesFile ?: return
        if (file.exists()) {
            try {
                val content = file.readText()
                val loaded = json.decodeFromString<List<MockRule>>(content)
                _rulesState.value = loaded
            } catch (e: Exception) {
                // Ignore or handle load error gracefully
            }
        }
    }

    private fun saveRules() {
        val file = rulesFile ?: return
        try {
            val content = json.encodeToString(loadedSerializer(), _rulesState.value)
            file.writeText(content)
        } catch (e: Exception) {
            // Ignore or handle save error gracefully
        }
    }

    private fun loadedSerializer() = kotlinx.serialization.serializer<List<MockRule>>()

    /**
     * Registers a new mock rule.
     */
    @Synchronized
    fun addRule(rule: MockRule) {
        _rulesState.update { current -> current + rule }
        saveRules()
    }

    /**
     * Deletes a mock rule by ID.
     */
    @Synchronized
    fun removeRule(id: String) {
        _rulesState.update { current -> current.filter { it.id != id } }
        saveRules()
    }

    /**
     * Updates an existing mock rule.
     */
    @Synchronized
    fun updateRule(rule: MockRule) {
        _rulesState.update { current ->
            current.map { if (it.id == rule.id) rule else it }
        }
        saveRules()
    }

    /**
     * Finds the first active rule that matches the requested URL and HTTP method.
     */
    @Synchronized
    fun matchRule(url: String, method: HttpMethod): MockRule? {
        return _rulesState.value.find { rule ->
            rule.enabled && 
            rule.method == method && 
            url.contains(rule.pathPattern, ignoreCase = true)
        }
    }
}
