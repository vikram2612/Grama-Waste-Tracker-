package com.grama.wastetracker.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.grama.wastetracker.R
import com.grama.wastetracker.ui.theme.*
import com.grama.wastetracker.ui.viewmodel.AdminViewModel
import com.grama.wastetracker.ui.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AdminViewModel,
    authViewModel: AuthViewModel,
    onNavigateToReports: () -> Unit,
    onNavigateToTractor: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.admin_dashboard), fontWeight = FontWeight.Bold)
                        Text("Welcome, ${authState.user?.fullName ?: "Admin"}", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Filled.Logout, "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GreenPrimaryDark,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .background(BackgroundLight)
        ) {
            // Stats Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(GreenPrimaryDark, GreenPrimary)))
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem("Total", uiState.totalReports.toString(), Color.White)
                    StatItem("Pending", uiState.pendingCount.toString(), OrangeAccent)
                    StatItem("Resolved", uiState.resolvedCount.toString(), BlueLight)
                }
            }

            Spacer(Modifier.height(20.dp))

            // Tractor Status
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = CircleShape, color = GreenSurface, modifier = Modifier.size(48.dp)) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.Agriculture, null, Modifier.size(28.dp), tint = GreenPrimary)
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(stringResource(R.string.tractor_status), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(10.dp)
                                        .background(if (uiState.tractorLocation?.isActive == true) StatusActive else StatusOffline, CircleShape)
                                )
                                Spacer(Modifier.width(6.dp))
                                Text(
                                    if (uiState.tractorLocation?.isActive == true) "Active" else "Offline",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (uiState.tractorLocation?.isActive == true) StatusActive else StatusOffline
                                )
                            }
                        }
                    }
                    Switch(
                        checked = uiState.tractorLocation?.isActive == true,
                        onCheckedChange = { viewModel.updateTractorStatus(it) },
                        colors = SwitchDefaults.colors(checkedTrackColor = GreenPrimary)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Action Cards
            Text("Management", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 16.dp))
            Spacer(Modifier.height(12.dp))

            AdminActionCard(
                icon = Icons.Filled.Map,
                title = stringResource(R.string.view_reports),
                subtitle = "${uiState.pendingCount} pending reports need attention",
                color = OrangeAccent,
                onClick = onNavigateToReports,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(Modifier.height(12.dp))

            AdminActionCard(
                icon = Icons.Filled.Agriculture,
                title = stringResource(R.string.manage_tractor),
                subtitle = "Update tractor location and status",
                color = BluePrimary,
                onClick = onNavigateToTractor,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold, color = color)
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
    }
}

@Composable
fun AdminActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(shape = RoundedCornerShape(12.dp), color = color.copy(alpha = 0.1f), modifier = Modifier.size(48.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, Modifier.size(28.dp), tint = color)
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = OnSurfaceVariantLight)
            }
            Icon(Icons.Filled.ChevronRight, null, tint = OnSurfaceVariantLight)
        }
    }
}
