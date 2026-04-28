package com.example.a216839_wan_lab4

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
import com.example.a216839_wan_lab4.ui.theme.BlueHeader
import com.example.a216839_wan_lab4.ui.theme.PurpleHeader

// ── SCREEN 2: HEALTH PROFILE FORM ────────────────────────────────────────────
@Composable
fun ProfileFormScreen(
    viewModel: HealthProfileViewModel,
    onBack   : () -> Unit,
    onSubmit : () -> Unit
) {
    var fullName       by remember { mutableStateOf(viewModel.profile.fullName) }
    var icNumber       by remember { mutableStateOf(viewModel.profile.icNumber) }
    var dateOfBirth    by remember { mutableStateOf(viewModel.profile.dateOfBirth) }
    var bloodType      by remember { mutableStateOf(viewModel.profile.bloodType) }
    var allergies      by remember { mutableStateOf(viewModel.profile.allergies) }
    var conditions     by remember { mutableStateOf(viewModel.profile.conditions) }
    var emergencyName  by remember { mutableStateOf(viewModel.profile.emergencyName) }
    var emergencyPhone by remember { mutableStateOf(viewModel.profile.emergencyPhone) }
    var showError      by remember { mutableStateOf(false) }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {

            // ── Gradient header ───────────────────────────────────────────────
            FormHeader(onBack = onBack)

            // ── Step indicator ────────────────────────────────────────────────
            StepIndicator(currentStep = 0)

            Spacer(Modifier.height(16.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {

                // ── Personal Information ──────────────────────────────────────
                FormSection(
                    title     = "Personal Information",
                    icon      = Icons.Outlined.Person,
                    iconBg    = MaterialTheme.colorScheme.primaryContainer,
                    iconTint  = MaterialTheme.colorScheme.primary
                ) {
                    // Full Name + IC Number — full width (required)
                    ProfileTextField(
                        label         = "Full Name",
                        value         = fullName,
                        placeholder   = "e.g. Syasya Nabilah",
                        required      = true,
                        isError       = showError && fullName.isBlank(),
                        errorMsg      = "Full name is required",
                        onValueChange = { fullName = it }
                    )
                    Spacer(Modifier.height(12.dp))
                    ProfileTextField(
                        label         = "IC Number",
                        value         = icNumber,
                        placeholder   = "e.g. 991231-14-5678",
                        required      = true,
                        isError       = showError && icNumber.isBlank(),
                        errorMsg      = "IC number is required",
                        onValueChange = { icNumber = it }
                    )
                    Spacer(Modifier.height(12.dp))
                    // Date of Birth + Blood Type — side by side
                    Row(
                        modifier            = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            ProfileTextField(
                                label         = "Date of Birth",
                                value         = dateOfBirth,
                                placeholder   = "e.g. 31 Dec 1999",
                                onValueChange = { dateOfBirth = it }
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            ProfileTextField(
                                label         = "Blood Type",
                                value         = bloodType,
                                placeholder   = "e.g. A+, O-",
                                onValueChange = { bloodType = it }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // ── Medical Information ───────────────────────────────────────
                FormSection(
                    title    = "Medical Information",
                    icon     = Icons.Outlined.MedicalServices,
                    iconBg   = Color(0xFFFDE8EF),
                    iconTint = Color(0xFFC0496E)
                ) {
                    ProfileTextField(
                        label         = "Known Allergies",
                        value         = allergies,
                        placeholder   = "e.g. Penicillin, Peanuts",
                        onValueChange = { allergies = it }
                    )
                    Spacer(Modifier.height(12.dp))
                    ProfileTextField(
                        label         = "Existing Medical Conditions",
                        value         = conditions,
                        placeholder   = "e.g. Hypertension, Diabetes",
                        onValueChange = { conditions = it },
                        singleLine    = false,
                        maxLines      = 3
                    )
                }

                Spacer(Modifier.height(14.dp))

                // ── Emergency Contact ─────────────────────────────────────────
                FormSection(
                    title    = "Emergency Contact",
                    icon     = Icons.Outlined.ContactPhone,
                    iconBg   = Color(0xFFE8F5F0),
                    iconTint = Color(0xFF1D9E75)
                ) {
                    ProfileTextField(
                        label         = "Contact Name",
                        value         = emergencyName,
                        placeholder   = "e.g. Ahmad bin Ali",
                        onValueChange = { emergencyName = it }
                    )
                    Spacer(Modifier.height(12.dp))
                    ProfileTextField(
                        label         = "Contact Phone Number",
                        value         = emergencyPhone,
                        placeholder   = "e.g. 012-345 6789",
                        onValueChange = { emergencyPhone = it }
                    )
                }

                Spacer(Modifier.height(24.dp))

                // ── Required field note ───────────────────────────────────────
                Text(
                    text  = "Fields marked with * are required",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // ── Submit ────────────────────────────────────────────────────
                Button(
                    onClick = {
                        if (fullName.isBlank() || icNumber.isBlank()) {
                            showError = true
                        } else {
                            showError = false
                            viewModel.updateProfile(
                                HealthProfile(
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape  = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor   = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Preview Profile →", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                Spacer(Modifier.height(10.dp))

                OutlinedButton(
                    onClick  = onBack,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
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
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(Brush.linearGradient(listOf(PurpleHeader, BlueHeader)))
    ) {
        IconButton(
            onClick  = onBack,
            modifier = Modifier
                .padding(12.dp)
                .size(38.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.18f))
                .align(Alignment.TopStart)
        ) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 20.dp)
        ) {
            // Small icon above the title
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.20f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.AccountCircle,
                    contentDescription = null,
                    tint     = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                "Health Profile",
                color      = Color.White,
                fontSize   = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Fill in your personal health details",
                color    = Color.White.copy(alpha = 0.80f),
                fontSize = 12.sp
            )
        }
    }
}

// ── STEP INDICATOR ────────────────────────────────────────────────────────────
//
// A horizontal step strip showing the user's progress through the form flow.
// Steps: 0 = Personal, 1 = Medical, 2 = Emergency, 3 = Preview.

@Composable
fun StepIndicator(currentStep: Int) {
    val steps = listOf("Personal", "Medical", "Emergency", "Preview")

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color    = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp
    ) {
        HorizontalDivider(
            color     = MaterialTheme.colorScheme.outlineVariant,
            thickness = 0.5.dp
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            steps.forEachIndexed { index, label ->
                // Step circle + label
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier          = Modifier.weight(1f)
                ) {
                    val isActive   = index == currentStep
                    val isComplete = index < currentStep

                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(
                                when {
                                    isActive   -> MaterialTheme.colorScheme.primary
                                    isComplete -> Color(0xFFE8F5F0)
                                    else       -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text  = "${index + 1}",
                            fontSize   = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isActive   -> MaterialTheme.colorScheme.onPrimary
                                isComplete -> Color(0xFF1D9E75)
                                else       -> MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    }
                    Spacer(Modifier.width(5.dp))
                    Text(
                        text  = label,
                        fontSize = 10.sp,
                        fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isActive)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Connector line between steps
                if (index < steps.lastIndex) {
                    HorizontalDivider(
                        modifier  = Modifier.width(16.dp),
                        color     = MaterialTheme.colorScheme.outlineVariant,
                        thickness = 1.dp
                    )
                }
            }
        }
        HorizontalDivider(
            color     = MaterialTheme.colorScheme.outlineVariant,
            thickness = 0.5.dp
        )
    }
}

// ── FORM SECTION WRAPPER ──────────────────────────────────────────────────────

@Composable
fun FormSection(
    title   : String,
    icon    : ImageVector,
    iconBg  : Color,
    iconTint: Color,
    content : @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Section header row
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier              = Modifier.padding(bottom = 14.dp)
            ) {
                Box(
                    modifier         = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector  = icon,
                        contentDescription = title,
                        tint         = iconTint,
                        modifier     = Modifier.size(17.dp)
                    )
                }
                Text(
                    title,
                    style      = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color      = MaterialTheme.colorScheme.onSurface
                )
            }

            HorizontalDivider(
                color     = MaterialTheme.colorScheme.outlineVariant,
                thickness = 0.5.dp,
                modifier  = Modifier.padding(bottom = 14.dp)
            )

            content()
        }
    }
}

// ── REUSABLE TEXT FIELD ───────────────────────────────────────────────────────

@Composable
fun ProfileTextField(
    label        : String,
    value        : String,
    placeholder  : String,
    required     : Boolean = false,
    isError      : Boolean = false,
    errorMsg     : String  = "",
    singleLine   : Boolean = true,
    maxLines     : Int     = 1,
    onValueChange: (String) -> Unit
) {
    Column {
        // Label row — asterisk suffix for required fields
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text  = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )
            if (required) {
                Spacer(Modifier.width(3.dp))
                Text(
                    text  = "*",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(Modifier.height(5.dp))

        OutlinedTextField(
            value         = value,
            onValueChange = onValueChange,
            placeholder   = {
                Text(placeholder, style = MaterialTheme.typography.bodyMedium)
            },
            singleLine = singleLine,
            maxLines   = maxLines,
            isError    = isError,
            shape      = RoundedCornerShape(12.dp),
            modifier   = Modifier.fillMaxWidth(),
            colors     = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                errorBorderColor     = MaterialTheme.colorScheme.error,
                errorContainerColor  = MaterialTheme.colorScheme.errorContainer
            )
        )

        if (isError && errorMsg.isNotBlank()) {
            Text(
                text     = errorMsg,
                color    = MaterialTheme.colorScheme.error,
                style    = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp, top = 3.dp)
            )
        }
    }
}