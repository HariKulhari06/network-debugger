package com.hari.networkdebugger.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hari.networkdebugger.ui.navigation.DebuggerNavHost
import com.hari.networkdebugger.ui.navigation.DebuggerTab
import com.hari.networkdebugger.ui.navigation.NetworkRoute
import com.hari.networkdebugger.ui.navigation.RequestDetailRoute
import com.hari.networkdebugger.ui.navigation.SettingsRoute
import com.hari.networkdebugger.ui.navigation.TimelineRoute
import com.hari.networkdebugger.ui.theme.DebuggerTheme
import com.hari.networkdebugger.ui.theme.LocalDebuggerColors

class NetworkDebuggerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DebuggerTheme {
                val navController = rememberNavController()
                val colors = LocalDebuggerColors.current
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val showBottomBar = currentDestination?.hasRoute<RequestDetailRoute>() != true

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            NavigationBar(
                                containerColor = colors.surfaceVariant
                            ) {
                                NavigationItems(currentDestination, navController)
                            }
                        }
                    },
                    containerColor = colors.surface,
                    // We set contentWindowInsets to 0 here because we want the individual screens
                    // to handle their own insets (status bar for TopAppBar, etc.)
                    // This prevents the root Scaffold from adding top padding to innerPadding.
                    contentWindowInsets = WindowInsets(0.dp, 0.dp, 0.dp, 0.dp)
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .consumeWindowInsets(innerPadding)
                            .background(colors.surface)
                    ) {
                        DebuggerNavHost(navController)
                    }
                }
            }
        }
    }

    @Composable
    private fun RowScope.NavigationItems(currentDestination: NavDestination?, navController: NavHostController) {
        val colors = LocalDebuggerColors.current
        DebuggerTab.entries.forEach { tab ->
            val selected = when (tab) {
                DebuggerTab.NETWORK -> currentDestination?.hasRoute<NetworkRoute>() == true
                DebuggerTab.TIMELINE -> currentDestination?.hasRoute<TimelineRoute>() == true
               // DebuggerTab.SETTINGS -> currentDestination?.hasRoute<SettingsRoute>() == true
            }

            NavigationBarItem(
                selected = selected,
                onClick = {
                    val route = when (tab) {
                        DebuggerTab.NETWORK -> NetworkRoute
                        DebuggerTab.TIMELINE -> TimelineRoute
                       // DebuggerTab.SETTINGS -> SettingsRoute
                    }
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) { 
                            saveState = true 
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(tab.icon, contentDescription = tab.label) },
                label = { Text(tab.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = colors.primary,
                    selectedTextColor = colors.primary,
                    unselectedIconColor = colors.onSurfaceVariant,
                    unselectedTextColor = colors.onSurfaceVariant,
                    indicatorColor = colors.surfaceContainer
                )
            )
        }
    }
}
