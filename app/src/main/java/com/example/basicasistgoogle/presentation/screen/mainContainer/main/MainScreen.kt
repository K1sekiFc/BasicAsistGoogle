package com.example.basicasistgoogle.presentation.screen.mainContainer.main
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.basicasistgoogle.R
import com.example.basicasistgoogle.presentation.component.AppTopBarDef
import com.example.basicasistgoogle.presentation.component.GradientBackground
import com.example.basicasistgoogle.theme.unspecified_scheme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    mainViewModel: MainViewModel = hiltViewModel(),
    bottomPadding: Dp = 0.dp,
) {
    val context = LocalContext.current
    val uiState by mainViewModel.uiState.collectAsStateWithLifecycle()
    val userData = uiState.userData
    var hasLocationPermission by remember { mutableStateOf(false) }


    var localLatitude by remember { mutableStateOf<Double?>(null) }
    var localLongitude by remember { mutableStateOf<Double?>(null) }
    var localTimeRecord by remember { mutableStateOf<String?>(null) }

    val defaultLocation = LatLng(-12.046374, -77.042793) // Coordenadas por defecto
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 12f)
    }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Gestor de permisos reactivo
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

        hasLocationPermission = fineGranted || coarseGranted

        if (hasLocationPermission) {
            val userEmail = userData?.email.orEmpty()
            val userName = userData?.displayName?.takeIf { it.isNotBlank() } ?: userEmail
            captureLocationAndSend(context, fusedLocationClient, userName, userEmail, mainViewModel) { lat, lng ->
                localLatitude = lat
                localLongitude = lng
                localTimeRecord = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(lat, lng), 17f)
            }
        } else {
            Toast.makeText(
                context,
                "Se requiere permiso de GPS para registrar asistencia.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    // Efecto reactivo ante éxito de red
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            Toast.makeText(context, "¡Asistencia guardada en Google Sheets!", Toast.LENGTH_LONG)
                .show()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage.isNotBlank()) {
            Toast.makeText(context, "No se pudo registrar: ${uiState.errorMessage}", Toast.LENGTH_LONG).show()
        }
    }

    GradientBackground {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                AppTopBarDef(
                    title = stringResource(R.string.app_name),
                    subtitle = {
                        Icon(
                            painter = painterResource(R.drawable.image_icon_default),
                            contentDescription = null,
                            modifier = Modifier.size(70.dp),
                            tint = unspecified_scheme.color
                        )
                    },
                    showBackButton = false,
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(imageVector = Icons.Default.GpsFixed, contentDescription = null)
                        }
                    }
                )
            },
            containerColor = Color.Transparent
        ) { padding ->

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = false,
                        myLocationButtonEnabled = false
                    )
                ) {
                    if (localLatitude != null && localLongitude != null) {
                        Marker(
                            state = rememberMarkerState(
                                position = LatLng(
                                    localLatitude!!,
                                    localLongitude!!
                                )
                            ),
                            title = "Marcado Exitoso",
                            snippet = "Registrado a las: $localTimeRecord"
                        )
                    }
                }
                // CAPA FLOTANTE SUPERIOR: Cuadro Informativo de Usuario (arriba al centro)
                Card(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp)
                        .fillMaxWidth(0.9f),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(
                            alpha = 0.9f
                        )
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Control de Asistencia",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        // Si existe nombre del usuario lo mostramos, de lo contrario mostramos el email
                        userData?.displayName?.let { name ->
                            Text(
                                text = name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = userData?.email?: "Cargando usuario...",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                FloatingActionButton(
                    onClick = {
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(
                            bottom = bottomPadding + 24.dp,
                            end = 24.dp
                        )
                        .size(130.dp, 56.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = RoundedCornerShape(28.dp)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    } else {
                        Text(
                            text = "MARCAR",
                            fontSize = 16.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@SuppressLint("MissingPermission")
private fun captureLocationAndSend(
    context: Context,
    client: com.google.android.gms.location.FusedLocationProviderClient,
    userName: String,
    email: String,
    viewModel: MainViewModel,
    onSuccessUpdate: (Double, Double) -> Unit
) {
    client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
        .addOnSuccessListener { location ->
            if (location != null) {
                onSuccessUpdate(location.latitude, location.longitude)
                viewModel.submitAttendanceRecord(userName, email, location.latitude, location.longitude)
            } else {
                Toast.makeText(
                    context,
                    "Active la ubicación en los ajustes del teléfono.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        .addOnFailureListener { exception ->
            Toast.makeText(
                context,
                "Error de hardware GPS: ${exception.localizedMessage}",
                Toast.LENGTH_LONG
            ).show()
        }
}
