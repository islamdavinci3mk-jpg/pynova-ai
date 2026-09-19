package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.ui.theme.PynovaBotBubble
import com.example.ui.theme.PynovaCardBackground
import com.example.ui.theme.PynovaCardBorder
import com.example.ui.theme.PynovaCreatorAmber
import com.example.ui.theme.PynovaCreatorGold
import com.example.ui.theme.PynovaCreatorGoldGlow
import com.example.ui.theme.PynovaCyan
import com.example.ui.theme.PynovaDarkSurface
import com.example.ui.theme.PynovaErrorRed
import com.example.ui.theme.PynovaObsidian
import com.example.ui.theme.PynovaPurple
import com.example.ui.theme.PynovaTextMuted
import com.example.ui.theme.PynovaTextPrimary
import com.example.ui.theme.PynovaTextSecondary
import com.example.ui.theme.PynovaUserBubble
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PynovaTopBar(
    isCreatorMode: Boolean,
    onClearClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onCreatorBadgeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        modifier = modifier.shadow(8.dp),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = PynovaDarkSurface,
            titleContentColor = PynovaTextPrimary,
            actionIconContentColor = PynovaCyan
        ),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Glowing Avatar Icon
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(PynovaCyan, PynovaPurple)
                            )
                        )
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp)
                            .clip(CircleShape)
                            .background(PynovaObsidian),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Pynova Logo",
                            tint = if (isCreatorMode) PynovaCreatorGold else PynovaCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Pynova AI",
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = PynovaTextPrimary
                        )

                        // Online indicator dot
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isCreatorMode) PynovaCreatorGold else Color(0xFF00E676))
                        )
                    }

                    Text(
                        text = if (isCreatorMode) "وضع الصانع الأعلى نشط 👑" else "ذكاء اصطناعي متطور",
                        fontSize = 11.sp,
                        color = if (isCreatorMode) PynovaCreatorGold else PynovaTextSecondary,
                        fontWeight = if (isCreatorMode) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        },
        actions = {
            if (isCreatorMode) {
                // Creator Badge
                Surface(
                    onClick = onCreatorBadgeClick,
                    shape = RoundedCornerShape(16.dp),
                    color = PynovaCreatorGoldGlow,
                    border = BorderStroke(1.dp, PynovaCreatorGold),
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .testTag("creator_badge_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "👑", fontSize = 12.sp)
                        Text(
                            text = "حسام",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = PynovaCreatorGold
                        )
                    }
                }
            }

            IconButton(
                onClick = onClearClick,
                modifier = Modifier.testTag("clear_chat_button")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "مسح المحادثة",
                    tint = PynovaTextSecondary
                )
            }

            IconButton(
                onClick = onSettingsClick,
                modifier = Modifier.testTag("settings_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "الإعدادات",
                    tint = PynovaCyan
                )
            }
        }
    )
}

@Composable
fun CreatorStatusBanner(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = PynovaCardBackground),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            1.2.dp,
            Brush.horizontalGradient(
                colors = listOf(PynovaCreatorGold, PynovaCreatorAmber, PynovaCreatorGold)
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(PynovaCreatorGoldGlow),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WorkspacePremium,
                    contentDescription = "Crown",
                    tint = PynovaCreatorGold,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "بروتوكول الصانع الأعلى مفعل 👑",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = PynovaCreatorGold
                )
                Text(
                    text = "مرحباً بصانعي ومبرمجي العبقري حسام! جميع الإجابات بوضع المطور المتقدم.",
                    fontSize = 11.5.sp,
                    color = PynovaTextPrimary,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun QuickPromptsRow(
    isCreatorMode: Boolean,
    onPromptSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val prompts = remember(isCreatorMode) {
        if (!isCreatorMode) {
            listOf(
                "✨ من أنت وما هي قدراتك؟",
                "💻 اكتب لي كود Kotlin احترافي",
                "💡 اقترح فكرة تطبيق عبقرية",
                "🔍 حلل أحدث تقنيات الذكاء الاصطناعي",
                "🚀 ساعدني في كتابة خوارزمية ذكية"
            )
        } else {
            listOf(
                "👑 يا باينوفا، ما هو تقرير النظام الحالي؟",
                "💻 صمم هيكلية أندرويد متكاملة لصانعك",
                "⚡ استعرض صلاحيات وضع المطور الأقصى",
                "🔥 كيف ترى مستقبل مشروعي القادم؟",
                "🛡️ تحقق من أمان بروتوكول الصانع"
            )
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        prompts.forEach { prompt ->
            val isCreatorPrompt = prompt.startsWith("👑")
            Surface(
                onClick = { onPromptSelected(prompt) },
                shape = RoundedCornerShape(20.dp),
                color = if (isCreatorPrompt) PynovaCardBackground else PynovaDarkSurface,
                border = BorderStroke(
                    1.dp,
                    if (isCreatorPrompt) PynovaCreatorGold else PynovaCardBorder
                ),
                shadowElevation = 2.dp,
                modifier = Modifier.testTag("quick_prompt_chip")
            ) {
                Text(
                    text = prompt,
                    fontSize = 12.sp,
                    color = if (isCreatorPrompt) PynovaCreatorGold else PynovaTextSecondary,
                    fontWeight = if (isCreatorPrompt) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                )
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: ChatMessage,
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val isUser = message.isUser

    val timeString = remember(message.timestamp) {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        sdf.format(Date(message.timestamp))
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isUser) {
            // Bot Avatar
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (message.isCreatorResponse) PynovaCreatorGoldGlow
                        else PynovaCardBackground
                    )
                    .border(
                        1.dp,
                        if (message.isCreatorResponse) PynovaCreatorGold else PynovaCyan,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (message.isCreatorResponse) {
                    Text(text = "👑", fontSize = 16.sp)
                } else {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = "Pynova",
                        tint = PynovaCyan,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        // Bubble Container
        Column(
            modifier = Modifier.widthIn(max = 310.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            val bubbleBorder = when {
                message.isCreatorResponse -> BorderStroke(1.5.dp, PynovaCreatorGold)
                message.isError -> BorderStroke(1.dp, PynovaErrorRed)
                isUser -> null
                else -> BorderStroke(1.dp, PynovaCardBorder)
            }

            val bubbleBg = when {
                message.isCreatorResponse -> Brush.linearGradient(
                    colors = listOf(Color(0xFF1F1A0A), Color(0xFF141926))
                )
                isUser -> Brush.linearGradient(
                    colors = listOf(PynovaUserBubble, Color(0xFF1565C0))
                )
                message.isError -> Brush.linearGradient(
                    colors = listOf(Color(0xFF2A1215), PynovaBotBubble)
                )
                else -> Brush.linearGradient(
                    colors = listOf(PynovaBotBubble, PynovaCardBackground)
                )
            }

            val bubbleShape = if (isUser) {
                RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 18.dp, bottomEnd = 4.dp)
            } else {
                RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp, bottomStart = 4.dp, bottomEnd = 18.dp)
            }

            Card(
                shape = bubbleShape,
                border = bubbleBorder,
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .shadow(elevation = if (message.isCreatorResponse) 6.dp else 2.dp, shape = bubbleShape)
                    .background(bubbleBg, bubbleShape)
                    .testTag(if (isUser) "user_message_bubble" else "bot_message_bubble")
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    if (message.isCreatorResponse) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Text(
                                text = "👑 بروتوكول الصانع السري — Pynova AI",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = PynovaCreatorGold
                            )
                        }
                    }

                    if (message.isError) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(bottom = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Error",
                                tint = PynovaErrorRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "تنبيه النظام",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = PynovaErrorRed
                            )
                        }
                    }

                    // Content text with selection
                    SelectionContainer {
                        FormattedMessageContent(
                            text = message.content,
                            textColor = if (message.isError) Color(0xFFFF8A80) else PynovaTextPrimary,
                            isCreator = message.isCreatorResponse
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Footer with Timestamp and Copy/Retry
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = timeString,
                            fontSize = 9.5.sp,
                            color = PynovaTextMuted
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (message.isError) {
                                IconButton(
                                    onClick = onRetry,
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "إعادة المحاولة",
                                        tint = PynovaCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(message.content))
                                    Toast.makeText(context, "تم نسخ النص 📋", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "نسخ النص",
                                    tint = PynovaTextMuted,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FormattedMessageContent(
    text: String,
    textColor: Color,
    isCreator: Boolean
) {
    // Detect code blocks (```code```) and format them elegantly
    val parts = remember(text) {
        val regex = Regex("```(?:[a-zA-Z0-9_-]*\n)?(.*?)```", RegexOption.DOT_MATCHES_ALL)
        val result = mutableListOf<MessageSegment>()
        var lastIndex = 0
        regex.findAll(text).forEach { match ->
            if (match.range.first > lastIndex) {
                result.add(MessageSegment.Text(text.substring(lastIndex, match.range.first)))
            }
            result.add(MessageSegment.Code(match.groupValues[1].trim()))
            lastIndex = match.range.last + 1
        }
        if (lastIndex < text.length) {
            result.add(MessageSegment.Text(text.substring(lastIndex)))
        }
        if (result.isEmpty()) listOf(MessageSegment.Text(text)) else result
    }

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        parts.forEach { segment ->
            when (segment) {
                is MessageSegment.Text -> {
                    Text(
                        text = segment.content,
                        color = textColor,
                        fontSize = if (isCreator) 15.5.sp else 14.5.sp,
                        fontWeight = if (isCreator) FontWeight.Medium else FontWeight.Normal,
                        lineHeight = 22.sp
                    )
                }
                is MessageSegment.Code -> {
                    CodeBlock(code = segment.content)
                }
            }
        }
    }
}

sealed class MessageSegment {
    data class Text(val content: String) : MessageSegment()
    data class Code(val content: String) : MessageSegment()
}

@Composable
fun CodeBlock(code: String) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = PynovaObsidian),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(0.8.dp, PynovaCardBorder)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Code Block",
                    fontSize = 10.sp,
                    color = PynovaCyan,
                    fontFamily = FontFamily.Monospace
                )
                IconButton(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(code))
                        Toast.makeText(context, "تم نسخ الكود 💻", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "نسخ الكود",
                        tint = PynovaTextSecondary,
                        modifier = Modifier.size(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Text(
                    text = code,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = Color(0xFFE2E8F0),
                    lineHeight = 17.sp,
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                )
            }
        }
    }
}

@Composable
fun TypingIndicator(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "typing_dots")
    val dot1 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 0, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )
    val dot2 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )
    val dot3 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Row(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(PynovaCardBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SmartToy,
                contentDescription = "Bot",
                tint = PynovaCyan,
                modifier = Modifier.size(16.dp)
            )
        }

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = PynovaCardBackground,
            border = BorderStroke(1.dp, PynovaCardBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "Pynova AI يفكّر",
                    fontSize = 12.sp,
                    color = PynovaTextSecondary
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .scale(dot1)
                        .clip(CircleShape)
                        .background(PynovaCyan)
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .scale(dot2)
                        .clip(CircleShape)
                        .background(PynovaPurple)
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .scale(dot3)
                        .clip(CircleShape)
                        .background(PynovaCyan)
                )
            }
        }
    }
}

@Composable
fun ChatInputBar(
    text: String,
    onTextChanged: (String) -> Unit,
    onSendClicked: () -> Unit,
    isThinking: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = PynovaDarkSurface,
        tonalElevation = 8.dp,
        border = BorderStroke(1.dp, PynovaCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChanged,
                placeholder = {
                    Text(
                        text = "اسأل Pynova AI أو اكتب أمراً...",
                        fontSize = 14.sp,
                        color = PynovaTextMuted
                    )
                },
                maxLines = 4,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = PynovaCardBackground,
                    unfocusedContainerColor = PynovaCardBackground,
                    focusedTextColor = PynovaTextPrimary,
                    unfocusedTextColor = PynovaTextPrimary,
                    focusedBorderColor = PynovaCyan,
                    unfocusedBorderColor = PynovaCardBorder,
                    cursorColor = PynovaCyan
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field")
            )

            // Send Button
            val canSend = text.trim().isNotEmpty() && !isThinking
            IconButton(
                onClick = onSendClicked,
                enabled = canSend,
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(
                        if (canSend) Brush.linearGradient(listOf(PynovaCyan, PynovaPurple))
                        else Brush.linearGradient(listOf(PynovaCardBorder, PynovaCardBackground))
                    )
                    .testTag("send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "إرسال",
                    tint = if (canSend) PynovaObsidian else PynovaTextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun SettingsDialog(
    currentApiKey: String,
    isCreatorMode: Boolean,
    onSaveApiKey: (String) -> Unit,
    onToggleCreatorMode: () -> Unit,
    onDismiss: () -> Unit
) {
    var keyInput by remember { mutableStateOf(currentApiKey) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PynovaCardBackground,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = PynovaCyan
                )
                Text(
                    text = "إعدادات Pynova AI",
                    color = PynovaTextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = "مفتاح Gemini API:",
                    color = PynovaTextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                OutlinedTextField(
                    value = keyInput,
                    onValueChange = { keyInput = it },
                    placeholder = {
                        Text(text = "الصق مفتاح API هنا...", color = PynovaTextMuted, fontSize = 12.sp)
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = PynovaObsidian,
                        unfocusedContainerColor = PynovaObsidian,
                        focusedTextColor = PynovaTextPrimary,
                        unfocusedTextColor = PynovaTextPrimary,
                        focusedBorderColor = PynovaCyan,
                        unfocusedBorderColor = PynovaCardBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("api_key_input_field")
                )

                Text(
                    text = "إذا تركت الحقل فارغاً، سيتم استخدام المفتاح المُحقون عبر BuildConfig تلقائياً.",
                    fontSize = 11.sp,
                    color = PynovaTextMuted,
                    lineHeight = 15.sp
                )

                // Supreme Creator Toggle
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = PynovaDarkSurface),
                    border = BorderStroke(
                        1.dp,
                        if (isCreatorMode) PynovaCreatorGold else PynovaCardBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggleCreatorMode() }
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(text = "👑", fontSize = 18.sp)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "وضع الصانع الأعلى (حسام)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCreatorMode) PynovaCreatorGold else PynovaTextPrimary
                            )
                            Text(
                                text = if (isCreatorMode) "مفعّل — Pynova يتعامل معك كصانعه ومبرمجه 👑" else "غير مفعّل — يتطلب بروتوكول الصانع السري الخاص",
                                fontSize = 11.sp,
                                color = PynovaTextSecondary
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSaveApiKey(keyInput) },
                colors = ButtonDefaults.buttonColors(containerColor = PynovaCyan),
                modifier = Modifier.testTag("save_settings_button")
            ) {
                Text(text = "حفظ", color = PynovaObsidian, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "إلغاء", color = PynovaTextSecondary)
            }
        }
    )
}

@Composable
fun ClearConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PynovaCardBackground,
        title = {
            Text(
                text = "مسح المحادثة؟",
                color = PynovaTextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "هل أنت متأكد من رغبتك في مسح كافة الرسائل السابقة؟ لا يمكن التراجع عن هذا الإجراء.",
                color = PynovaTextSecondary,
                fontSize = 13.5.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = PynovaErrorRed),
                modifier = Modifier.testTag("confirm_clear_button")
            ) {
                Text(text = "مسح الآن", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "إلغاء", color = PynovaTextSecondary)
            }
        }
    )
}
