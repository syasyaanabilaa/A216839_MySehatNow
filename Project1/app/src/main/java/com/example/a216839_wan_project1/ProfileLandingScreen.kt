package com.example.a216839_wan_project1

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import com.example.a216839_wan_project1.ui.theme.*

// ── DATA ──────────────────────────────────────────────────────────────────────
private data class ProfileFeature(
    val icon    : ImageVector,
    val iconBg  : Color,
    val iconTint: Color,
    val title   : String,
    val subtitle: String
)

private val profileFeatures = listOf(
    ProfileFeature(Icons.Outlined.Person,          BgCal,   ColorCal,   "Personal Info",     "Name, IC number & date of birth"),
    ProfileFeature(Icons.Outlined.MonitorHeart,    BgHeart, ColorHeart, "Health Details",    "Blood type, allergies & conditions"),
    ProfileFeature(Icons.Outlined.MedicalServices, BgTrack, ColorTrack, "Medical History",   "Past diagnoses & medications"),
    ProfileFeature(Icons.Outlined.ContactPhone,    BgFam,   ColorFam,   "Emergency Contact", "Next-of-kin & contact number")
)

// ── SCREEN ────────────────────────────────────────────────────────────────────
@Composable
fun ProfileLandingScreen(
    onStartSetup: () -> Unit,
    onBack      : () -> Unit
) {
    val scrollState = rememberScrollState()

    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.55f,
        targetValue   = 0.85f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmerAlpha"
    )

    Scaffold(containerColor = BackgroundGray) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {

            // ── HERO ──────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Brush.linearGradient(listOf(PurpleHeader, BlueHeader)))
            ) {
                Box(
                    Modifier
                        .size(180.dp)
                        .offset(x = 80.dp, y = (-40).dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.07f))
                        .align(Alignment.TopEnd)
                )
                Box(
                    Modifier
                        .size(110.dp)
                        .offset(x = (-50).dp, y = 30.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.05f))
                        .align(Alignment.BottomStart)
                )

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

                Row(
                    modifier          = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 20.dp, bottom = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = shimmerAlpha)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.AccountCircle,
                            contentDescription = null,
                            tint     = PurpleHeader,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Text(
                            "My Health Profile",
                            color      = Color.White,
                            fontSize   = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Set up your profile for personalised care",
                            color    = Color.White.copy(alpha = 0.80f),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(22.dp))

            Text(
                "What's included",
                style      = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.onBackground,
                modifier   = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(10.dp))

            Card(
                modifier  = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape     = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    profileFeatures.forEachIndexed { index, feature ->
                        LandingFeatureRow(feature = feature)
                        if (index < profileFeatures.lastIndex) {
                            HorizontalDivider(
                                color     = MaterialTheme.colorScheme.outlineVariant,
                                thickness = 1.dp,
                                modifier  = Modifier.padding(start = 58.dp)
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier              = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Outlined.Lock, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                Text(
                    "Your data is encrypted and stored securely.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(28.dp))

            Button(
                onClick  = onStartSetup,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(52.dp),
                shape    = RoundedCornerShape(16.dp),
                colors   = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor   = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Start Profile Setup", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick  = onBack,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(48.dp),
                shape    = RoundedCornerShape(16.dp)
            ) {
                Text("Maybe Later", fontWeight = FontWeight.Medium)
            }

            Spacer(Modifier.height(28.dp))
        }
    }
}

// ── FEATURE ROW ───────────────────────────────────────────────────────────────
@Composable
private fun LandingFeatureRow(feature: ProfileFeature) {
    Row(
        modifier          = Modifier.fillMaxWidth().padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier         = Modifier.size(44.dp).clip(RoundedCornerShape(14.dp)).background(feature.iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(feature.icon, contentDescription = feature.title, tint = feature.iconTint, modifier = Modifier.size(24.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(feature.title,    style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Text(feature.subtitle, style = MaterialTheme.typography.bodySmall,  color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
    }
}