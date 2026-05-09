package com.grama.wastetracker.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.grama.wastetracker.R
import com.grama.wastetracker.data.model.ChatMessage
import com.grama.wastetracker.ui.theme.*
import com.grama.wastetracker.ui.viewmodel.AiAssistantViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiAssistantScreen(
    viewModel: AiAssistantViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var inputText by remember { mutableStateOf("") }
    var classifyText by remember { mutableStateOf("") }
    var showClassifier by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll to bottom
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.ai_assistant), fontWeight = FontWeight.Bold)
                        Text("Waste disposal guidance", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.7f))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back") }
                },
                actions = {
                    IconButton(onClick = { showClassifier = !showClassifier }) {
                        Icon(
                            if (showClassifier) Icons.Filled.Chat else Icons.Filled.Category,
                            "Toggle mode"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BluePrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight)
        ) {
            if (showClassifier) {
                // Waste Classifier Mode
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Filled.Category, null, Modifier.size(48.dp), tint = BluePrimary)
                            Spacer(Modifier.height(12.dp))
                            Text(stringResource(R.string.classify_waste), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Describe the waste item and AI will classify it",
                                style = MaterialTheme.typography.bodyMedium, color = OnSurfaceVariantLight
                            )
                            Spacer(Modifier.height(16.dp))

                            OutlinedTextField(
                                value = classifyText,
                                onValueChange = { classifyText = it },
                                placeholder = { Text("e.g. plastic bottle, banana peel, old battery...") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BluePrimary, cursorColor = BluePrimary)
                            )
                            Spacer(Modifier.height(16.dp))

                            Button(
                                onClick = { viewModel.classifyWaste(classifyText) },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BluePrimary),
                                enabled = classifyText.isNotBlank() && !uiState.isThinking
                            ) {
                                if (uiState.isThinking) {
                                    CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                                    Spacer(Modifier.width(8.dp))
                                }
                                Text(stringResource(R.string.classify_waste))
                            }
                        }
                    }

                    // Classification Result
                    if (uiState.classification != null) {
                        val c = uiState.classification!!
                        val (color, label) = when (c.category) {
                            "dry" -> DryWasteColor to "Dry Waste ♻️"
                            "wet" -> WetWasteColor to "Wet Waste 🌿"
                            "hazardous" -> HazardousWasteColor to "Hazardous ⚠️"
                            else -> GreenPrimary to "Unknown"
                        }

                        Spacer(Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
                            border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(shape = CircleShape, color = color.copy(alpha = 0.15f), modifier = Modifier.size(40.dp)) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(Icons.Filled.CheckCircle, null, Modifier.size(24.dp), tint = color)
                                        }
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = color)
                                        Text("Confidence: ${(c.confidence * 100).toInt()}%", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariantLight)
                                    }
                                }
                                Spacer(Modifier.height(12.dp))
                                Text(c.explanation, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceLight)
                                Spacer(Modifier.height(12.dp))
                                HorizontalDivider(color = color.copy(alpha = 0.2f))
                                Spacer(Modifier.height(12.dp))
                                Text("Disposal Advice:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = color)
                                Spacer(Modifier.height(4.dp))
                                Text(c.disposalAdvice, style = MaterialTheme.typography.bodySmall, color = OnSurfaceLight)
                            }
                        }
                    }
                }
            } else {
                // Chat Mode
                if (uiState.messages.isEmpty()) {
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                            Icon(Icons.Filled.SmartToy, null, Modifier.size(64.dp), tint = BluePrimary.copy(alpha = 0.4f))
                            Spacer(Modifier.height(16.dp))
                            Text("Ask me about waste disposal!", style = MaterialTheme.typography.titleMedium, color = OnSurfaceVariantLight)
                            Spacer(Modifier.height(8.dp))
                            Text("I can help with segregation, composting, recycling & more",
                                style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariantLight,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        state = listState,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.messages) { message ->
                            ChatBubble(message = message)
                        }
                        if (uiState.isThinking) {
                            item {
                                Row(modifier = Modifier.padding(8.dp)) {
                                    CircularProgressIndicator(Modifier.size(16.dp), color = BluePrimary, strokeWidth = 2.dp)
                                    Spacer(Modifier.width(8.dp))
                                    Text(stringResource(R.string.ai_thinking), style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariantLight)
                                }
                            }
                        }
                    }
                }

                // Chat Input
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White,
                    shadowElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = inputText,
                            onValueChange = { inputText = it },
                            placeholder = { Text(stringResource(R.string.ask_question)) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(24.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BluePrimary,
                                cursorColor = BluePrimary
                            ),
                            singleLine = true
                        )
                        Spacer(Modifier.width(8.dp))
                        FilledIconButton(
                            onClick = {
                                if (inputText.isNotBlank()) {
                                    viewModel.sendMessage(inputText)
                                    inputText = ""
                                }
                            },
                            colors = IconButtonDefaults.filledIconButtonColors(containerColor = BluePrimary)
                        ) {
                            Icon(Icons.Filled.Send, "Send", tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.isFromUser
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Surface(shape = CircleShape, color = BluePrimary.copy(alpha = 0.1f), modifier = Modifier.size(32.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.SmartToy, null, Modifier.size(18.dp), tint = BluePrimary)
                }
            }
            Spacer(Modifier.width(8.dp))
        }

        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp, topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) GreenPrimary else Color.White,
            shadowElevation = if (isUser) 0.dp else 2.dp,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = if (isUser) Color.White else OnSurfaceLight
            )
        }
    }
}
