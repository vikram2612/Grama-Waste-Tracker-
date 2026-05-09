package com.grama.wastetracker.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.grama.wastetracker.R
import com.grama.wastetracker.ui.theme.*
import com.grama.wastetracker.ui.viewmodel.AuthViewModel
import com.grama.wastetracker.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    authViewModel: AuthViewModel,
    onNavigateToBlackspot: () -> Unit,
    onNavigateToWasteGuide: () -> Unit,
    onNavigateToAiAssistant: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToBlackspotList: () -> Unit,
    onLogout: () -> Unit
) {
    val homeState by homeViewModel.uiState.collectAsState()
    val authState by authViewModel.uiState.collectAsState()
    val tractorLocation = homeState.tractorLocation

    // Default to Karnataka region
    val defaultPos = LatLng(15.3173, 75.7139)
    val tractorPos = if (tractorLocation != null && tractorLocation.latitude != 0.0) {
        LatLng(tractorLocation.latitude, tractorLocation.longitude)
    } else defaultPos

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(tractorPos, 14f)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Welcome, ${authState.user?.fullName ?: "User"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToNotifications) {
                        Icon(Icons.Outlined.Notifications, "Notifications")
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Outlined.AccountCircle, "Profile")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GreenPrimary,
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
            // Tractor Status Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(6.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                stringResource(R.string.tractor_status),
                                style = MaterialTheme.typography.titleMedium,
                                color = OnSurfaceVariantLight
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (tractorLocation?.isActive == true) StatusActive
                                            else StatusOffline
                                        )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    if (tractorLocation?.isActive == true)
                                        stringResource(R.string.active)
                                    else stringResource(R.string.offline),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (tractorLocation?.isActive == true) StatusActive
                                    else StatusOffline
                                )
                            }
                        }

                        // ETA Badge
                        if (tractorLocation?.isActive == true && homeState.estimatedMinutes != null) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = GreenSurface,
                                border = BorderStroke(1.dp, GreenPrimary.copy(alpha = 0.3f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("ETA", style = MaterialTheme.typography.labelSmall, color = GreenPrimary)
                                    Text(
                                        "${homeState.estimatedMinutes} min",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = GreenPrimaryDark
                                    )
                                }
                            }
                        }
                    }

                    if (tractorLocation?.driverName?.isNotBlank() == true) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Driver: ${tractorLocation.driverName} • ${tractorLocation.routeName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = OnSurfaceVariantLight
                        )
                    }
                }
            }

            // Map Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box {
                    GoogleMap(
                        modifier = Modifier.fillMaxSize(),
                        cameraPositionState = cameraPositionState,
                        uiSettings = MapUiSettings(zoomControlsEnabled = false)
                    ) {
                        if (tractorLocation != null && tractorLocation.latitude != 0.0) {
                            Marker(
                                state = MarkerState(position = tractorPos),
                                title = "Kachara Gaadi",
                                snippet = if (tractorLocation.isActive) "Active" else "Offline"
                            )
                        }
                    }

                    // Map overlay label
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = GreenPrimaryDark.copy(alpha = 0.85f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.MyLocation, null, Modifier.size(14.dp), tint = Color.White)
                            Spacer(Modifier.width(4.dp))
                            Text(
                                stringResource(R.string.tractor_live_location),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Actions Grid
            Text(
                "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp),
                color = OnSurfaceLight
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.ReportProblem,
                    title = stringResource(R.string.report_blackspot),
                    color = OrangeAccent,
                    onClick = onNavigateToBlackspot
                )
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.AutoStories,
                    title = stringResource(R.string.waste_guide),
                    color = GreenPrimary,
                    onClick = onNavigateToWasteGuide
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.SmartToy,
                    title = stringResource(R.string.ai_assistant),
                    color = BluePrimary,
                    onClick = onNavigateToAiAssistant
                )
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Filled.List,
                    title = stringResource(R.string.blackspot_reports),
                    color = StatusPending,
                    onClick = onNavigateToBlackspotList
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun QuickActionCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    color: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(110.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.12f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, Modifier.size(24.dp), tint = color)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = OnSurfaceLight,
                maxLines = 2,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}
