package com.callblocker.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.callblocker.app.data.*
import com.callblocker.app.util.PatternMatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).blockRuleDao()

    // ── Live data streams that the UI observes ───────────────────────────────

    val rules: StateFlow<List<BlockRule>> =
        dao.getAllRules()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val blockedCalls: StateFlow<List<BlockedCall>> =
        dao.getRecentBlockedCalls()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalBlocked: StateFlow<Int> =
        dao.getTotalBlockedCount()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // ── Actions the UI calls ─────────────────────────────────────────────────

    fun addRule(pattern: String, ruleType: RuleType, label: String) {
        viewModelScope.launch {
            dao.insertRule(
                BlockRule(
                    pattern  = pattern.trim(),
                    ruleType = ruleType,
                    label    = label.trim()
                )
            )
        }
    }

    fun deleteRule(rule: BlockRule) {
        viewModelScope.launch { dao.deleteRule(rule) }
    }

    fun toggleRule(rule: BlockRule, enabled: Boolean) {
        viewModelScope.launch { dao.setRuleEnabled(rule.id, enabled) }
    }

    fun clearBlockedCallLog() {
        viewModelScope.launch { dao.clearBlockedCallLog() }
    }

    /** Validates input before saving. Returns error string or null. */
    fun validatePattern(pattern: String, ruleType: RuleType): String? =
        PatternMatcher.validate(pattern, ruleType)
}
