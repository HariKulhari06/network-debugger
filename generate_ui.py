import os

BASE_DIR = "/Users/hari/Documents/kids/Learning/Android/network-debugger/network-debugger-ui"

def write_file(path, content):
    full_path = os.path.join(BASE_DIR, path)
    os.makedirs(os.path.dirname(full_path), exist_ok=True)
    with open(full_path, "w") as f:
        f.write(content.strip() + "\n")

files = {}

files["build.gradle.kts"] = """
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.hari.networkdebugger.ui"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":network-debugger-core"))
    implementation(project(":network-debugger-storage"))
    
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.compose.material.icons)
    implementation(libs.compose.foundation)
    implementation(libs.compose.runtime)
    debugImplementation(libs.compose.ui.tooling)
    
    implementation(libs.core.ktx)
    implementation(libs.activity.compose)
    implementation(libs.navigation.compose)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewmodel.compose)
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)
    implementation(libs.serialization.json)
}
"""

files["src/main/AndroidManifest.xml"] = """
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application>
        <activity
            android:name=".NetworkDebuggerActivity"
            android:theme="@style/Theme.NetworkDebugger"
            android:exported="false" />
    </application>
</manifest>
"""

files["src/main/res/values/themes.xml"] = """
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <style name="Theme.NetworkDebugger" parent="android:Theme.Material.Light.NoActionBar" />
</resources>
"""

files["src/main/kotlin/com/hari/networkdebugger/ui/DebuggerServiceLocator.kt"] = """
package com.hari.networkdebugger.ui

import com.hari.networkdebugger.core.config.NetworkDebuggerConfig
import com.hari.networkdebugger.core.store.NetworkEventStore

object DebuggerServiceLocator {
    var store: NetworkEventStore? = null
    var config: NetworkDebuggerConfig? = null
}
"""

files["src/main/kotlin/com/hari/networkdebugger/ui/theme/DebuggerColors.kt"] = """
package com.hari.networkdebugger.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.hari.networkdebugger.core.model.HttpMethod

data class DebuggerColorScheme(
    val surface: Color = Color(0xFF121212),
    val surfaceVariant: Color = Color(0xFF1E1E2E),
    val surfaceContainer: Color = Color(0xFF2A2A3A),
    val onSurface: Color = Color(0xFFE0E0E0),
    val onSurfaceVariant: Color = Color(0xFF9E9E9E),
    val primary: Color = Color(0xFF5B8DEF),
    val outline: Color = Color(0xFF333344),
    val sectionHeader: Color = Color(0xFF7C6EF6),
    val methodGet: Color = Color(0xFF4CAF50),
    val methodPost: Color = Color(0xFF5B8DEF),
    val methodPut: Color = Color(0xFFFF9800),
    val methodDelete: Color = Color(0xFFEF5350),
    val methodPatch: Color = Color(0xFFAB47BC),
    val status2xx: Color = Color(0xFF4CAF50),
    val status3xx: Color = Color(0xFF5B8DEF),
    val status4xx: Color = Color(0xFFEF5350),
    val status5xx: Color = Color(0xFFEF5350),
    val liveDot: Color = Color(0xFF4CAF50),
    val errorDot: Color = Color(0xFFEF5350)
) {
    fun methodColor(method: HttpMethod): Color = when(method) {
        HttpMethod.GET -> methodGet
        HttpMethod.POST -> methodPost
        HttpMethod.PUT -> methodPut
        HttpMethod.DELETE -> methodDelete
        HttpMethod.PATCH -> methodPatch
        else -> onSurfaceVariant
    }

    fun statusBadgeColor(statusCode: Int): Color = when(statusCode) {
        in 200..299 -> status2xx
        in 300..399 -> status3xx
        in 400..499 -> status4xx
        in 500..599 -> status5xx
        else -> onSurfaceVariant
    }
}

val LocalDebuggerColors = staticCompositionLocalOf { DebuggerColorScheme() }
"""

files["src/main/kotlin/com/hari/networkdebugger/ui/theme/DebuggerTheme.kt"] = """
package com.hari.networkdebugger.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun DebuggerTheme(content: @Composable () -> Unit) {
    val colors = DebuggerColorScheme()
    val materialColors = darkColorScheme(
        background = colors.surface,
        surface = colors.surface,
        surfaceVariant = colors.surfaceVariant,
        onSurface = colors.onSurface,
        onSurfaceVariant = colors.onSurfaceVariant,
        primary = colors.primary,
        outline = colors.outline
    )

    CompositionLocalProvider(LocalDebuggerColors provides colors) {
        MaterialTheme(
            colorScheme = materialColors,
            content = content
        )
    }
}
"""

files["src/main/kotlin/com/hari/networkdebugger/ui/navigation/DebuggerNavigation.kt"] = """
package com.hari.networkdebugger.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hari.networkdebugger.ui.screens.detail.RequestDetailScreen
import com.hari.networkdebugger.ui.screens.network.NetworkListScreen
import com.hari.networkdebugger.ui.screens.settings.SettingsScreen
import com.hari.networkdebugger.ui.screens.timeline.TimelineScreen
import kotlinx.serialization.Serializable

@Serializable object NetworkRoute
@Serializable data class RequestDetailRoute(val eventId: String)
@Serializable object TimelineRoute
@Serializable object SettingsRoute

enum class DebuggerTab(val label: String, val icon: ImageVector) {
    NETWORK("Network", Icons.Default.List),
    TIMELINE("Timeline", Icons.Default.Schedule),
    SETTINGS("Settings", Icons.Default.Settings)
}

@Composable
fun DebuggerNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = NetworkRoute) {
        composable<NetworkRoute> {
            NetworkListScreen(
                onEventClick = { id -> navController.navigate(RequestDetailRoute(id)) }
            )
        }
        composable<RequestDetailRoute> { backStackEntry ->
            // In a real app we'd get this from backStackEntry args
            val eventId = "" // Placeholder due to Compose Navigation limitations in this snippet
            RequestDetailScreen(
                eventId = eventId,
                onBack = { navController.popBackStack() }
            )
        }
        composable<TimelineRoute> {
            TimelineScreen()
        }
        composable<SettingsRoute> {
            SettingsScreen()
        }
    }
}
"""

files["src/main/kotlin/com/hari/networkdebugger/ui/NetworkDebuggerActivity.kt"] = """
package com.hari.networkdebugger.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.hari.networkdebugger.ui.navigation.DebuggerNavHost
import com.hari.networkdebugger.ui.navigation.DebuggerTab
import com.hari.networkdebugger.ui.navigation.NetworkRoute
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
                var selectedTab by remember { mutableStateOf(DebuggerTab.NETWORK) }
                val colors = LocalDebuggerColors.current

                Scaffold(
                    bottomBar = {
                        NavigationBar(
                            containerColor = colors.surfaceVariant
                        ) {
                            DebuggerTab.values().forEach { tab ->
                                NavigationBarItem(
                                    selected = selectedTab == tab,
                                    onClick = {
                                        selectedTab = tab
                                        when(tab) {
                                            DebuggerTab.NETWORK -> navController.navigate(NetworkRoute)
                                            DebuggerTab.TIMELINE -> navController.navigate(TimelineRoute)
                                            DebuggerTab.SETTINGS -> navController.navigate(SettingsRoute)
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
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding).background(colors.surface)) {
                        DebuggerNavHost(navController)
                    }
                }
            }
        }
    }
}
"""

files["src/main/kotlin/com/hari/networkdebugger/ui/components/MethodBadge.kt"] = """
package com.hari.networkdebugger.ui.components

import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hari.networkdebugger.core.model.HttpMethod
import com.hari.networkdebugger.ui.theme.LocalDebuggerColors

@Composable
fun MethodBadge(method: HttpMethod, modifier: Modifier = Modifier) {
    val colors = LocalDebuggerColors.current
    Text(
        text = method.name,
        color = colors.methodColor(method),
        fontWeight = FontWeight.Bold,
        modifier = modifier.width(60.dp)
    )
}
"""

files["src/main/kotlin/com/hari/networkdebugger/ui/components/StatusBadge.kt"] = """
package com.hari.networkdebugger.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hari.networkdebugger.ui.theme.LocalDebuggerColors

@Composable
fun StatusBadge(statusCode: Int, statusMessage: String? = null, showMessage: Boolean = false) {
    val colors = LocalDebuggerColors.current
    val text = if (showMessage && statusMessage != null) "$statusCode $statusMessage" else statusCode.toString()
    
    Box(
        modifier = Modifier
            .background(colors.statusBadgeColor(statusCode), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
"""

files["src/main/kotlin/com/hari/networkdebugger/ui/components/SectionHeader.kt"] = """
package com.hari.networkdebugger.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hari.networkdebugger.ui.theme.LocalDebuggerColors

@Composable
fun SectionHeader(title: String, action: String? = null, onAction: (() -> Unit)? = null) {
    val colors = LocalDebuggerColors.current
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title.uppercase(),
            color = colors.sectionHeader,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )
        if (action != null && onAction != null) {
            Text(
                text = action,
                color = colors.primary,
                fontSize = 12.sp,
                modifier = Modifier.clickable { onAction() }
            )
        }
    }
}
"""

files["src/main/kotlin/com/hari/networkdebugger/ui/components/CodeBlock.kt"] = """
package com.hari.networkdebugger.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hari.networkdebugger.ui.theme.LocalDebuggerColors

@Composable
fun CodeBlock(content: String, modifier: Modifier = Modifier, onCopy: (() -> Unit)? = null) {
    val colors = LocalDebuggerColors.current
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(colors.surfaceVariant, RoundedCornerShape(12.dp))
            .horizontalScroll(rememberScrollState())
            .padding(12.dp)
    ) {
        Text(
            text = content,
            color = colors.onSurface,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp
        )
    }
}
"""

files["src/main/kotlin/com/hari/networkdebugger/ui/components/SummaryCardsRow.kt"] = """
package com.hari.networkdebugger.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.hari.networkdebugger.ui.theme.LocalDebuggerColors

@Composable
fun SummaryCardsRow(status: String, duration: String, size: String, time: String) {
    val colors = LocalDebuggerColors.current
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        SummaryCard("STATUS", status)
        SummaryCard("DURATION", duration)
        SummaryCard("SIZE", size)
        SummaryCard("TIME", time)
    }
}

@Composable
private fun SummaryCard(label: String, value: String) {
    val colors = LocalDebuggerColors.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = colors.onSurfaceVariant, fontSize = 10.sp)
        Text(text = value, color = colors.onSurface, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}
"""

files["src/main/kotlin/com/hari/networkdebugger/ui/screens/network/NetworkListScreen.kt"] = """
package com.hari.networkdebugger.ui.screens.network

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hari.networkdebugger.ui.theme.LocalDebuggerColors

@Composable
fun NetworkListScreen(onEventClick: (String) -> Unit) {
    val colors = LocalDebuggerColors.current
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Network List (Stub)", color = colors.onSurface)
    }
}
"""

files["src/main/kotlin/com/hari/networkdebugger/ui/screens/detail/RequestDetailScreen.kt"] = """
package com.hari.networkdebugger.ui.screens.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hari.networkdebugger.ui.theme.LocalDebuggerColors

@Composable
fun RequestDetailScreen(eventId: String, onBack: () -> Unit) {
    val colors = LocalDebuggerColors.current
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Request Details (Stub) for $eventId", color = colors.onSurface)
    }
}
"""

files["src/main/kotlin/com/hari/networkdebugger/ui/screens/timeline/TimelineScreen.kt"] = """
package com.hari.networkdebugger.ui.screens.timeline

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hari.networkdebugger.ui.theme.LocalDebuggerColors

@Composable
fun TimelineScreen() {
    val colors = LocalDebuggerColors.current
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Timeline (Stub)", color = colors.onSurface)
    }
}
"""

files["src/main/kotlin/com/hari/networkdebugger/ui/screens/settings/SettingsScreen.kt"] = """
package com.hari.networkdebugger.ui.screens.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.hari.networkdebugger.ui.theme.LocalDebuggerColors

@Composable
fun SettingsScreen() {
    val colors = LocalDebuggerColors.current
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Settings (Stub)", color = colors.onSurface)
    }
}
"""

for path, content in files.items():
    write_file(path, content)
