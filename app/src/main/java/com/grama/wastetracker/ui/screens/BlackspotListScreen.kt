package com.grama.wastetracker.ui.screens

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.grama.wastetracker.R
import com.grama.wastetracker.data.model.BlackspotReport
import com.grama.wastetracker.ui.theme.*
import com.grama.wastetracker.ui.viewmodel.BlackspotViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlackspotListScreen(
    viewModel: BlackspotViewModel,
    userId: String,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.observeUserReports(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.blackspot_reports), fontWeight = FontWeight.Bold) },
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
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(BackgroundLight)) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = GreenPrimary
                )
            } else if (uiState.reports.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Filled.CheckCircle, null, Modifier.size(64.dp), tint = GreenPrimary.copy(alpha = 0.5f))
                    Spacer(Modifier.height(12.dp))
                    Text("No reports yet", style = MaterialTheme.typography.titleMedium, color = OnSurfaceVariantLight)
                    Text("Your blackspot reports will appear here", style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariantLight)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.reports) { report ->
                        BlackspotReportCard(report = report)
                    }
                }
            }
        }
    }
}

@Composable
fun BlackspotReportCard(report: BlackspotReport) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    val isPending = report.status == BlackspotReport.STATUS_PENDING

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(dateFormat.format(Date(report.createdAt)), style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariantLight)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isPending) StatusPending.copy(alpha = 0.15f) else StatusResolved.copy(alpha = 0.15f)
                ) {
                    Text(
                        if (isPending) "Pending" else "Resolved",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isPending) StatusPending else StatusResolved
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            if (report.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = report.imageUrl,
                    contentDescription = "Report image",
                    modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(10.dp))
            }

            Text(report.description, style = MaterialTheme.typography.bodyMedium, color = OnSurfaceLight)

            Spacer(Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.LocationOn, null, Modifier.size(14.dp), tint = RedError)
                Spacer(Modifier.width(4.dp))
                Text(
                    "%.4f, %.4f".format(report.latitude, report.longitude),
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariantLight
                )
            }
        }
    }
}
