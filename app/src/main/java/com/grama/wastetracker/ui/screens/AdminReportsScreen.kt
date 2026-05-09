package com.grama.wastetracker.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.grama.wastetracker.ui.viewmodel.AdminViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminReportsScreen(
    viewModel: AdminViewModel,
    adminId: String,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var filterTab by remember { mutableIntStateOf(0) }

    val filteredReports = when (filterTab) {
        1 -> uiState.reports.filter { it.status == BlackspotReport.STATUS_PENDING }
        2 -> uiState.reports.filter { it.status == BlackspotReport.STATUS_RESOLVED }
        else -> uiState.reports
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.blackspot_reports), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GreenPrimaryDark,
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
            // Filter Tabs
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = filterTab == 0,
                        onClick = { filterTab = 0 },
                        label = { Text("All (${uiState.totalReports})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GreenPrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = filterTab == 1,
                        onClick = { filterTab = 1 },
                        label = { Text("Pending (${uiState.pendingCount})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = StatusPending,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = filterTab == 2,
                        onClick = { filterTab = 2 },
                        label = { Text("Resolved (${uiState.resolvedCount})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = StatusResolved,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GreenPrimary)
                }
            } else if (filteredReports.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.CheckCircle, null, Modifier.size(64.dp), tint = GreenPrimary.copy(alpha = 0.4f))
                        Spacer(Modifier.height(12.dp))
                        Text("No reports found", style = MaterialTheme.typography.titleMedium, color = OnSurfaceVariantLight)
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredReports) { report ->
                        AdminReportCard(
                            report = report,
                            onResolve = {
                                viewModel.markReportResolved(report.id, adminId, "Resolved by admin")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminReportCard(
    report: BlackspotReport,
    onResolve: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }
    val isPending = report.status == BlackspotReport.STATUS_PENDING
    var showResolveDialog by remember { mutableStateOf(false) }

    if (showResolveDialog) {
        AlertDialog(
            onDismissRequest = { showResolveDialog = false },
            title = { Text("Resolve Report") },
            text = { Text("Mark this blackspot report as resolved?") },
            confirmButton = {
                Button(
                    onClick = {
                        onResolve()
                        showResolveDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) { Text("Resolve") }
            },
            dismissButton = {
                TextButton(onClick = { showResolveDialog = false }) { Text("Cancel") }
            }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "By: ${report.userName}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        dateFormat.format(Date(report.createdAt)),
                        style = MaterialTheme.typography.bodySmall,
                        color = OnSurfaceVariantLight
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isPending) StatusPending.copy(alpha = 0.15f)
                    else StatusResolved.copy(alpha = 0.15f)
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

            Spacer(Modifier.height(12.dp))

            // Image
            if (report.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = report.imageUrl,
                    contentDescription = "Report image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(12.dp))
            }

            // Description
            Text(
                report.description,
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceLight
            )

            Spacer(Modifier.height(8.dp))

            // Location
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.LocationOn, null, Modifier.size(16.dp), tint = RedError)
                Spacer(Modifier.width(4.dp))
                Text(
                    "%.4f, %.4f".format(report.latitude, report.longitude),
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariantLight
                )
            }

            // Resolve button for pending reports
            if (isPending) {
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { showResolveDialog = true },
                    modifier = Modifier.fillMaxWidth().height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                ) {
                    Icon(Icons.Filled.CheckCircle, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.mark_resolved))
                }
            }

            // Resolved info
            if (!isPending && report.resolvedAt > 0) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Resolved: ${dateFormat.format(Date(report.resolvedAt))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = StatusResolved
                )
            }
        }
    }
}
