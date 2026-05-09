package com.grama.wastetracker.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.grama.wastetracker.R
import com.grama.wastetracker.ui.theme.*
import com.grama.wastetracker.ui.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTractorScreen(
    viewModel: AdminViewModel,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var latitude by remember { mutableStateOf(uiState.tractorLocation?.latitude?.toString() ?: "15.3173") }
    var longitude by remember { mutableStateOf(uiState.tractorLocation?.longitude?.toString() ?: "75.7139") }
    var driverName by remember { mutableStateOf(uiState.tractorLocation?.driverName ?: "") }
    var routeName by remember { mutableStateOf(uiState.tractorLocation?.routeName ?: "") }

    // Update fields when tractor data loads
    LaunchedEffect(uiState.tractorLocation) {
        uiState.tractorLocation?.let {
            if (it.latitude != 0.0) latitude = it.latitude.toString()
            if (it.longitude != 0.0) longitude = it.longitude.toString()
            if (it.driverName.isNotBlank()) driverName = it.driverName
            if (it.routeName.isNotBlank()) routeName = it.routeName
        }
    }

    val tractorPos = LatLng(
        latitude.toDoubleOrNull() ?: 15.3173,
        longitude.toDoubleOrNull() ?: 75.7139
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(tractorPos, 14f)
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.updateSuccess) {
        if (uiState.updateSuccess) {
            snackbarHostState.showSnackbar("Tractor location updated successfully!")
            viewModel.clearSuccess()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar("Error: $it")
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.manage_tractor), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Filled.ArrowBack, "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GreenPrimaryDark,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .background(BackgroundLight)
        ) {
            // Map Preview
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = MapUiSettings(zoomControlsEnabled = true)
                ) {
                    Marker(
                        state = MarkerState(position = tractorPos),
                        title = "Tractor Location"
                    )
                }
            }

            // Status Toggle
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Agriculture, null, Modifier.size(24.dp), tint = GreenPrimary)
                        Spacer(Modifier.width(12.dp))
                        Text("Tractor Active", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    }
                    Switch(
                        checked = uiState.tractorLocation?.isActive == true,
                        onCheckedChange = { viewModel.updateTractorStatus(it) },
                        colors = SwitchDefaults.colors(checkedTrackColor = GreenPrimary)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Location Form
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        stringResource(R.string.update_tractor),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(16.dp))

                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = latitude,
                            onValueChange = { latitude = it },
                            label = { Text("Latitude") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GreenPrimary, cursorColor = GreenPrimary)
                        )
                        OutlinedTextField(
                            value = longitude,
                            onValueChange = { longitude = it },
                            label = { Text("Longitude") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GreenPrimary, cursorColor = GreenPrimary)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = driverName,
                        onValueChange = { driverName = it },
                        label = { Text("Driver Name") },
                        leadingIcon = { Icon(Icons.Outlined.Person, null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GreenPrimary, cursorColor = GreenPrimary)
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = routeName,
                        onValueChange = { routeName = it },
                        label = { Text("Route Name") },
                        leadingIcon = { Icon(Icons.Outlined.Route, null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = GreenPrimary, cursorColor = GreenPrimary)
                    )

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = {
                            val lat = latitude.toDoubleOrNull() ?: 0.0
                            val lng = longitude.toDoubleOrNull() ?: 0.0
                            viewModel.updateTractorLocation(lat, lng, driverName, routeName)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        enabled = !uiState.isLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GreenPrimary)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                            Spacer(Modifier.width(8.dp))
                        }
                        Icon(Icons.Filled.MyLocation, null, Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(stringResource(R.string.update_status), style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}
