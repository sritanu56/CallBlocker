package com.callblocker.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.callblocker.app.data.RuleType
import com.callblocker.app.ui.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRuleDialog(viewModel: MainViewModel, onDismiss: () -> Unit) {

    var pattern  by remember { mutableStateOf("") }
    var label    by remember { mutableStateOf("") }
    var ruleType by remember { mutableStateOf(RuleType.STARTS_WITH) }
    var error    by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Blocking Rule") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // ── Rule type selector ───────────────────────────────────────
                Text("Rule type:", style = MaterialTheme.typography.labelLarge)
                RuleTypeSelector(selected = ruleType, onSelect = { ruleType = it })

                // ── Helpful hint based on type ───────────────────────────────
                Text(
                    text = hintFor(ruleType),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                // ── Pattern input ────────────────────────────────────────────
                OutlinedTextField(
                    value = pattern,
                    onValueChange = { pattern = it; error = null },
                    label = { Text("Pattern") },
                    placeholder = { Text(placeholderFor(ruleType)) },
                    isError = error != null,
                    supportingText = error?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // ── Optional friendly label ──────────────────────────────────
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Label (optional)") },
                    placeholder = { Text("e.g. Telemarketer, Bank fraud, etc.") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Divider()

                // ── Quick-add examples ───────────────────────────────────────
                Text("Common examples:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                QuickAddChips { chip ->
                    pattern  = chip.pattern
                    ruleType = chip.type
                    label    = chip.label
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val validationError = viewModel.validatePattern(pattern, ruleType)
                if (validationError != null) {
                    error = validationError
                } else {
                    viewModel.addRule(pattern, ruleType, label)
                    onDismiss()
                }
            }) {
                Text("Add Rule")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RuleTypeSelector(selected: RuleType, onSelect: (RuleType) -> Unit) {
    val types = listOf(
        RuleType.STARTS_WITH to "Starts with",
        RuleType.EXACT       to "Exact number",
        RuleType.CONTAINS    to "Contains",
        RuleType.ENDS_WITH   to "Ends with",
        RuleType.REGEX       to "Regex (advanced)"
    )
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        types.forEach { (type, label) ->
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                RadioButton(
                    selected = selected == type,
                    onClick  = { onSelect(type) }
                )
                Text(label, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickAddChips(onSelect: (QuickChip) -> Unit) {
    val chips = listOf(
        QuickChip("140",       RuleType.STARTS_WITH, "TRAI Telemarketer prefix"),
        QuickChip("1800",      RuleType.STARTS_WITH, "Toll-free spam"),
        QuickChip("000000",    RuleType.ENDS_WITH,   "Likely fake number"),
        QuickChip("000000000", RuleType.CONTAINS,    "Repeated zeros"),
        QuickChip("^\\+?0+\$", RuleType.REGEX,       "All-zeros numbers"),
    )
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        chips.forEach { chip ->
            AssistChip(
                onClick = { onSelect(chip) },
                label = { Text("${chip.pattern} — ${chip.label}", style = MaterialTheme.typography.bodySmall) }
            )
        }
    }
}

data class QuickChip(val pattern: String, val type: RuleType, val label: String)

private fun hintFor(type: RuleType) = when (type) {
    RuleType.EXACT       -> "The complete phone number must match exactly. Best for blocking one specific number."
    RuleType.STARTS_WITH -> "Blocks all numbers beginning with your prefix. Great for blocking entire telemarketer ranges like '140'."
    RuleType.ENDS_WITH   -> "Blocks numbers that end with your suffix."
    RuleType.CONTAINS    -> "Blocks any number that has your text anywhere inside it."
    RuleType.REGEX       -> "Advanced: full regular expression. E.g. ^\\+911[89]\\d{8}$ blocks Jio/Airtel-range numbers."
}

private fun placeholderFor(type: RuleType) = when (type) {
    RuleType.EXACT       -> "+919876543210"
    RuleType.STARTS_WITH -> "+91140  or  1800  or  0120"
    RuleType.ENDS_WITH   -> "000000"
    RuleType.CONTAINS    -> "9999"
    RuleType.REGEX       -> "^\\+?91140\\d+"
}
