package com.callblocker.app

import android.app.role.RoleManager
import android.os.Bundle
import android.telecom.TelecomManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.callblocker.app.ui.MainViewModel
import com.callblocker.app.ui.screens.*
import com.callblocker.app.ui.theme.CallBlockerTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    // ── Activity result launcher to ask for the Caller ID role ───────────────
    private val roleRequestLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // After the user responds, refresh the role check
        checkRole()
    }

    private var hasCallerIdRole by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkRole()

        setContent {
            CallBlockerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (!hasCallerIdRole) {
                        // Show setup screen if we don't have the required role yet
                        SetupScreen(onRequestRole = ::requestCallerIdRole)
                    } else {
                        MainScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkRole() // Refresh when coming back from settings
    }

    private fun checkRole() {
        val roleManager = getSystemService(RoleManager::class.java)
        hasCallerIdRole = roleManager.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)
    }

    private fun requestCallerIdRole() {
        val roleManager = getSystemService(RoleManager::class.java)
        val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING)
        roleRequestLauncher.launch(intent)
    }
}

// ── Tab navigation ────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val totalBlocked by viewModel.totalBlocked.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("📵 Call Blocker", fontWeight = FontWeight.Bold)
                        Text(
                            "$totalBlocked calls blocked",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick  = { selectedTab = 0 },
                    icon     = { Icon(Icons.Default.Rule, contentDescription = null) },
                    label    = { Text("Rules") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick  = { selectedTab = 1 },
                    icon     = { Icon(Icons.Default.History, contentDescription = null) },
                    label    = { Text("Blocked Log") }
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                0 -> RulesScreen(viewModel = viewModel)
                1 -> BlockedCallsScreen(viewModel = viewModel)
            }
        }
    }
}
