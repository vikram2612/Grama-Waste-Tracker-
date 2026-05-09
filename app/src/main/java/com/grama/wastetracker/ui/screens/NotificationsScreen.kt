package com.grama.wastetracker.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.grama.wastetracker.R
import com.grama.wastetracker.data.model.AppNotification
import com.grama.wastetracker.ui.theme.*
import com.grama.wastetracker.ui.viewmodel.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    userId: String,
    onBack: () -> Unit
) {
    // Since we don't have a dedicated NotificationViewModel, show placeholder
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.notifications), fontWeight = FontWeight.Bold) },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(BackgroundLight),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Filled.NotificationsNone, null, Modifier.size(64.dp), tint = GreenPrimary.copy(alpha = 0.4f))
                Spacer(Modifier.height(12.dp))
                Text("No notifications yet", style = MaterialTheme.typography.titleMedium, color = OnSurfaceVariantLight)
                Text("You'll be notified when the tractor is nearby", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariantLight)
            }
        }
    }
}
