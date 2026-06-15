package com.example.a216839_wan_project2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a216839_wan_project2.data.api.OverpassElement
import com.example.a216839_wan_project2.data.entity.SavedClinicEntity
import com.example.a216839_wan_project2.data.firebase.SharedClinicReport
import com.example.a216839_wan_project2.viewmodel.HealthCentreUiState
import com.example.a216839_wan_project2.viewmodel.HealthCentreViewModel
import com.example.a216839_wan_project2.ui.theme.BlueHeader
import com.example.a216839_wan_project2.ui.theme.PurpleHeader
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HealthCentreScreen(
    viewModel: HealthCentreViewModel,
    onBack   : () -> Unit
) {
    val uiState       by viewModel.uiState.collectAsState()
    val savedClinics  by viewModel.savedClinics.collectAsState()
    val sharedReports by viewModel.sharedReports.collectAsState()
    val message       by viewModel.message.collectAsState()

    val locationPermission = rememberPermissionState(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    // Tab: 0 = Find Clinics, 1 = Saved, 2 = Community
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost   = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ── HEADER ────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Brush.linearGradient(listOf(PurpleHeader, BlueHeader)))
            ) {
                IconButton(
                    onClick  = onBack,
                    modifier = Modifier
                        .padding(12.dp).size(42.dp).clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .align(Alignment.TopStart)
                ) {
                    Icon(Icons.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 20.dp, bottom = 20.dp)
                ) {
                    Text("Health Centres",
                        color = Color.White, fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold)
                    Text("GPS • Live API • Room • Firebase",
                        color = Color.White.copy(alpha = 0.80f), fontSize = 13.sp)
                }
            }

            // ── TABS ──────────────────────────────────────────────────────────
            TabRow(selectedTabIndex = selectedTab) {
                Tab(selected = selectedTab == 0,
                    onClick  = { selectedTab = 0 },
                    text     = { Text("Find Nearby") })
                Tab(selected = selectedTab == 1,
                    onClick  = { selectedTab = 1 },
                    text     = { Text("Saved (${savedClinics.size})") })
                Tab(selected = selectedTab == 2,
                    onClick  = { selectedTab = 2 },
                    text     = { Text("Community") })
            }

            // ── TAB CONTENT ───────────────────────────────────────────────────
            when (selectedTab) {

                // ── TAB 0: Find nearby clinics (GPS + API) ────────────────────
                0 -> {
                    when (val state = uiState) {
                        is HealthCentreUiState.Idle -> IdleContent(
                            hasPermission       = locationPermission.status.isGranted,
                            onRequestPermission = { locationPermission.launchPermissionRequest() },
                            onFindClinics       = {
                                if (locationPermission.status.isGranted)
                                    viewModel.findNearbyClinics()
                                else
                                    locationPermission.launchPermissionRequest()
                            }
                        )
                        is HealthCentreUiState.LoadingGps ->
                            LoadingContent("Getting your location via GPS...")
                        is HealthCentreUiState.LoadingApi ->
                            LoadingContent("Searching for nearby clinics via API...")
                        is HealthCentreUiState.Success -> SuccessContent(
                            state     = state,
                            viewModel = viewModel,
                            savedClinics = savedClinics,
                            onReset   = { viewModel.reset() }
                        )
                        is HealthCentreUiState.Error -> ErrorContent(
                            message = state.message,
                            onRetry = { viewModel.reset() }
                        )
                    }
                }

                // ── TAB 1: Saved clinics (Room) ───────────────────────────────
                1 -> SavedClinicsTab(
                    savedClinics = savedClinics,
                    onDelete     = { viewModel.deleteSaved(it) }
                )

                // ── TAB 2: Community shared clinics (Firebase) ────────────────
                2 -> CommunityTab(
                    reports  = sharedReports,
                    onDelete = { viewModel.deleteSharedReport(it) }
                )
            }
        }
    }
}

// ── TAB 0: IDLE ───────────────────────────────────────────────────────────────
@Composable
private fun IdleContent(
    hasPermission      : Boolean,
    onRequestPermission: () -> Unit,
    onFindClinics      : () -> Unit
) {
    Column(
        modifier            = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier         = Modifier.size(96.dp).clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Outlined.LocalHospital, null,
                modifier = Modifier.size(48.dp),
                tint     = MaterialTheme.colorScheme.primary)
        }
        Spacer(Modifier.height(20.dp))
        Text("Find Nearby Health Centres",
            fontSize  = 20.sp, fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text("GPS detects your location, then our API finds clinics within 5km.",
            style     = MaterialTheme.typography.bodyMedium,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center)
        Spacer(Modifier.height(32.dp))

        if (!hasPermission) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(16.dp),
                colors   = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Row(modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Warning, null,
                        tint     = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(10.dp))
                    Text("Location permission required.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onErrorContainer)
                }
            }
            Spacer(Modifier.height(16.dp))
        }

        Button(
            onClick  = onFindClinics,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape    = RoundedCornerShape(16.dp)
        ) {
            Icon(Icons.Outlined.MyLocation, null)
            Spacer(Modifier.width(8.dp))
            Text(if (hasPermission) "Find Clinics Near Me" else "Grant Location & Find",
                fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(28.dp))

        // Explain all 4 pillars
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape    = RoundedCornerShape(16.dp),
            colors   = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("How this screen works",
                    fontWeight = FontWeight.Bold,
                    style      = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(10.dp))
                listOf(
                    "📍 GPS Sensor reads your coordinates",
                    "🌐 Overpass API fetches real clinics nearby",
                    "💾 Tap Save → stored in Room (offline)",
                    "🔥 Tap Share → uploaded to Firebase community board"
                ).forEach {
                    Text(it, style = MaterialTheme.typography.bodySmall,
                        color    = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 3.dp))
                }
            }
        }
    }
}

// ── TAB 0: LOADING ────────────────────────────────────────────────────────────
@Composable
private fun LoadingContent(message: String) {
    Column(
        modifier            = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(56.dp), strokeWidth = 4.dp)
        Spacer(Modifier.height(20.dp))
        Text(message, style = MaterialTheme.typography.bodyLarge,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center)
    }
}

// ── TAB 0: SUCCESS ────────────────────────────────────────────────────────────
@Composable
private fun SuccessContent(
    state       : HealthCentreUiState.Success,
    viewModel   : HealthCentreViewModel,
    savedClinics: List<SavedClinicEntity>,
    onReset     : () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Surface(color = MaterialTheme.colorScheme.primaryContainer) {
            Row(
                modifier          = Modifier.fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.CheckCircle, null,
                        tint     = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("${state.clinics.size} centres found within 5km",
                        style      = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color      = MaterialTheme.colorScheme.onPrimaryContainer)
                }
                TextButton(onClick = onReset) { Text("Reset", fontSize = 12.sp) }
            }
        }

        LazyColumn(
            contentPadding      = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.clinics) { clinic ->
                val dist      = viewModel.distanceKm(state.userLat, state.userLon,
                    clinic.lat, clinic.lon)
                val isSaved   = savedClinics.any { it.name == clinic.name }
                ClinicCard(
                    clinic    = clinic,
                    distance  = dist,
                    isSaved   = isSaved,
                    onSave    = { viewModel.saveClinic(clinic, dist) },
                    onShare   = { by, note ->
                        viewModel.shareClinicToFirebase(clinic, dist, by, note)
                    }
                )
            }
        }
    }
}

// ── CLINIC CARD with Save + Share buttons ─────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClinicCard(
    clinic  : OverpassElement,
    distance: Double,
    isSaved : Boolean,
    onSave  : () -> Unit,
    onShare : (reportedBy: String, note: String) -> Unit
) {
    var showShareSheet by remember { mutableStateOf(false) }
    val sheetState     = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier         = Modifier.size(50.dp).clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.LocalHospital, null,
                        tint     = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(26.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(clinic.name,
                        style      = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold)
                    if (clinic.address != "Address not available") {
                        Spacer(Modifier.height(2.dp))
                        Text(clinic.address,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (clinic.phone.isNotBlank()) {
                        Spacer(Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.Phone, null,
                                modifier = Modifier.size(12.dp),
                                tint     = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.width(4.dp))
                            Text(clinic.phone,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
                Surface(shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer) {
                    Text("${distance}km",
                        fontSize   = 11.sp, fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier   = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                }
            }

            Spacer(Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
            Spacer(Modifier.height(10.dp))

            // ── Save + Share buttons ──────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // SAVE TO ROOM button
                OutlinedButton(
                    onClick  = onSave,
                    modifier = Modifier.weight(1f).height(38.dp),
                    shape    = RoundedCornerShape(10.dp),
                    colors   = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (isSaved)
                            MaterialTheme.colorScheme.primaryContainer
                        else Color.Transparent
                    )
                ) {
                    Icon(
                        imageVector        = if (isSaved) Icons.Filled.Bookmark
                        else Icons.Filled.BookmarkBorder,
                        contentDescription = "Save",
                        modifier           = Modifier.size(16.dp),
                        tint               = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text     = if (isSaved) "Saved" else "Save",
                        fontSize = 12.sp,
                        color    = MaterialTheme.colorScheme.primary
                    )
                }

                // SHARE TO FIREBASE button
                Button(
                    onClick  = { showShareSheet = true },
                    modifier = Modifier.weight(1f).height(38.dp),
                    shape    = RoundedCornerShape(10.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE91E63)
                    )
                ) {
                    Icon(Icons.Outlined.Share, null,
                        modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Share", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // ── Share bottom sheet ────────────────────────────────────────────────────
    if (showShareSheet) {
        ShareClinicSheet(
            clinicName = clinic.name,
            sheetState = sheetState,
            onDismiss  = { showShareSheet = false },
            onConfirm  = { by, note ->
                onShare(by, note)
                showShareSheet = false
            }
        )
    }
}

// ── SHARE BOTTOM SHEET ────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShareClinicSheet(
    clinicName: String,
    sheetState: SheetState,
    onDismiss : () -> Unit,
    onConfirm : (reportedBy: String, note: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState       = sheetState,
        shape            = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
        ) {
            Text("Share to Community 🔥",
                fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            Text("Share info about $clinicName with the community via Firebase.",
                fontSize = 13.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp))

            Text("Your Name (optional)",
                style      = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color      = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value         = name,
                onValueChange = { name = it },
                placeholder   = { Text("e.g. Syasya or Anonymous") },
                singleLine    = true,
                shape         = RoundedCornerShape(12.dp),
                modifier      = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(14.dp))

            Text("Note for Community",
                style      = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color      = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(6.dp))
            OutlinedTextField(
                value         = note,
                onValueChange = { note = it },
                placeholder   = { Text("e.g. Short waiting time, friendly staff") },
                singleLine    = false,
                minLines      = 2,
                maxLines      = 3,
                shape         = RoundedCornerShape(12.dp),
                modifier      = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(24.dp))

            Button(
                onClick  = { onConfirm(name, note) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape    = RoundedCornerShape(16.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE91E63))
            ) {
                Icon(Icons.Outlined.Send, null)
                Spacer(Modifier.width(8.dp))
                Text("Share to Firebase", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ── TAB 1: SAVED CLINICS (Room) ───────────────────────────────────────────────
@Composable
private fun SavedClinicsTab(
    savedClinics: List<SavedClinicEntity>,
    onDelete    : (SavedClinicEntity) -> Unit
) {
    if (savedClinics.isEmpty()) {
        Column(
            modifier            = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Filled.BookmarkBorder, null,
                modifier = Modifier.size(64.dp),
                tint     = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))
            Text("No saved clinics yet",
                fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Tap Save on any clinic in the Find Nearby tab to save it here for offline access.",
                style     = MaterialTheme.typography.bodyMedium,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center)
        }
        return
    }

    LazyColumn(
        contentPadding      = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(12.dp),
                colors   = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Storage, null,
                        tint     = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Saved locally using Room Database — available offline",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
        items(savedClinics) { clinic ->
            SavedClinicCard(clinic = clinic, onDelete = { onDelete(clinic) })
        }
    }
}

@Composable
private fun SavedClinicCard(clinic: SavedClinicEntity, onDelete: () -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier         = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Bookmark, null,
                    tint     = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(clinic.name,
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold)
                if (clinic.address.isNotBlank() && clinic.address != "Address not available") {
                    Text(clinic.address,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Text("${clinic.distanceKm}km away",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Outlined.Delete, "Remove",
                    tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}

// ── TAB 2: COMMUNITY (Firebase) ───────────────────────────────────────────────
@Composable
private fun CommunityTab(
    reports : List<SharedClinicReport>,
    onDelete: (String) -> Unit
) {
    if (reports.isEmpty()) {
        Column(
            modifier            = Modifier.fillMaxSize().padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("🔥", fontSize = 48.sp)
            Spacer(Modifier.height(16.dp))
            Text("No community reports yet",
                fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text("Be the first! Find a clinic nearby and tap Share to add it here.",
                style     = MaterialTheme.typography.bodyMedium,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center)
        }
        return
    }

    LazyColumn(
        contentPadding      = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(12.dp),
                colors   = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFEBEE))
            ) {
                Row(modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    Text("🔥", fontSize = 18.sp)
                    Spacer(Modifier.width(8.dp))
                    Text("Community reports stored in Firebase Firestore — synced in real-time",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFC62828))
                }
            }
        }
        items(reports) { report ->
            CommunityReportCard(report = report, onDelete = { onDelete(report.id) })
        }
    }
}

@Composable
private fun CommunityReportCard(report: SharedClinicReport, onDelete: () -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier         = Modifier.size(36.dp).clip(CircleShape)
                            .background(Color(0xFFFFEBEE)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text  = (report.reportedBy.firstOrNull() ?: 'A')
                                .toString().uppercase(),
                            fontSize   = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Color(0xFFE91E63)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(report.reportedBy.ifBlank { "Anonymous" },
                        style      = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold)
                }
                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Outlined.Delete, null,
                        tint     = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp))
                }
            }

            Spacer(Modifier.height(8.dp))
            Text(report.clinicName,
                style      = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold)
            if (report.address.isNotBlank() && report.address != "Address not available") {
                Text(report.address,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (report.note.isNotBlank()) {
                Spacer(Modifier.height(6.dp))
                Surface(shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer) {
                    Text("\"${report.note}\"",
                        style    = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        color    = MaterialTheme.colorScheme.onSecondaryContainer)
                }
            }
            Spacer(Modifier.height(6.dp))
            Text("${report.distanceKm}km away",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary)
        }
    }
}

// ── ERROR ─────────────────────────────────────────────────────────────────────
@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Column(
        modifier            = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Outlined.ErrorOutline, null,
            modifier = Modifier.size(64.dp),
            tint     = MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(16.dp))
        Text("Oops!", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text(message,
            style     = MaterialTheme.typography.bodyMedium,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        OutlinedButton(onClick = onRetry, shape = RoundedCornerShape(16.dp)) {
            Icon(Icons.Outlined.Refresh, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Try Again")
        }
    }
}
