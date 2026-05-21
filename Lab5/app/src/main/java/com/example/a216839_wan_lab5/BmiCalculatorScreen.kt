package com.example.a216839_wan_lab5

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a216839_wan_lab5.viewmodel.BmiResult
import com.example.a216839_wan_lab5.viewmodel.BmiViewModel
import com.example.a216839_wan_lab5.ui.theme.BlueHeader
import com.example.a216839_wan_lab5.ui.theme.PurpleHeader

// ── SCREEN ────────────────────────────────────────────────────────────────────
@Composable
fun BmiCalculatorScreen(
    viewModel: BmiViewModel,
    onBack   : () -> Unit
) {
    var weightInput by remember { mutableStateOf("") }
    var heightInput by remember { mutableStateOf("") }
    var showError   by remember { mutableStateOf(false) }

    val result by viewModel.currentResult.collectAsState()
    val history by viewModel.bmiHistory.collectAsState()

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // ── Header ────────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
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
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 20.dp, bottom = 20.dp)
                ) {
                    Text("BMI Calculator",              color = Color.White,                     fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Check your Body Mass Index",  color = Color.White.copy(alpha = 0.80f), fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Input Card ────────────────────────────────────────────────────
            Card(
                modifier  = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                shape     = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Enter Your Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(16.dp))

                    Text("Weight (kg)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value           = weightInput,
                        onValueChange   = { weightInput = it; showError = false; viewModel.clearResult() },
                        placeholder     = { Text("e.g. 65") },
                        singleLine      = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError         = showError && weightInput.isBlank(),
                        shape           = RoundedCornerShape(12.dp),
                        modifier        = Modifier.fillMaxWidth(),
                        leadingIcon     = { Icon(Icons.Outlined.MonitorWeight, null, tint = MaterialTheme.colorScheme.primary) },
                        suffix          = { Text("kg") }
                    )

                    Spacer(Modifier.height(14.dp))

                    Text("Height (cm)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value           = heightInput,
                        onValueChange   = { heightInput = it; showError = false; viewModel.clearResult() },
                        placeholder     = { Text("e.g. 165") },
                        singleLine      = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        isError         = showError && heightInput.isBlank(),
                        shape           = RoundedCornerShape(12.dp),
                        modifier        = Modifier.fillMaxWidth(),
                        leadingIcon     = { Icon(Icons.Outlined.Height, null, tint = MaterialTheme.colorScheme.primary) },
                        suffix          = { Text("cm") }
                    )

                    if (showError) {
                        Spacer(Modifier.height(6.dp))
                        Text("Please enter valid weight and height values.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = {
                            val w = weightInput.toDoubleOrNull()
                            val h = heightInput.toDoubleOrNull()
                            if (w == null || h == null || w <= 0 || h <= 0) {
                                showError = true
                            } else {
                                showError = false
                                viewModel.calculate(w, h)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape    = RoundedCornerShape(16.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Outlined.Calculate, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Calculate BMI", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Spacer(Modifier.height(8.dp))

                    OutlinedButton(
                        onClick  = { weightInput = ""; heightInput = ""; showError = false; viewModel.clearResult() },
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape    = RoundedCornerShape(16.dp)
                    ) {
                        Text("Reset", fontWeight = FontWeight.Medium)
                    }
                }
            }

            // ── Result + Zone Chart ───────────────────────────────────────────
            result?.let { r ->
                Spacer(Modifier.height(20.dp))
                BmiResultCard(r)

                Spacer(Modifier.height(16.dp))
                BmiZoneChart(
                    bmi      = r.bmi,
                    heightCm = heightInput.toDoubleOrNull() ?: 165.0,
                    weightKg = weightInput.toDoubleOrNull() ?: 65.0
                )
            }

            // ── Reference Table ───────────────────────────────────────────────
            if (result != null) {
                Spacer(Modifier.height(16.dp))
                BmiReferenceTable()
            }

            // ── History ───────────────────────────────────────────────────────
            if (history.isNotEmpty()) {
                Spacer(Modifier.height(20.dp))
                BmiHistoryCard(history = history, onDelete = { viewModel.deleteRecord(it) }, onClearAll = { viewModel.clearHistory() })
            }

            Spacer(Modifier.height(28.dp))
        }
    }
}

// ── RESULT CARD ───────────────────────────────────────────────────────────────
@Composable
private fun BmiResultCard(r: BmiResult) {
    val bgColor    = Color(android.graphics.Color.parseColor(r.bgColorHex))
    val accentColor = Color(android.graphics.Color.parseColor(r.colorHex))

    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)),
        shape     = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors    = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier            = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Your BMI", style = MaterialTheme.typography.titleMedium, color = accentColor, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Text("${r.bmi}", fontSize = 56.sp, fontWeight = FontWeight.ExtraBold, color = accentColor)
            Spacer(Modifier.height(4.dp))
            Surface(shape = RoundedCornerShape(20.dp), color = accentColor) {
                Text(r.category, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp))
            }
            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = accentColor.copy(alpha = 0.25f))
            Spacer(Modifier.height(12.dp))
            Text(r.advice, style = MaterialTheme.typography.bodyMedium, color = accentColor, textAlign = TextAlign.Center, lineHeight = 22.sp)
        }
    }
}

// ── BMI ZONE CHART (Canvas) ───────────────────────────────────────────────────
@Composable
fun BmiZoneChart(bmi: Double, heightCm: Double, weightKg: Double) {
    val animatedBmi by animateFloatAsState(
        targetValue   = bmi.toFloat(),
        animationSpec = tween(durationMillis = 800),
        label         = "bmi_anim"
    )

    Card(
        modifier  = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape     = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("BMI Zone Chart", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text("Your position on the BMI scale", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(16.dp))

            // ── Horizontal band chart ────────────────────────────────────────
            val chartBmi = animatedBmi.coerceIn(10f, 42f)
            val fraction = ((chartBmi - 10f) / (42f - 10f)).coerceIn(0f, 1f)

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
            ) {
                val w = size.width
                val h = size.height
                val barTop    = h * 0.25f
                val barBottom = h * 0.65f
                val barHeight = barBottom - barTop
                val radius    = barHeight / 2f

                // Zone fractions: underweight 0–18.5, normal 18.5–25, overweight 25–30, obese 30–42
                val zones = listOf(
                    Triple(10f,  18.5f, Color(0xFF2196F3)),  // blue  - underweight
                    Triple(18.5f,25f,   Color(0xFF4CAF50)),  // green - normal
                    Triple(25f,  30f,   Color(0xFFFFC107)),  // amber - overweight
                    Triple(30f,  42f,   Color(0xFFF44336))   // red   - obese
                )
                val totalRange = 42f - 10f

                zones.forEachIndexed { index, (minV, maxV, color) ->
                    val startFrac = (minV - 10f) / totalRange
                    val endFrac   = (maxV - 10f) / totalRange
                    val x0 = w * startFrac
                    val x1 = w * endFrac
                    val path = Path().apply {
                        when (index) {
                            0 -> {  // leftmost — round left corners only
                                moveTo(x0 + radius, barTop)
                                lineTo(x1, barTop)
                                lineTo(x1, barBottom)
                                lineTo(x0 + radius, barBottom)
                                arcTo(
                                    rect = androidx.compose.ui.geometry.Rect(x0, barTop, x0 + radius * 2, barBottom),
                                    startAngleDegrees = 90f, sweepAngleDegrees = 180f, forceMoveTo = false
                                )
                            }
                            zones.lastIndex -> {  // rightmost — round right corners only
                                moveTo(x0, barTop)
                                lineTo(x1 - radius, barTop)
                                arcTo(
                                    rect = androidx.compose.ui.geometry.Rect(x1 - radius * 2, barTop, x1, barBottom),
                                    startAngleDegrees = 270f, sweepAngleDegrees = 180f, forceMoveTo = false
                                )
                                lineTo(x0, barBottom)
                                close()
                            }
                            else -> {
                                moveTo(x0, barTop)
                                lineTo(x1, barTop)
                                lineTo(x1, barBottom)
                                lineTo(x0, barBottom)
                                close()
                            }
                        }
                    }
                    drawPath(path, color)
                }

                // Indicator line
                val dotX = w * fraction
                drawLine(
                    color       = Color.Black.copy(alpha = 0.6f),
                    start       = Offset(dotX, barTop - 6f),
                    end         = Offset(dotX, barBottom + 6f),
                    strokeWidth = 2.dp.toPx(),
                    cap         = StrokeCap.Round
                )

                // Indicator dot
                val dotY = (barTop + barBottom) / 2f
                drawCircle(color = Color.White,              radius = 10.dp.toPx(), center = Offset(dotX, dotY))
                drawCircle(color = Color.Black.copy(alpha=0.8f), radius = 10.dp.toPx(), center = Offset(dotX, dotY), style = Stroke(width = 2.dp.toPx()))
                drawCircle(color = Color.Black,              radius =  4.dp.toPx(), center = Offset(dotX, dotY))

                // Zone labels below the bar
                val labelY = barBottom + 18.dp.toPx()
                drawBmiLabel(this, "Underweight", (10f + 18.5f) / 2f, 10f, 42f, w, labelY)
                drawBmiLabel(this, "Normal",      (18.5f + 25f) / 2f, 10f, 42f, w, labelY)
                drawBmiLabel(this, "Overweight",  (25f + 30f)   / 2f, 10f, 42f, w, labelY)
                drawBmiLabel(this, "Obese",       (30f + 42f)   / 2f, 10f, 42f, w, labelY)
            }

            Spacer(Modifier.height(8.dp))

            // Numeric scale row
            Row(
                modifier              = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("10", "18.5", "25", "30", "42").forEach { label ->
                    Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(Modifier.height(12.dp))

            // BMI summary row
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                BmiStatChip("Height", "${heightCm.toInt()} cm")
                BmiStatChip("Weight", "${String.format("%.1f", weightKg)} kg")
                BmiStatChip("BMI",    "${String.format("%.1f", bmi)}")
            }
        }
    }
}

// Helper to draw small text labels in Canvas
private fun drawBmiLabel(
    scope   : DrawScope,
    text    : String,
    midBmi  : Float,
    minBmi  : Float,
    maxBmi  : Float,
    width   : Float,
    y       : Float
) {
    val fraction = (midBmi - minBmi) / (maxBmi - minBmi)
    val x        = width * fraction
    val paint    = android.graphics.Paint().apply {
        textSize  = 22f
        color     = android.graphics.Color.argb(150, 0, 0, 0)
        textAlign = android.graphics.Paint.Align.CENTER
        isAntiAlias = true
    }
    scope.drawContext.canvas.nativeCanvas.drawText(text, x, y, paint)
}

@Composable
private fun BmiStatChip(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
        Text(label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

// ── BMI REFERENCE TABLE ───────────────────────────────────────────────────────
@Composable
private fun BmiReferenceTable() {
    Card(
        modifier  = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape     = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("BMI Reference Guide", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(12.dp))
            listOf(
                Triple("< 18.5",      "Underweight",   Color(0xFF1565C0)),
                Triple("18.5 – 24.9", "Normal Weight", Color(0xFF2E7D32)),
                Triple("25.0 – 29.9", "Overweight",    Color(0xFFE65100)),
                Triple("≥ 30.0",      "Obese",         Color(0xFFC62828))
            ).forEachIndexed { index, (range, label, color) ->
                Row(
                    modifier              = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(range, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
                    Surface(shape = RoundedCornerShape(8.dp), color = color.copy(alpha = 0.12f)) {
                        Text(label, color = color, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                    }
                }
                if (index < 3) HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
            }
        }
    }
}

// ── BMI HISTORY CARD ──────────────────────────────────────────────────────────
@Composable
private fun BmiHistoryCard(
    history    : List<com.example.a216839_wan_lab5.data.entity.BmiRecordEntity>,
    onDelete   : (com.example.a216839_wan_lab5.data.entity.BmiRecordEntity) -> Unit,
    onClearAll : () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape     = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("Recent Records", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                TextButton(onClick = onClearAll) {
                    Text("Clear All", fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                }
            }
            Spacer(Modifier.height(8.dp))
            history.take(5).forEach { record ->
                val color = when (record.category) {
                    "Underweight"   -> Color(0xFF1565C0)
                    "Normal Weight" -> Color(0xFF2E7D32)
                    "Overweight"    -> Color(0xFFE65100)
                    else            -> Color(0xFFC62828)
                }
                Row(
                    modifier              = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column {
                        Text("BMI ${String.format("%.1f", record.bmi)}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("${record.weightKg}kg · ${record.heightCm}cm", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Surface(shape = RoundedCornerShape(8.dp), color = color.copy(alpha = 0.12f)) {
                            Text(record.category, color = color, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                        }
                        IconButton(onClick = { onDelete(record) }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Outlined.Delete, "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
            }
        }
    }
}