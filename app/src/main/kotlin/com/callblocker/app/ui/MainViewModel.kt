package com.callblocker.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.callblocker.app.data.*
import com.callblocker.app.service.RulesCache
import com.callblocker.app.util.PatternMatcher
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).blockRuleDao()

    val rules: StateFlow<List<BlockRule>> =
        dao.getAllRules()
            .onEach { RulesCache.rules = it.filter { r -> r.isEnabled } } // keep cache fresh
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val blockedCalls: StateFlow<List<BlockedCall>> =
        dao.getRecentBlockedCalls()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalBlocked: StateFlow<Int> =
        dao.getTotalBlockedCount()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        // Pre-load cache immediately on startup
        viewModelScope.launch {
            RulesCache.rules = dao.getEnabledRules()
        }
    }

    fun addRule(pattern: String, ruleType: RuleType, label: String) {
        viewModelScope.launch {
            dao.insertRule(BlockRule(pattern = pattern.trim(), ruleType = ruleType, label = label.trim()))
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

    fun validatePattern(pattern: String, ruleType: RuleType): String? =
        PatternMatcher.validate(pattern, ruleType)
}
