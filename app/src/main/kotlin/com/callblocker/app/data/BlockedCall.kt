package com.callblocker.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/** A record of a call that was automatically blocked. */
@Entity(tableName = "blocked_calls")
data class BlockedCall(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val phoneNumber: String,          // The number that tried to call
    val blockedAt: Long = System.currentTimeMillis(),
    val matchedPattern: String,       // Which rule triggered the block
    val matchedRuleType: RuleType     // What type of rule it was
)
