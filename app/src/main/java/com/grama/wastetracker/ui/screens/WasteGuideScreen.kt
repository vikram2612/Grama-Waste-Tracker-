package com.grama.wastetracker.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.grama.wastetracker.R
import com.grama.wastetracker.data.model.WasteGuideItem
import com.grama.wastetracker.ui.theme.*
import com.grama.wastetracker.ui.viewmodel.WasteGuideViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WasteGuideScreen(
    viewModel: WasteGuideViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.waste_guide), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GreenPrimary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
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
            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GreenPrimary)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.guides) { guide ->
                        WasteGuideCard(guide = guide)
                    }
                }
            }
        }
    }
}

@Composable
fun WasteGuideCard(guide: WasteGuideItem) {
    var expanded by remember { mutableStateOf(false) }

    val (color, icon) = when (guide.category) {
        "dry" -> DryWasteColor to Icons.Filled.Recycling
        "wet" -> WetWasteColor to Icons.Filled.Compost
        "hazardous" -> HazardousWasteColor to Icons.Filled.Warning
        else -> GreenPrimary to Icons.Filled.Delete
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = !expanded },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = color.copy(alpha = 0.12f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, null, Modifier.size(28.dp), tint = color)
                    }
                }
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        guide.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = color
                    )
                    Text(
                        guide.titleKn,
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceLight
                    )
                }
                Icon(
                    if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    null, tint = OnSurfaceLight
                )
            }

            Spacer(Modifier.height(12.dp))
            Text(guide.description, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceLight)

            // Expandable content
            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    HorizontalDivider(color = OutlineLight)
                    Spacer(Modifier.height(12.dp))

                    Text("Items:", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))

                    guide.items.forEachIndexed { index, item ->
                        Row(
                            modifier = Modifier.padding(vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = color.copy(alpha = 0.2f),
                                modifier = Modifier.size(8.dp)
                            ) {}
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(item, style = MaterialTheme.typography.bodyMedium)
                                if (index < guide.itemsKn.size) {
                                    Text(guide.itemsKn[index], style = MaterialTheme.typography.bodySmall, color = OnSurfaceLight)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Disposal Tip
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f))
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                            Icon(Icons.Filled.Lightbulb, null, Modifier.size(20.dp), tint = color)
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text("Disposal Tip", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = color)
                                Spacer(Modifier.height(4.dp))
                                Text(guide.disposalTip, style = MaterialTheme.typography.bodySmall, color = OnSurfaceLight)
                                Text(guide.disposalTipKn, style = MaterialTheme.typography.bodySmall, color = OnSurfaceLight)
                            }
                        }
                    }
                }
            }
        }
    }
}
