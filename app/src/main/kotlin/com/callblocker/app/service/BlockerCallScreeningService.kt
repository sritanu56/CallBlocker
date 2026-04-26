package com.callblocker.app.service

import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import com.callblocker.app.data.AppDatabase
import com.callblocker.app.data.BlockedCall
import com.callblocker.app.util.PatternMatcher
import kotlinx.coroutines.*

/**
 * This service runs in the background and intercepts EVERY incoming call.
 *
 * Android calls [onScreenCall] before the phone even rings.
 * We check the number against all rules, and either:
 *   → Block it silently (caller hears busy tone, you see nothing)
 *   → Allow it through normally
 */
class BlockerCallScreeningService : CallScreeningService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onScreenCall(callDetails: Call.Details) {
        val rawNumber = callDetails.handle?.schemeSpecificPart ?: "Unknown"
        Log.d("CallBlocker", "Screening call from: $rawNumber")

        serviceScope.launch {
            val db = AppDatabase.getInstance(applicationContext)
            val dao = db.blockRuleDao()
            val rules = dao.getEnabledRules()

            val matchedRule = PatternMatcher.findMatchingRule(rawNumber, rules)

            if (matchedRule != null) {
                // ── BLOCK THIS CALL ─────────────────────────────────────────
                Log.d("CallBlocker", "BLOCKING $rawNumber — matched rule: ${matchedRule.pattern}")

                // Log it so the user can see it was blocked
                dao.logBlockedCall(
                    BlockedCall(
                        phoneNumber    = rawNumber,
                        matchedPattern = matchedRule.pattern,
                        matchedRuleType = matchedRule.ruleType
                    )
                )

                // Tell Android to reject the call
                respondToCall(
                    callDetails,
                    CallResponse.Builder()
                        .setRejectCall(true)           // Hang up
                        .setDisallowVoicemail(true)    // Don't let it go to voicemail
                        .setSkipCallLog(false)         // Still record in system call log
                        .setSkipNotification(true)     // No missed-call notification
                        .build()
                )
            } else {
                // ── ALLOW THIS CALL ─────────────────────────────────────────
                respondToCall(
                    callDetails,
                    CallResponse.Builder()
                        .setRejectCall(false)
                        .build()
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
