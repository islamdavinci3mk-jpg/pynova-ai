package com.example.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ChatInputBar
import com.example.ui.components.ClearConfirmDialog
import com.example.ui.components.CreatorStatusBanner
import com.example.ui.components.MessageBubble
import com.example.ui.components.PynovaTopBar
import com.example.ui.components.QuickPromptsRow
import com.example.ui.components.SettingsDialog
import com.example.ui.components.TypingIndicator
import com.example.ui.theme.PynovaCardBackground
import com.example.ui.theme.PynovaCreatorGold
import com.example.ui.theme.PynovaCyan
import com.example.ui.theme.PynovaObsidian
import com.example.ui.theme.PynovaPurple
import com.example.ui.theme.PynovaTextMuted
import com.example.ui.theme.PynovaTextPrimary
import kotlinx.coroutines.launch

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val messages by viewModel.messages.collectAsStateWithLifecycle()
    val isThinking by viewModel.isThinking.collectAsStateWithLifecycle()
    val isCreatorMode by viewModel.isCreatorMode.collectAsStateWithLifecycle()
    val showSettings by viewModel.showSettingsDialog.collectAsStateWithLifecycle()
    val showClearConfirm by viewModel.showClearConfirmDialog.collectAsStateWithLifecycle()
    val customApiKey by viewModel.customApiKey.collectAsStateWithLifecycle()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Handle toast events
    LaunchedEffect(Unit) {
        viewModel.toastEvent.collect { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    // Scroll to bottom when messages update
    LaunchedEffect(messages.size, isThinking) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(PynovaObsidian)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
        topBar = {
            PynovaTopBar(
                isCreatorMode = isCreatorMode,
                onClearClick = { viewModel.openClearConfirmDialog() },
                onSettingsClick = { viewModel.openSettingsDialog() },
                onCreatorBadgeClick = {
                    Toast.makeText(context, "👑 وضع الصانع الأعلى: حسام", Toast.LENGTH_SHORT).show()
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(PynovaObsidian)
        ) {
            // Creator Banner
            AnimatedVisibility(
                visible = isCreatorMode,
                enter = fadeIn() + slideInVertically(),
                exit = fadeOut() + slideOutVertically()
            ) {
                CreatorStatusBanner(
                    onDismiss = {}
                )
            }

            // Messages List
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                if (messages.isEmpty()) {
                    // Empty state
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(PynovaCyan, PynovaPurple))
                                )
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                                    .background(PynovaCardBackground),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    tint = PynovaCyan,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "مرحباً بك في Pynova AI",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = PynovaTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "نظام ذكاء اصطناعي فخم جاهز لمساعدتك في كل ما تحتاج.",
                            fontSize = 13.sp,
                            color = PynovaTextMuted
                        )
                    }
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 12.dp)
                    ) {
                        items(
                            items = messages,
                            key = { it.id }
                        ) { msg ->
                            if (msg.isThinking) {
                                TypingIndicator()
                            } else {
                                MessageBubble(
                                    message = msg,
                                    onRetry = { viewModel.retryLastMessage() }
                                )
                            }
                        }
                    }
                }
            }

            // Quick Prompt Chips
            QuickPromptsRow(
                isCreatorMode = isCreatorMode,
                onPromptSelected = { selected ->
                    // Auto-fill or send prompt
                    inputText = selected
                }
            )

            // Bottom Input Bar
            ChatInputBar(
                text = inputText,
                onTextChanged = { inputText = it },
                onSendClicked = {
                    val toSend = inputText
                    inputText = ""
                    viewModel.sendMessage(toSend)
                    coroutineScope.launch {
                        if (messages.isNotEmpty()) {
                            listState.animateScrollToItem(messages.size - 1)
                        }
                    }
                },
                isThinking = isThinking
            )
        }
    }

    // Settings Dialog
    if (showSettings) {
        SettingsDialog(
            currentApiKey = customApiKey,
            isCreatorMode = isCreatorMode,
            onSaveApiKey = { newKey -> viewModel.saveApiKey(newKey) },
            onToggleCreatorMode = { viewModel.toggleCreatorMode() },
            onDismiss = { viewModel.closeSettingsDialog() }
        )
    }

    // Clear Chat Dialog
    if (showClearConfirm) {
        ClearConfirmDialog(
            onConfirm = { viewModel.clearChat() },
            onDismiss = { viewModel.closeClearConfirmDialog() }
        )
    }
}
