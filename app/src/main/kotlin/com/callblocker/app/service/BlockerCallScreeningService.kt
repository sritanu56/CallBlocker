package com.callblocker.app.service

import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import com.callblocker.app.data.AppDatabase
import com.callblocker.app.data.BlockedCall
import com.callblocker.app.data.BlockRule
import com.callblocker.app.util.PatternMatcher
import kotlinx.coroutines.*

class BlockerCallScreeningService : CallScreeningService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onScreenCall(callDetails: Call.Details) {
        val rawNumber = callDetails.handle?.schemeSpecificPart ?: "Unknown"
        Log.d("CallBlocker", "Screening: $rawNumber")

        val rules = RulesCache.rules
        val matchedRule = PatternMatcher.findMatchingRule(rawNumber, rules)

        if (matchedRule != null) {
            Log.d("CallBlocker", "BLOCKING $rawNumber")

            respondToCall(
                callDetails,
                CallResponse.Builder()
                    .setRejectCall(true)
                    .setSkipCallLog(false)
                    .setSkipNotification(true)
                    .build()
            )

            serviceScope.launch {
                AppDatabase.getInstance(applicationContext).blockRuleDao().logBlockedCall(
                    BlockedCall(
                        phoneNumber     = rawNumber,
                        matchedPattern  = matchedRule.pattern,
                        matchedRuleType = matchedRule.ruleType
                    )
                )
            }
        } else {
            respondToCall(
                callDetails,
                CallResponse.Builder().setRejectCall(false).build()
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}

// Kept in same file to avoid import issues
object RulesCache {
    @Volatile
    var rules: List<BlockRule> = emptyList()
}
