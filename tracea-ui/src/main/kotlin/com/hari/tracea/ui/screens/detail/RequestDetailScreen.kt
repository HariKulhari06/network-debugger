package com.hari.tracea.ui.screens.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hari.tracea.core.model.BodyData
import com.hari.tracea.core.util.DurationFormatter
import com.hari.tracea.core.util.SizeFormatter
import com.hari.tracea.ui.components.MethodBadge
import com.hari.tracea.ui.components.StatusBadge
import com.hari.tracea.ui.components.SummaryCardsRow
import com.hari.tracea.ui.screens.detail.tabs.OverviewTab
import com.hari.tracea.ui.screens.detail.tabs.RequestTab
import com.hari.tracea.ui.screens.detail.tabs.ResponseTab
import com.hari.tracea.ui.screens.detail.tabs.TimingTab
import com.hari.tracea.ui.theme.LocalDebuggerColors
import com.hari.tracea.ui.util.ShareUtility
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestDetailScreen(
    eventId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RequestDetailViewModel = viewModel()
) {
    val colors = LocalDebuggerColors.current
    val event by viewModel.event.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val responseBodyMode by viewModel.responseBodyMode.collectAsState()
    val requestBodyMode by viewModel.requestBodyMode.collectAsState()
    
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var showShareMenu by remember { mutableStateOf(false) }

    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    val currentEvent = event

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Transaction Details", 
                        color = colors.onSurface, 
                        fontSize = 18.sp, 
                        fontWeight = FontWeight.Bold 
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Back", 
                            tint = colors.onSurface
                        )
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showShareMenu = true }) {
                            Icon(
                                imageVector = Icons.Default.Share, 
                                contentDescription = "Share", 
                                tint = colors.onSurface
                            )
                        }
                        
                        DropdownMenu(
                            expanded = showShareMenu,
                            onDismissRequest = { showShareMenu = false },
                            modifier = Modifier.background(colors.surfaceVariant)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Copy as cURL", color = colors.onSurface) },
                                leadingIcon = { Icon(Icons.Default.Code, contentDescription = null, tint = colors.primary) },
                                onClick = {
                                    showShareMenu = false
                                    val curl = viewModel.getCurlCommand()
                                    clipboardManager.setText(AnnotatedString(curl))
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Share Full Report", color = colors.onSurface) },
                                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null, tint = colors.primary) },
                                onClick = {
                                    showShareMenu = false
                                    currentEvent?.let {
                                        val report = ShareUtility.generateFullReport(it)
                                        ShareUtility.shareText(context, report)
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Share Response Body", color = colors.onSurface) },
                                leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null, tint = colors.primary) },
                                onClick = {
                                    showShareMenu = false
                                    currentEvent?.let { event ->
                                        val body = event.responseBody
                                        if (body is BodyData.Text) {
                                            val extension = if (event.responseContentType?.contains("json", true) == true) "json" else "txt"
                                            ShareUtility.shareFile(
                                                context = context,
                                                content = body.content,
                                                fileName = "response_body_${event.id}.$extension",
                                                title = "Share Response Body"
                                            )
                                        }
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Share as HAR", color = colors.onSurface) },
                                leadingIcon = { Icon(Icons.Default.Share, contentDescription = null, tint = colors.primary) },
                                onClick = {
                                    showShareMenu = false
                                    currentEvent?.let {
                                        val har = ShareUtility.generateHar(it)
                                        ShareUtility.shareFile(
                                            context = context,
                                            content = har,
                                            fileName = "transaction_${it.id}.har",
                                            title = "Share as HAR"
                                        )
                                    }
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.surface)
            )
        },
        containerColor = colors.surface,
        modifier = modifier
    ) { paddingValues ->
        if (currentEvent == null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues), 
                contentAlignment = Alignment.Center
            ) {
                Text("Loading details...", color = colors.onSurfaceVariant)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Header section: Method + Path + Status Badge
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.surface)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MethodBadge(method = currentEvent.method)
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Text(
                            text = currentEvent.path,
                            color = colors.onSurface,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        currentEvent.statusCode?.let { code ->
                            StatusBadge(
                                statusCode = code, 
                                statusMessage = currentEvent.statusMessage, 
                                showMessage = true
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = currentEvent.url,
                        color = colors.onSurfaceVariant,
                        fontSize = 12.sp,
                        maxLines = 2,
                        lineHeight = 16.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                HorizontalDivider(color = colors.outline.copy(alpha = 0.5f), thickness = 0.5.dp)

                // Summary cards row
                val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                SummaryCardsRow(
                    status = currentEvent.statusCode?.toString() ?: "N/A",
                    duration = currentEvent.timing.totalMs?.let { DurationFormatter.format(it) } ?: "N/A",
                    size = SizeFormatter.format(currentEvent.requestSize + currentEvent.responseSize),
                    time = timeFormat.format(Date(currentEvent.timestamp)),
                    modifier = Modifier.padding(16.dp)
                )

                // Tabs
                TabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    containerColor = colors.surface,
                    contentColor = colors.primary,
                    divider = {
                        HorizontalDivider(color = colors.outline.copy(alpha = 0.5f), thickness = 0.5.dp)
                    },
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab.ordinal]),
                            color = colors.primary,
                            height = 3.dp
                        )
                    }
                ) {
                    DetailTab.entries.forEach { tab ->
                        Tab(
                            selected = selectedTab == tab,
                            onClick = { viewModel.selectTab(tab) },
                            text = {
                                Text(
                                    text = tab.label,
                                    fontSize = 10.sp,
                                    fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Medium,
                                    letterSpacing = 0.5.sp
                                )
                            },
                            selectedContentColor = colors.primary,
                            unselectedContentColor = colors.onSurfaceVariant
                        )
                    }
                }

                // Tab Content
                Box(modifier = Modifier.weight(1f)) {
                    when (selectedTab) {
                        DetailTab.OVERVIEW -> OverviewTab(
                            event = currentEvent,
                            responseBodyMode = responseBodyMode,
                            onResponseBodyModeChange = { viewModel.setResponseBodyMode(it) }
                        )
                        DetailTab.REQUEST -> RequestTab(
                            event = currentEvent,
                            requestBodyMode = requestBodyMode,
                            onRequestBodyModeChange = { viewModel.setRequestBodyMode(it) }
                        )
                        DetailTab.RESPONSE -> ResponseTab(
                            event = currentEvent,
                            responseBodyMode = responseBodyMode,
                            onResponseBodyModeChange = { viewModel.setResponseBodyMode(it) }
                        )
                        DetailTab.TIMING -> TimingTab(
                            event = currentEvent
                        )
                    }
                }
            }
        }
    }
}
