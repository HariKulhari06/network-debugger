package com.hari.tracea.core.mock

import com.hari.tracea.core.model.HttpMethod
import com.hari.tracea.core.model.MockRule
import com.hari.tracea.core.util.readTextFromFile
import com.hari.tracea.core.util.writeTextToFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json

/**
 * Singleton engine for managing, persisting, and matching network mock rules.
 */
object MockEngine {
    private val json = Json { ignoreUnknownKeys = true; prettyPrint = true }
    private var filesDirPath: String? = null
    
    private val _rulesState = MutableStateFlow<List<MockRule>>(emptyList())
    val rulesState: StateFlow<List<MockRule>> = _rulesState.asStateFlow()

    private val _mockingEnabledState = MutableStateFlow(true)
    val mockingEnabledState: StateFlow<Boolean> = _mockingEnabledState.asStateFlow()

    /**
     * Initializes the engine and loads saved rules from disk.
     */
    fun initialize(dirPath: String) {
        filesDirPath = dirPath
        val content = readTextFromFile(dirPath, "mocking_enabled.txt")
        if (content != null) {
            try {
                _mockingEnabledState.value = content.toBoolean()
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
        filesDirPath?.let { dir ->
            try {
                writeTextToFile(dir, "mocking_enabled.txt", enabled.toString())
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    private fun loadRules() {
        val dir = filesDirPath ?: return
        val content = readTextFromFile(dir, "mock_rules.json")
        if (content != null) {
            try {
                val loaded = json.decodeFromString<List<MockRule>>(content)
                _rulesState.value = loaded
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    private fun saveRules() {
        val dir = filesDirPath ?: return
        try {
            val content = json.encodeToString(loadedSerializer(), _rulesState.value)
            writeTextToFile(dir, "mock_rules.json", content)
        } catch (e: Exception) {
            // Ignore
        }
    }

    private fun loadedSerializer() = kotlinx.serialization.serializer<List<MockRule>>()

    /**
     * Registers a new mock rule.
     */
    fun addRule(rule: MockRule) {
        _rulesState.update { current -> current + rule }
        saveRules()
    }

    /**
     * Deletes a mock rule by ID.
     */
    fun removeRule(id: String) {
        _rulesState.update { current -> current.filter { it.id != id } }
        saveRules()
    }

    /**
     * Updates an existing mock rule.
     */
    fun updateRule(rule: MockRule) {
        _rulesState.update { current ->
            current.map { if (it.id == rule.id) rule else it }
        }
        saveRules()
    }

    /**
     * Finds the first active rule that matches the requested URL and HTTP method.
     */
    fun matchRule(url: String, method: HttpMethod): MockRule? {
        return _rulesState.value.find { rule ->
            rule.enabled && 
            rule.method == method && 
            url.contains(rule.pathPattern, ignoreCase = true)
        }
    }
}
