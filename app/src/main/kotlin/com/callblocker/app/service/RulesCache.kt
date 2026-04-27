package com.callblocker.app.service

import com.callblocker.app.data.BlockRule

/**
 * In-memory cache of blocking rules.
 * Kept updated by the ViewModel whenever rules change.
 * The screening service reads from here instantly — no DB query on every call.
 */
object RulesCache {
    @Volatile
    var rules: List<BlockRule> = emptyList()
}
