package com.callblocker.app.util

import com.callblocker.app.data.BlockRule
import com.callblocker.app.data.RuleType

/**
 * Core pattern matching engine.
 *
 * When a call comes in, this checks the incoming phone number against
 * every rule. The first matching rule wins.
 */
object PatternMatcher {

    /**
     * Returns the matching rule if the [incomingNumber] should be blocked,
     * or null if the call is clean.
     */
    fun findMatchingRule(incomingNumber: String, rules: List<BlockRule>): BlockRule? {
        // Normalise: strip spaces, dashes, parentheses for consistent matching
        val normalised = normalise(incomingNumber)

        for (rule in rules) {
            if (!rule.isEnabled) continue
            val pattern = normalise(rule.pattern)
            val matched = when (rule.ruleType) {
                RuleType.EXACT       -> normalised == pattern
                RuleType.STARTS_WITH -> normalised.startsWith(pattern)
                RuleType.ENDS_WITH   -> normalised.endsWith(pattern)
                RuleType.CONTAINS    -> normalised.contains(pattern)
                RuleType.REGEX       -> safeRegexMatch(normalised, pattern)
            }
            if (matched) return rule
        }
        return null
    }

    /** Returns true only if [number] matches the regex [pattern]; false on bad regex. */
    private fun safeRegexMatch(number: String, pattern: String): Boolean {
        return try {
            Regex(pattern).containsMatchIn(number)
        } catch (e: Exception) {
            false   // Bad regex → don't crash, just don't block
        }
    }

    /** Strip formatting characters to make matching consistent. */
    fun normalise(number: String): String =
        number.replace(Regex("[\\s\\-().+]"), "")

    /**
     * Validate that a pattern is acceptable before saving it.
     * Returns an error message or null if valid.
     */
    fun validate(pattern: String, ruleType: RuleType): String? {
        if (pattern.isBlank()) return "Pattern cannot be empty"
        if (ruleType == RuleType.REGEX) {
            return try {
                Regex(pattern)
                null   // Valid regex
            } catch (e: Exception) {
                "Invalid regex: ${e.message}"
            }
        }
        return null
    }
}
