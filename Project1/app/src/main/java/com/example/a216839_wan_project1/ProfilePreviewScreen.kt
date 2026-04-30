package com.example.a216839_wan_project1

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a216839_wan_project1.ui.theme.BlueHeader
import com.example.a216839_wan_project1.ui.theme.PurpleHeader

// ── SCREEN ────────────────────────────────────────────────────────────────────
@Composable
fun ProfilePreviewScreen(
    viewModel: HealthProfileViewModel,
    onEdit   : () -> Unit,
    onHome   : () -> Unit
) {
    val profile = viewModel.profile

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            PreviewHeader(profile = profile, onBack = onEdit)
            SuccessStrip()
            QuickStatTiles(profile = profile)
            Spacer(Modifier.height(16.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {

                PreviewSection(
                    title    = "Personal Information",
                    icon     = Icons.Outlined.Person,
                    iconBg   = Color(0xFFEDE8FB),
                    iconTint = Color(0xFF6C3FC8),
                    onEdit   = onEdit
                ) {
                    PreviewRow("Full Name",     profile.fullName.ifBlank { "—" })
                    PreviewRow("IC Number",     profile.icNumber.ifBlank { "—" })
                    PreviewRow("Date of Birth", profile.dateOfBirth.ifBlank { "—" })
                    PreviewRowBloodType(profile.bloodType.ifBlank { "—" })
                }

                Spacer(Modifier.height(12.dp))

                PreviewSection(
                    title    = "Medical Information",
                    icon     = Icons.Outlined.MedicalServices,
                    iconBg   = Color(0xFFFDE8EF),
                    iconTint = Color(0xFFC0496E),
                    onEdit   = onEdit
                ) {
                    PreviewRow("Allergies",  profile.allergies.ifBlank  { "None stated" }, empty = profile.allergies.isBlank())
                    PreviewRow("Conditions", profile.conditions.ifBlank { "None stated" }, empty = profile.conditions.isBlank())
                }

                Spacer(Modifier.height(12.dp))

                PreviewSection(
                    title    = "Emergency Contact",
                    icon     = Icons.Outlined.ContactPhone,
                    iconBg   = Color(0xFFE8F5F0),
                    iconTint = Color(0xFF1D9E75),
                    onEdit   = onEdit
                ) {
                    PreviewRow("Contact Name",  profile.emergencyName.ifBlank  { "—" })
                    PreviewRow("Phone Number",  profile.emergencyPhone.ifBlank { "—" })
                }

                Spacer(Modifier.height(28.dp))

                Button(
                    onClick  = onEdit,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape    = RoundedCornerShape(16.dp),
                    colors   = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor   = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.Outlined.Edit, "Edit", modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Edit Profile", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                Spacer(Modifier.height(10.dp))

                OutlinedButton(
                    onClick  = onHome,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape    = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Filled.Home, "Home", modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Back to Home", fontWeight = FontWeight.Medium)
                }

                Spacer(Modifier.height(28.dp))
            }
        }
    }
}

// ── HEADER ────────────────────────────────────────────────────────────────────
@Composable
fun PreviewHeader(profile: HealthProfile, onBack: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
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
            Icon(Icons.Filled.ArrowBack, "Back", tint = Color.White)
        }

        Column(
            modifier            = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier         = Modifier.size(72.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = if (profile.fullName.isNotBlank()) profile.fullName.first().uppercase() else "?",
                    color      = Color.White,
                    fontSize   = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(profile.fullName.ifBlank { "—" }, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(profile.icNumber.ifBlank { "IC not provided" }, color = Color.White.copy(alpha = 0.72f), fontSize = 12.sp)
        }
    }
}

// ── SUCCESS STRIP ─────────────────────────────────────────────────────────────
@Composable
fun SuccessStrip() {
    Surface(modifier = Modifier.fillMaxWidth(), color = MaterialTheme.colorScheme.tertiaryContainer) {
        Row(
            modifier              = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Filled.CheckCircle, contentDescription = "Saved", tint = MaterialTheme.colorScheme.tertiary, modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(7.dp))
            Text("Profile saved successfully", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onTertiaryContainer, fontWeight = FontWeight.SemiBold)
        }
    }
}

// ── QUICK STAT TILES ──────────────────────────────────────────────────────────
@Composable
fun QuickStatTiles(profile: HealthProfile) {
    val conditionCount = profile.conditions.split(",").map { it.trim() }.count { it.isNotBlank() }
    val allergyDisplay = if (profile.allergies.isBlank()) "None" else profile.allergies.split(",").first().trim()

    Row(
        modifier              = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        listOf(
            Pair("Blood Type",  profile.bloodType.ifBlank { "—" }),
            Pair("Allergies",   allergyDisplay),
            Pair("Conditions",  if (conditionCount == 0) "None" else conditionCount.toString())
        ).forEach { (label, value) ->
            Card(
                modifier  = Modifier.weight(1f),
                shape     = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier            = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(3.dp))
                    Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
                }
            }
        }
    }
}

// ── PREVIEW SECTION ───────────────────────────────────────────────────────────
@Composable
fun PreviewSection(
    title   : String,
    icon    : ImageVector,
    iconBg  : Color,
    iconTint: Color,
    onEdit  : () -> Unit,
    content : @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            Row(
                modifier              = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                    Box(
                        modifier         = Modifier.size(30.dp).clip(RoundedCornerShape(9.dp)).background(iconBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, title, tint = iconTint, modifier = Modifier.size(16.dp))
                    }
                    Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                }
                Surface(onClick = onEdit, shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                    Text("Edit", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
            content()
        }
    }
}

// ── FIELD ROW ─────────────────────────────────────────────────────────────────
@Composable
fun PreviewRow(label: String, value: String, empty: Boolean = false) {
    Row(
        modifier              = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 11.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.Top
    ) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(0.42f))
        Text(
            value,
            style      = MaterialTheme.typography.bodySmall,
            color      = if (empty) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (empty) FontWeight.Normal else FontWeight.SemiBold,
            fontStyle  = if (empty) FontStyle.Italic  else FontStyle.Normal,
            textAlign  = TextAlign.End,
            modifier   = Modifier.weight(0.58f)
        )
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 14.dp))
}

// ── BLOOD TYPE BADGE ──────────────────────────────────────────────────────────
@Composable
fun PreviewRowBloodType(value: String) {
    Row(
        modifier              = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 9.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text("Blood Type", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(0.42f))
        Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primaryContainer) {
            Text(value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
        }
    }
}