package com.hari.networkdebugger.ui.screens.mocks

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hari.networkdebugger.core.model.HttpMethod
import com.hari.networkdebugger.core.model.MockRule
import com.hari.networkdebugger.ui.components.EmptyState
import com.hari.networkdebugger.ui.components.MethodBadge
import com.hari.networkdebugger.ui.components.StatusBadge
import com.hari.networkdebugger.ui.theme.LocalDebuggerColors
import java.util.UUID

/**
 * Screen displaying the Mock Rules list and rule creation/editing tools.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MockRulesScreen(
    modifier: Modifier = Modifier,
    viewModel: MockRulesViewModel = viewModel()
) {
    val colors = LocalDebuggerColors.current
    val rules by viewModel.rules.collectAsState()
    val capturedPaths by viewModel.capturedPaths.collectAsState()
    val globalMocksEnabled by viewModel.mockingEnabled.collectAsState()

    var showEditorDialog by remember { mutableStateOf<MockRule?>(null) }
    var isNewRule by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(colors.surface)) {
                TopAppBar(
                    title = { Text(text = "Mock Rules", fontWeight = FontWeight.Bold, color = colors.onSurface) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = colors.surface),
                    windowInsets = WindowInsets.statusBars
                )
                
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .background(colors.surfaceVariant, shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Enable Network Mocking",
                            color = colors.onSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Bypass all mocks when disabled",
                            color = colors.onSurfaceVariant,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Switch(
                        checked = globalMocksEnabled,
                        onCheckedChange = { viewModel.setMockingEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = colors.primary,
                            checkedTrackColor = colors.primary.copy(alpha = 0.5f),
                            uncheckedThumbColor = colors.onSurfaceVariant,
                            uncheckedTrackColor = colors.surfaceContainer
                        ),
                        modifier = Modifier.scale(0.85f)
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                if (!globalMocksEnabled) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .border(1.dp, colors.status5xx, RoundedCornerShape(8.dp))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Mocking disabled",
                                tint = colors.status5xx
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Mocking is disabled – all mock rules are bypassed.",
                                color = colors.status5xx,
                                style = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                            )
                        }
                    }
                }
                HorizontalDivider(color = colors.outline.copy(alpha = 0.5f), thickness = 0.5.dp)
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showEditorDialog = MockRule(
                        id = UUID.randomUUID().toString(),
                        pathPattern = "",
                        method = HttpMethod.GET,
                        statusCode = 200,
                        responseBody = "{\n  \"message\": \"Success\"\n}",
                        enabled = true
                    )
                    isNewRule = true
                },
                containerColor = colors.primary,
                contentColor = colors.surface
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Mock Rule")
            }
        },
        containerColor = colors.surface,
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (rules.isEmpty()) {
                EmptyState(
                    title = "No active mocks",
                    subtitle = "Tap '+' to define an HTTP endpoint override rule."
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = WindowInsets.statusBars.asPaddingValues()
                ) {
                    items(rules, key = { it.id }) { rule ->
                        MockRuleCard(
                            rule = rule,
                            isGlobalEnabled = globalMocksEnabled,
                            onToggle = { enabled -> viewModel.toggleRule(rule.id, enabled) },
                            onDelete = { viewModel.removeRule(rule.id) },
                            onClick = {
                                showEditorDialog = rule
                                isNewRule = false
                            }
                        )
                    }
                }
            }

            // Mock Editor Dialog
            showEditorDialog?.let { rule ->
                MockRuleEditorDialog(
                    rule = rule,
                    isNew = isNewRule,
                    capturedPaths = capturedPaths,
                    onDismiss = { showEditorDialog = null },
                    onSave = { updatedRule ->
                        if (isNewRule) {
                            viewModel.addRule(updatedRule)
                        } else {
                            viewModel.updateRule(updatedRule)
                        }
                        showEditorDialog = null
                    }
                )
            }
        }
    }
}

@Composable
private fun MockRuleCard(
    rule: MockRule,
    isGlobalEnabled: Boolean,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    val colors = LocalDebuggerColors.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .alpha(if (isGlobalEnabled) 1f else 0.5f)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    MethodBadge(method = rule.method)
                    Spacer(modifier = Modifier.width(8.dp))
                    StatusBadge(statusCode = rule.statusCode)
                    if (rule.delayMs > 0L) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${rule.delayMs}ms delay",
                            color = colors.onSurfaceVariant.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = rule.pathPattern,
                    color = colors.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Controls: Enable Switch and Delete
            Switch(
                checked = rule.enabled,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = colors.primary,
                    checkedTrackColor = colors.primary.copy(alpha = 0.5f),
                    uncheckedThumbColor = colors.onSurfaceVariant,
                    uncheckedTrackColor = colors.surfaceContainer
                ),
                modifier = Modifier.scale(0.85f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Mock Rule",
                    tint = colors.status4xx
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MockRuleEditorDialog(
    rule: MockRule,
    isNew: Boolean,
    capturedPaths: List<String>,
    onDismiss: () -> Unit,
    onSave: (MockRule) -> Unit
) {
    val colors = LocalDebuggerColors.current

    var pathPattern by remember { mutableStateOf(rule.pathPattern) }
    var selectedMethod by remember { mutableStateOf(rule.method) }
    var statusCodeStr by remember { mutableStateOf(rule.statusCode.toString()) }
    var delayStr by remember { mutableStateOf(rule.delayMs.toString()) }
    var responseBody by remember { mutableStateOf(rule.responseBody) }
    var enabled by remember { mutableStateOf(rule.enabled) }

    var dropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isNew) "Add Mock Rule" else "Edit Mock Rule",
                color = colors.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Method selection chips
                Text(
                    text = "HTTP METHOD",
                    color = colors.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val methods = listOf(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE, HttpMethod.PATCH)
                    methods.forEach { method ->
                        val isSelected = selectedMethod == method
                        val mColor = colors.methodColor(method)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) mColor.copy(alpha = 0.25f) else colors.surfaceContainer,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, mColor) else null,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedMethod = method }
                                .padding(vertical = 4.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = method.name,
                                    color = if (isSelected) mColor else colors.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // Path pattern with Autocomplete Dropdown
                Text(
                    text = "PATH MATCH",
                    color = colors.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = pathPattern,
                        onValueChange = { pathPattern = it },
                        label = { Text("Path Match (e.g. /v1/users)") },
                        singleLine = true,
                        trailingIcon = {
                            if (capturedPaths.isNotEmpty()) {
                                IconButton(onClick = { dropdownExpanded = !dropdownExpanded }) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = "Show Captured Paths",
                                        tint = colors.primary
                                    )
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.outline,
                            focusedLabelColor = colors.primary,
                            unfocusedLabelColor = colors.onSurfaceVariant,
                            focusedTextColor = colors.onSurface,
                            unfocusedTextColor = colors.onSurface
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    DropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .background(colors.surfaceVariant)
                    ) {
                        capturedPaths.forEach { path ->
                            DropdownMenuItem(
                                text = { Text(text = path, color = colors.onSurface, fontSize = 13.sp) },
                                onClick = {
                                    pathPattern = path
                                    dropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Status Code Presets Row
                Text(
                    text = "COMMON STATUS CODES",
                    color = colors.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val presets = listOf(200, 201, 400, 401, 403, 404, 500)
                    presets.forEach { code ->
                        val isSelected = statusCodeStr == code.toString()
                        val badgeColor = colors.statusColor(code)
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) badgeColor.copy(alpha = 0.25f) else colors.surfaceContainer,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, badgeColor) else null,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { statusCodeStr = code.toString() }
                                .padding(vertical = 2.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = code.toString(),
                                    color = if (isSelected) badgeColor else colors.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                            }
                        }
                    }
                }

                // Row for Status Code Input & Delay
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = statusCodeStr,
                        onValueChange = { statusCodeStr = it },
                        label = { Text("Status Code") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.outline,
                            focusedLabelColor = colors.primary,
                            unfocusedLabelColor = colors.onSurfaceVariant,
                            focusedTextColor = colors.onSurface,
                            unfocusedTextColor = colors.onSurface
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = delayStr,
                        onValueChange = { delayStr = it },
                        label = { Text("Delay (ms)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colors.primary,
                            unfocusedBorderColor = colors.outline,
                            focusedLabelColor = colors.primary,
                            unfocusedLabelColor = colors.onSurfaceVariant,
                            focusedTextColor = colors.onSurface,
                            unfocusedTextColor = colors.onSurface
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                // Response body payload
                Text(
                    text = "RESPONSE PAYLOAD (JSON/TEXT)",
                    color = colors.onSurfaceVariant,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = responseBody,
                    onValueChange = { responseBody = it },
                    singleLine = false,
                    maxLines = 6,
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.primary,
                        unfocusedBorderColor = colors.outline,
                        focusedTextColor = colors.onSurface,
                        unfocusedTextColor = colors.onSurface
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                )

                // Enabled switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Enable Rule Immediately",
                        color = colors.onSurface,
                        fontSize = 14.sp
                    )
                    Switch(
                        checked = enabled,
                        onCheckedChange = { enabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = colors.primary,
                            checkedTrackColor = colors.primary.copy(alpha = 0.5f),
                            uncheckedThumbColor = colors.onSurfaceVariant,
                            uncheckedTrackColor = colors.surfaceContainer
                        )
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (pathPattern.isNotBlank()) {
                        val finalStatus = statusCodeStr.toIntOrNull() ?: 200
                        val finalDelay = delayStr.toLongOrNull() ?: 0L
                        onSave(
                            rule.copy(
                                pathPattern = pathPattern.trim(),
                                method = selectedMethod,
                                statusCode = finalStatus,
                                responseBody = responseBody,
                                delayMs = finalDelay,
                                enabled = enabled
                            )
                        )
                    }
                }
            ) {
                Text("SAVE", color = colors.primary, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = colors.onSurfaceVariant)
            }
        },
        containerColor = colors.surfaceVariant
    )
}
