package com.example.a216839_wan_project2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a216839_wan_project2.data.entity.HealthProfileEntity
import com.example.a216839_wan_project2.viewmodel.HealthProfileViewModel
import com.example.a216839_wan_project2.ui.theme.BlueHeader
import com.example.a216839_wan_project2.ui.theme.PurpleHeader

@Composable
fun ProfileFormScreen(
    viewModel: HealthProfileViewModel,
    onBack   : () -> Unit,
    onSubmit : () -> Unit
) {
    // Pre-fill from Room if profile already exists
    val savedProfile by viewModel.profile.collectAsState()

    var fullName       by remember { mutableStateOf("") }
    var icNumber       by remember { mutableStateOf("") }
    var dateOfBirth    by remember { mutableStateOf("") }
    var bloodType      by remember { mutableStateOf("") }
    var allergies      by remember { mutableStateOf("") }
    var conditions     by remember { mutableStateOf("") }
    var emergencyName  by remember { mutableStateOf("") }
    var emergencyPhone by remember { mutableStateOf("") }
    var showError      by remember { mutableStateOf(false) }
    var prefilled      by remember { mutableStateOf(false) }

    // Pre-fill once when saved profile loads from Room
    LaunchedEffect(savedProfile) {
        if (!prefilled && savedProfile != null) {
            savedProfile?.let { p ->
                fullName       = p.fullName
                icNumber       = p.icNumber
                dateOfBirth    = p.dateOfBirth
                bloodType      = p.bloodType
                allergies      = p.allergies
                conditions     = p.conditions
                emergencyName  = p.emergencyName
                emergencyPhone = p.emergencyPhone
                prefilled      = true
            }
        }
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            FormHeader(onBack = onBack)
            StepIndicator(currentStep = 0)
            Spacer(Modifier.height(16.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {

                FormSection("Personal Information", Icons.Outlined.Person, MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.primary) {
                    ProfileTextField("Full Name",   fullName,    "e.g. Syasya Nabilah",   required = true, isError = showError && fullName.isBlank(),   errorMsg = "Full name is required",   onValueChange = { fullName    = it })
                    Spacer(Modifier.height(12.dp))
                    ProfileTextField("IC Number",   icNumber,    "e.g. 991231-14-5678",   required = true, isError = showError && icNumber.isBlank(),   errorMsg = "IC number is required",   onValueChange = { icNumber    = it })
                    Spacer(Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(Modifier.weight(1f)) { ProfileTextField("Date of Birth", dateOfBirth, "e.g. 31 Dec 1999", onValueChange = { dateOfBirth = it }) }
                        Box(Modifier.weight(1f)) { ProfileTextField("Blood Type",   bloodType,   "e.g. A+, O-",       onValueChange = { bloodType   = it }) }
                    }
                }

                Spacer(Modifier.height(14.dp))

                FormSection("Medical Information", Icons.Outlined.MedicalServices, Color(0xFFFDE8EF), Color(0xFFC0496E)) {
                    ProfileTextField("Known Allergies",             allergies,  "e.g. Penicillin, Peanuts",      onValueChange = { allergies   = it })
                    Spacer(Modifier.height(12.dp))
                    ProfileTextField("Existing Medical Conditions", conditions, "e.g. Hypertension, Diabetes",   onValueChange = { conditions  = it }, singleLine = false, maxLines = 3)
                }

                Spacer(Modifier.height(14.dp))

                FormSection("Emergency Contact", Icons.Outlined.ContactPhone, Color(0xFFE8F5F0), Color(0xFF1D9E75)) {
                    ProfileTextField("Contact Name",         emergencyName,  "e.g. Ahmad bin Ali",   onValueChange = { emergencyName  = it })
                    Spacer(Modifier.height(12.dp))
                    ProfileTextField("Contact Phone Number", emergencyPhone, "e.g. 012-345 6789",    onValueChange = { emergencyPhone = it })
                }

                Spacer(Modifier.height(24.dp))
                Text("Fields marked with * are required", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 16.dp))

                Button(
                    onClick = {
                        if (fullName.isBlank() || icNumber.isBlank()) {
                            showError = true
                        } else {
                            showError = false
                            viewModel.saveProfile(
                                HealthProfileEntity(
                                    id             = savedProfile?.id ?: 0,
                                    fullName       = fullName,
                                    icNumber       = icNumber,
                                    dateOfBirth    = dateOfBirth,
                                    bloodType      = bloodType,
                                    allergies      = allergies,
                                    conditions     = conditions,
                                    emergencyName  = emergencyName,
                                    emergencyPhone = emergencyPhone
                                )
                            )
                            onSubmit()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape    = RoundedCornerShape(16.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Preview Profile →", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(Modifier.height(10.dp))
                OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(16.dp)) {
                    Text("Cancel", fontWeight = FontWeight.Medium)
                }
                Spacer(Modifier.height(28.dp))
            }
        }
    }
}

// ── HEADER ────────────────────────────────────────────────────────────────────
@Composable
fun FormHeader(onBack: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().height(150.dp)
            .background(Brush.linearGradient(listOf(PurpleHeader, BlueHeader)))
    ) {
        IconButton(
            onClick  = onBack,
            modifier = Modifier.padding(12.dp).size(38.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.18f)).align(Alignment.TopStart)
        ) { Icon(Icons.Filled.ArrowBack, "Back", tint = Color.White) }
        Column(modifier = Modifier.align(Alignment.BottomStart).padding(start = 20.dp, bottom = 20.dp)) {
            Box(modifier = Modifier.size(38.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.20f)), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.AccountCircle, null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text("Health Profile",                       color = Color.White,                     fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("Fill in your personal health details", color = Color.White.copy(alpha = 0.80f), fontSize = 12.sp)
        }
    }
}

// ── STEP INDICATOR ────────────────────────────────────────────────────────────
@Composable
fun StepIndicator(currentStep: Int) {
    val steps = listOf("Personal", "Medical", "Emergency", "Preview")
    Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.surface) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
            steps.forEachIndexed { index, label ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    val isActive   = index == currentStep
                    val isComplete = index < currentStep
                    Box(
                        modifier         = Modifier.size(22.dp).clip(CircleShape).background(when { isActive -> MaterialTheme.colorScheme.primary; isComplete -> Color(0xFFE8F5F0); else -> MaterialTheme.colorScheme.surfaceVariant }),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("${index + 1}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = when { isActive -> MaterialTheme.colorScheme.onPrimary; isComplete -> Color(0xFF1D9E75); else -> MaterialTheme.colorScheme.onSurfaceVariant })
                    }
                    Spacer(Modifier.width(5.dp))
                    Text(label, fontSize = 10.sp, fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal, color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (index < steps.lastIndex) HorizontalDivider(modifier = Modifier.width(16.dp), color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
            }
        }
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
    }
}

// ── FORM SECTION ──────────────────────────────────────────────────────────────
@Composable
fun FormSection(title: String, icon: ImageVector, iconBg: Color, iconTint: Color, content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(bottom = 14.dp)) {
                Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(10.dp)).background(iconBg), contentAlignment = Alignment.Center) {
                    Icon(icon, title, tint = iconTint, modifier = Modifier.size(17.dp))
                }
                Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp, modifier = Modifier.padding(bottom = 14.dp))
            content()
        }
    }
}

// ── TEXT FIELD ────────────────────────────────────────────────────────────────
@Composable
fun ProfileTextField(label: String, value: String, placeholder: String, required: Boolean = false, isError: Boolean = false, errorMsg: String = "", singleLine: Boolean = true, maxLines: Int = 1, onValueChange: (String) -> Unit) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.SemiBold)
            if (required) { Spacer(Modifier.width(3.dp)); Text("*", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold) }
        }
        Spacer(Modifier.height(5.dp))
        OutlinedTextField(value = value, onValueChange = onValueChange, placeholder = { Text(placeholder) }, singleLine = singleLine, maxLines = maxLines, isError = isError, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary, unfocusedBorderColor = MaterialTheme.colorScheme.outline, errorBorderColor = MaterialTheme.colorScheme.error))
        if (isError && errorMsg.isNotBlank()) Text(errorMsg, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 4.dp, top = 3.dp))
    }
}