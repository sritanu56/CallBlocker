package com.callblocker.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockRuleDao {

    // ── Rules ────────────────────────────────────────────────────────────────

    @Query("SELECT * FROM block_rules ORDER BY createdAt DESC")
    fun getAllRules(): Flow<List<BlockRule>>

    /** Only the enabled rules — used by the screening service for performance. */
    @Query("SELECT * FROM block_rules WHERE isEnabled = 1")
    suspend fun getEnabledRules(): List<BlockRule>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRule(rule: BlockRule)

    @Delete
    suspend fun deleteRule(rule: BlockRule)

    @Query("UPDATE block_rules SET isEnabled = :enabled WHERE id = :id")
    suspend fun setRuleEnabled(id: Int, enabled: Boolean)

    // ── Blocked call log ─────────────────────────────────────────────────────

    @Query("SELECT * FROM blocked_calls ORDER BY blockedAt DESC LIMIT 200")
    fun getRecentBlockedCalls(): Flow<List<BlockedCall>>

    @Insert
    suspend fun logBlockedCall(call: BlockedCall)

    @Query("DELETE FROM blocked_calls")
    suspend fun clearBlockedCallLog()

    @Query("SELECT COUNT(*) FROM blocked_calls")
    fun getTotalBlockedCount(): Flow<Int>
}
