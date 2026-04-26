package com.callblocker.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents one blocking rule stored in the database.
 *
 * Rule types:
 * - EXACT      : The full number must match exactly.         e.g. "+918001234567"
 * - STARTS_WITH: Number starts with this prefix.            e.g. "+91140" blocks all 140xxx calls
 * - ENDS_WITH  : Number ends with this suffix.              e.g. "0000" blocks numbers ending 0000
 * - CONTAINS   : Number contains this anywhere.             e.g. "999" blocks anything with 999
 * - REGEX      : Full regular expression pattern.           e.g. "^\+911[89]\d{8}$"
 */
@Entity(tableName = "block_rules")
data class BlockRule(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val pattern: String,              // The actual pattern text the user typed
    val ruleType: RuleType,           // What kind of match to perform
    val label: String = "",           // Optional friendly name, e.g. "Telemarketer prefix"
    val isEnabled: Boolean = true,    // Can be toggled on/off without deleting
    val createdAt: Long = System.currentTimeMillis()
)

enum class RuleType {
    EXACT,
    STARTS_WITH,
    ENDS_WITH,
    CONTAINS,
    REGEX
}
