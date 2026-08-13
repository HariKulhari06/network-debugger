package com.hari.tracea.ui.screens.mocks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hari.tracea.core.mock.MockEngine
import com.hari.tracea.core.model.MockRule
import com.hari.tracea.ui.TraceaServiceLocator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel managing Mock Rules list interactions.
 */
class MockRulesViewModel : ViewModel() {

    val rules: StateFlow<List<MockRule>> = MockEngine.rulesState

    val mockingEnabled: StateFlow<Boolean> = MockEngine.mockingEnabledState

    fun setMockingEnabled(enabled: Boolean) {
        viewModelScope.launch {
            MockEngine.setMockingEnabled(enabled)
        }
    }

    val capturedPaths: StateFlow<List<String>> = (TraceaServiceLocator.store?.getAll() ?: flowOf(emptyList()))
        .map { events ->
            events.map { it.path }.filter { it.isNotBlank() }.distinct().sorted()
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addRule(rule: MockRule) {
        viewModelScope.launch {
            MockEngine.addRule(rule)
        }
    }

    fun updateRule(rule: MockRule) {
        viewModelScope.launch {
            MockEngine.updateRule(rule)
        }
    }

    fun removeRule(id: String) {
        viewModelScope.launch {
            MockEngine.removeRule(id)
        }
    }

    fun toggleRule(id: String, enabled: Boolean) {
        viewModelScope.launch {
            val rule = rules.value.find { it.id == id }
            if (rule != null) {
                MockEngine.updateRule(rule.copy(enabled = enabled))
            }
        }
    }

    suspend fun getResponseBodyForPath(path: String): String? {
        val store = TraceaServiceLocator.store ?: return null
        val events = store.getAll().firstOrNull() ?: return null
        // Find the latest completed event with this path that has text body
        val event = events.firstOrNull { 
            it.path == path && 
            it.responseBody is com.hari.tracea.core.model.BodyData.Text 
        }
        return (event?.responseBody as? com.hari.tracea.core.model.BodyData.Text)?.content
    }
}
