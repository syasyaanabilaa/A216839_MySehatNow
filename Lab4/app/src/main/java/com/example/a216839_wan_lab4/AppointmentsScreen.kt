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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a216839_wan_lab4.ui.theme.*

private data class Appointment(
    val title: String, val facility: String, val date: String, val time: String,
    val type: String, val color: Color, val bg: Color
)

private val appointments = listOf(
    Appointment("Blood Pressure Check", "KK Cheras",       "25 Jul 2025", "9:00 AM",  "Upcoming",  ColorHeart, BgHeart),
    Appointment("General Consultation", "KK Kajang",       "20 Jul 2025", "10:30 AM", "Upcoming",  ColorCal,   BgCal),
    Appointment("Diabetes Follow-Up",   "Hospital Kajang", "10 Jul 2025", "2:15 PM",  "Completed", ColorTrack, BgTrack),
    Appointment("Eye Screening",        "KK Bangi",        "5 Jun 2025",  "8:45 AM",  "Completed", ColorFam,   BgFam),
    Appointment("Dental Check",         "KK Sg Chua",      "20 Apr 2025", "11:00 AM", "Completed", ColorLoc,   BgLoc)
)

@Composable
fun AppointmentsScreen(onBack: () -> Unit) {
    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding).verticalScroll(rememberScrollState())) {
            Box(modifier = Modifier.fillMaxWidth().height(160.dp).background(Brush.linearGradient(listOf(PurpleHeader, BlueHeader)))) {
                IconButton(onClick = onBack, modifier = Modifier.padding(12.dp).size(42.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.15f)).align(Alignment.TopStart)) {
                    Icon(Icons.Filled.ArrowBack, "Back", tint = Color.White)
                }
                Column(modifier = Modifier.align(Alignment.BottomStart).padding(start = 20.dp, bottom = 20.dp)) {
                    Text("My Appointments", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                    Text("${appointments.count { it.type == "Upcoming" }} upcoming  •  ${appointments.count { it.type == "Completed" }} completed", color = Color.White.copy(alpha = 0.80f), fontSize = 13.sp)
                }
            }
            Spacer(Modifier.height(20.dp))
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                appointments.forEach { appt -> AppointmentCard(appt); Spacer(Modifier.height(12.dp)) }
                Spacer(Modifier.height(8.dp))
                Button(onClick = {}, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                    Icon(Icons.Outlined.CalendarToday, "Book", modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Book New Appointment", fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(28.dp))
            }
        }
    }
}

@Composable
private fun AppointmentCard(appt: Appointment) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(18.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(50.dp).clip(RoundedCornerShape(14.dp)).background(appt.bg), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.CalendarToday, appt.title, tint = appt.color, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(appt.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(appt.facility, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Outlined.CalendarToday, "Date", modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(appt.date, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Outlined.Schedule, "Time", modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(appt.time, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            Spacer(Modifier.width(8.dp))
            Surface(shape = RoundedCornerShape(20.dp), color = if (appt.type == "Upcoming") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondaryContainer) {
                Text(appt.type, color = if (appt.type == "Upcoming") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSecondaryContainer, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
            }
        }
    }
}