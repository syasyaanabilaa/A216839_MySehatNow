package com.example.a216839_wan_project2

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a216839_wan_project2.data.entity.AppointmentEntity
import com.example.a216839_wan_project2.viewmodel.AppointmentViewModel
import com.example.a216839_wan_project2.ui.theme.*
import androidx.compose.foundation.horizontalScroll

// ── Appointment types the user can choose from ──────────────────────────────
private val APPOINTMENT_TYPES = listOf(
    "General Consultation",
    "Blood Pressure Check",
    "Diabetes Follow-Up",
    "Eye Screening",
    "Dental Check-Up",
    "Other"
)

// ── Simple date options (replace with DatePickerDialog for real use) ─────────
private fun generateDateOptions(): List<String> {
    // Returns next 14 days as human-readable strings.
    // In production, wire up android.app.DatePickerDialog or
    // androidx.compose.material3's experimental DatePicker instead.
    val cal = java.util.Calendar.getInstance()
    val months = listOf(
        "Jan","Feb","Mar","Apr","May","Jun",
        "Jul","Aug","Sep","Oct","Nov","Dec"
    )
    return (0 until 14).map { offset ->
        val c = cal.clone() as java.util.Calendar
        c.add(java.util.Calendar.DAY_OF_YEAR, offset)
        val day  = c.get(java.util.Calendar.DAY_OF_MONTH)
        val mon  = months[c.get(java.util.Calendar.MONTH)]
        val year = c.get(java.util.Calendar.YEAR)
        "$day $mon $year"
    }
}

private val TIME_OPTIONS = listOf(
    "08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM",
    "12:00 PM", "02:00 PM", "03:00 PM", "04:00 PM"
)

// ── Main screen ──────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentsScreen(
    viewModel: AppointmentViewModel,
    onBack: () -> Unit
) {
    val appointments  by viewModel.appointments.collectAsState()
    val upcomingCount by viewModel.upcomingCount.collectAsState()
    val completedCount by viewModel.completedCount.collectAsState()

    // Bottom-sheet state
    var showSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {

            // ── HEADER ──────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(Brush.linearGradient(listOf(PurpleHeader, BlueHeader)))
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .padding(12.dp)
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .align(Alignment.TopStart)
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 20.dp, bottom = 20.dp)
                ) {
                    Text(
                        text = "My Appointments",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "$upcomingCount upcoming • $completedCount completed",
                        color = Color.White.copy(alpha = 0.80f),
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {

                if (appointments.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No appointments yet.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    appointments.forEach { appt ->
                        AppointmentCard(appt = appt, onDelete = { viewModel.deleteAppointment(it) })
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // ── BOOK BUTTON ─────────────────────────────────────────────
                Button(
                    onClick = { showSheet = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Outlined.CalendarToday, contentDescription = "Book")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Book New Appointment", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }

    // ── BOOKING BOTTOM SHEET ─────────────────────────────────────────────────
    if (showSheet) {
        BookingBottomSheet(
            sheetState = sheetState,
            onDismiss = { showSheet = false },
            onConfirm = { type, date, time ->
                viewModel.addAppointment(
                    AppointmentEntity(
                        title    = type,
                        facility = "UKM Health Centre",
                        date     = date,
                        time     = time,
                        type     = "Upcoming"
                    )
                )
                showSheet = false
            }
        )
    }
}

// ── Booking bottom sheet ─────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BookingBottomSheet(
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onConfirm: (type: String, date: String, time: String) -> Unit
) {
    var selectedType by remember { mutableStateOf(APPOINTMENT_TYPES[0]) }
    var selectedDate by remember { mutableStateOf(generateDateOptions()[0]) }
    var selectedTime by remember { mutableStateOf(TIME_OPTIONS[0]) }

    val dateOptions = remember { generateDateOptions() }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Sheet title
            Text(
                text = "Book Appointment",
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Choose appointment type, date, and time",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // ── Step 1: Type ─────────────────────────────────────────────
            SectionLabel(step = "1", label = "Appointment Type")
            Spacer(modifier = Modifier.height(10.dp))

            APPOINTMENT_TYPES.forEach { type ->
                SelectableRow(
                    label = type,
                    selected = selectedType == type,
                    onClick = { selectedType = type }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Step 2: Date ─────────────────────────────────────────────
            SectionLabel(step = "2", label = "Select Date")
            Spacer(modifier = Modifier.height(10.dp))

            // Horizontal-scrollable date chips wrapped in a Box with horizontal scroll
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                dateOptions.forEach { date ->
                    DateChip(
                        label = date,
                        selected = selectedDate == date,
                        onClick = { selectedDate = date }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Step 3: Time ─────────────────────────────────────────────
            SectionLabel(step = "3", label = "Select Time")
            Spacer(modifier = Modifier.height(10.dp))

            // 2-column time grid
            val pairs = TIME_OPTIONS.chunked(2)
            pairs.forEach { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pair.forEach { time ->
                        TimeChip(
                            label = time,
                            selected = selectedTime == time,
                            onClick = { selectedTime = time },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // fill the gap if odd number
                    if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Confirm button ────────────────────────────────────────────
            Button(
                onClick = { onConfirm(selectedType, selectedDate, selectedTime) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Filled.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Confirm Booking", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ── Small helpers ────────────────────────────────────────────────────────────
@Composable
private fun SectionLabel(step: String, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(text = step, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = label, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    }
}

@Composable
private fun SelectableRow(label: String, selected: Boolean, onClick: () -> Unit) {
    val borderColor = if (selected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.outlineVariant
    val bgColor     = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
    else Color.Transparent

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
        if (selected) {
            Icon(
                Icons.Filled.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun DateChip(label: String, selected: Boolean, onClick: () -> Unit) {
    // Split "20 May 2026" → day + rest
    val parts = label.split(" ")
    val day   = parts.getOrNull(0) ?: label
    val mon   = parts.getOrNull(1) ?: ""

    val bg     = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val fg     = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = Modifier
            .width(64.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = day, color = fg, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp)
        Text(text = mon, color = fg.copy(alpha = 0.8f), fontSize = 11.sp)
    }
}

@Composable
private fun TimeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.outlineVariant
    val bgColor     = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
    else Color.Transparent

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.5.dp, borderColor, RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Outlined.Schedule,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                color = if (selected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ── Appointment Card (unchanged from original) ───────────────────────────────
@Composable
private fun AppointmentCard(
    appt: AppointmentEntity,
    onDelete: (AppointmentEntity) -> Unit
) {
    val (iconColor, iconBg) = when (appt.title) {
        "Blood Pressure Check" -> Pair(ColorHeart, BgHeart)
        "General Consultation" -> Pair(ColorCal,   BgCal)
        "Diabetes Follow-Up"   -> Pair(ColorTrack, BgTrack)
        "Eye Screening"        -> Pair(ColorFam,   BgFam)
        else                   -> Pair(ColorLoc,   BgLoc)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.CalendarToday, contentDescription = appt.title, tint = iconColor)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = appt.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text(text = appt.facility, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.CalendarToday, contentDescription = "Date", modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = appt.date, style = MaterialTheme.typography.labelSmall)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Schedule, contentDescription = "Time", modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = appt.time, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            IconButton(onClick = { onDelete(appt) }) {
                Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}