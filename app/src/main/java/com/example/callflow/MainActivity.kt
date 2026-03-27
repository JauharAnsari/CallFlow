package com.example.callflow

import android.os.Bundle
import android.content.Intent
import android.content.pm.PackageManager
import androidx.compose.ui.text.style.TextAlign
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Dialpad
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.callflow.repository.CallLogRepository
import com.example.callflow.repository.ContactsRepository
import com.example.callflow.ui.calllogs.CallLogScreen
import com.example.callflow.ui.contacts.ContactsScreen
import com.example.callflow.ui.dialer.DialerScreen
import com.example.callflow.ui.theme.CallFlowTheme
import com.example.callflow.utils.CallManager
import com.example.callflow.viewmodel.CallLogViewModel
import com.example.callflow.viewmodel.ContactsViewModel
import com.example.callflow.viewmodel.DialerViewModel

import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    private lateinit var callManager: CallManager

    private val requestPermissionsLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.all { it.value }) {
            recreate()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        callManager = CallManager(this)
        
        enableEdgeToEdge()
        setContent {
            CallFlowTheme {
                val navController = rememberNavController()
                val items = listOf(
                    Screen.Dialer,
                    Screen.Contacts,
                    Screen.CallLogs
                )

                // Mocking simple DI
                val dialerViewModel: DialerViewModel = viewModel {
                    DialerViewModel(ContactsRepository(this@MainActivity))
                }
                val contactsViewModel: ContactsViewModel = viewModel {
                    ContactsViewModel(ContactsRepository(this@MainActivity))
                }
                val callLogViewModel: CallLogViewModel = viewModel {
                    CallLogViewModel(CallLogRepository(this@MainActivity))
                }

                val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
                var hasPermissions by remember { mutableStateOf(callManager.hasPermissions()) }
                var isDefaultDialer by remember { mutableStateOf(callManager.isDefaultDialer()) }

                DisposableEffect(lifecycleOwner) {
                    val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
                        if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                            hasPermissions = callManager.hasPermissions()
                            isDefaultDialer = callManager.isDefaultDialer()
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                if (!hasPermissions) {
                    PermissionRequestScreen {
                        requestPermissionsLauncher.launch(
                            arrayOf(
                                android.Manifest.permission.CALL_PHONE,
                                android.Manifest.permission.READ_CONTACTS,
                                android.Manifest.permission.READ_CALL_LOG,
                                android.Manifest.permission.READ_PHONE_STATE
                            )
                        )
                    }
                } else if (!isDefaultDialer) {
                    DefaultDialerRequestScreen {
                        callManager.requestDefaultDialer(this@MainActivity)
                    }
                } else {
                    Scaffold(
                        bottomBar = {
                            NavigationBar {
                                val navBackStackEntry by navController.currentBackStackEntryAsState()
                                val currentDestination = navBackStackEntry?.destination
                                items.forEach { screen ->
                                    NavigationBarItem(
                                        icon = { Icon(screen.icon, contentDescription = null) },
                                        label = { Text(screen.route) },
                                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                                        onClick = {
                                            navController.navigate(screen.route) {
                                                popUpTo(navController.graph.findStartDestination().id) {
                                                    saveState = true
                                                }
                                                launchSingleTop = true
                                                restoreState = true
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        NavHost(
                            navController,
                            startDestination = Screen.Dialer.route,
                            Modifier.padding(innerPadding)
                        ) {
                            composable(Screen.Dialer.route) {
                                DialerScreen(dialerViewModel) { number ->
                                    callManager.makeCall(number)
                                }
                            }
                            composable(Screen.Contacts.route) {
                                ContactsScreen(contactsViewModel) { number ->
                                    callManager.makeCall(number)
                                }
                            }
                            composable(Screen.CallLogs.route) {
                                CallLogScreen(callLogViewModel) { number ->
                                    callManager.makeCall(number)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}

sealed class Screen(val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Dialer : Screen("Dialer", Icons.Default.Dialpad)
    object Contacts : Screen("Contacts", Icons.Default.Contacts)
    object CallLogs : Screen("CallLogs", Icons.Default.History)
}

@Composable
fun PermissionRequestScreen(onRequest: () -> Unit) {
    Column(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Permissions required to use the app")
        Spacer(modifier = androidx.compose.ui.Modifier.size(16.dp))
        Button(onClick = onRequest) {
            Text("Grant Permissions")
        }
    }
}

@Composable
fun DefaultDialerRequestScreen(onRequest: () -> Unit) {
    Column(
        modifier = androidx.compose.ui.Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "App must be set as Default Phone App to handle calls internally.",
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = androidx.compose.ui.Modifier.size(16.dp))
        Button(onClick = onRequest) {
            Text("Set as Default")
        }
    }
}