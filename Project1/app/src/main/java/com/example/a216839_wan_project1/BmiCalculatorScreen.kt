package com.example.a216839_wan_project1

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a216839_wan_project1.ui.theme.BlueHeader
import com.example.a216839_wan_project1.ui.theme.PurpleHeader
import kotlin.math.pow

// ── BMI RESULT ────────────────────────────────────────────────────────────────
private data class BmiResult(
    val bmi     : Double,
    val category: String,
    val advice  : String,
    val color   : Color,
    val bgColor : Color
)

private fun calculateBmi(weightKg: Double, heightCm: Double): BmiResult {
    val heightM = heightCm / 100.0
    val bmi     = weightKg / heightM.pow(2)
    val rounded = Math.round(bmi * 10.0) / 10.0
    return when {
        bmi < 18.5 -> BmiResult(rounded, "Underweight",   "You may need to increase your caloric intake. Consider consulting a dietitian for a personalised meal plan.",              Color(0xFF1565C0), Color(0xFFE3F2FD))
        bmi < 25.0 -> BmiResult(rounded, "Normal Weight", "Great job! Maintain your healthy weight through a balanced diet and at least 30 minutes of exercise most days.",          Color(0xFF2E7D32), Color(0xFFE8F5E9))
        bmi < 30.0 -> BmiResult(rounded, "Overweight",    "Consider a balanced diet and more physical activity. A 30-minute brisk walk daily can make a big difference.",           Color(0xFFE65100), Color(0xFFFFF3E0))
        else       -> BmiResult(rounded, "Obese",          "Please consult a healthcare professional for a personalised weight management plan and medical advice.",                  Color(0xFFC62828), Color(0xFFFFEBEE))
    }
}

// ── SCREEN ────────────────────────────────────────────────────────────────────
@Composable
fun BmiCalculatorScreen(onBack: () -> Unit) {

    var weightInput by remember { mutableStateOf("") }
    var heightInput by remember { mutableStateOf("") }
    var result      by remember { mutableStateOf<BmiResult?>(null) }
    var showError   by remember { mutableStateOf(false) }

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
                    Text("BMI Calculator",           color = Color.White,                     fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                    Text("Check your Body Mass Index", color = Color.White.copy(alpha = 0.80f), fontSize = 13.sp)
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

                    Text("Enter Your Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(16.dp))

                    // Weight
                    Text("Weight (kg)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value           = weightInput,
                        onValueChange   = { weightInput = it; showError = false; result = null },
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

                    // Height
                    Text("Height (cm)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(6.dp))
                    OutlinedTextField(
                        value           = heightInput,
                        onValueChange   = { heightInput = it; showError = false; result = null },
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

                    // Calculate button
                    Button(
                        onClick = {
                            val w = weightInput.toDoubleOrNull()
                            val h = heightInput.toDoubleOrNull()
                            if (w == null || h == null || w <= 0 || h <= 0) {
                                showError = true
                                result    = null
                            } else {
                                showError = false
                                result    = calculateBmi(w, h)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape    = RoundedCornerShape(16.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor   = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(Icons.Outlined.Calculate, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Calculate BMI", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }

                    Spacer(Modifier.height(8.dp))

                    // Reset button
                    OutlinedButton(
                        onClick  = { weightInput = ""; heightInput = ""; result = null; showError = false },
                        modifier = Modifier.fillMaxWidth().height(44.dp),
                        shape    = RoundedCornerShape(16.dp)
                    ) {
                        Text("Reset", fontWeight = FontWeight.Medium)
                    }
                }
            }

            // ── Result ────────────────────────────────────────────────────────
            result?.let { r ->
                Spacer(Modifier.height(20.dp))

                Card(
                    modifier  = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)),
                    shape     = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    colors    = CardDefaults.cardColors(containerColor = r.bgColor)
                ) {
                    Column(
                        modifier            = Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Your BMI", style = MaterialTheme.typography.titleMedium, color = r.color, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        Text("${r.bmi}", fontSize = 56.sp, fontWeight = FontWeight.ExtraBold, color = r.color)
                        Spacer(Modifier.height(4.dp))
                        Surface(shape = RoundedCornerShape(20.dp), color = r.color) {
                            Text(r.category, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp))
                        }
                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider(color = r.color.copy(alpha = 0.25f))
                        Spacer(Modifier.height(12.dp))
                        Text(r.advice, style = MaterialTheme.typography.bodyMedium, color = r.color, textAlign = TextAlign.Center, lineHeight = 22.sp)
                    }
                }

                Spacer(Modifier.height(20.dp))
                BmiReferenceTable()
            }

            Spacer(Modifier.height(28.dp))
        }
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
                if (index < 3) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 0.5.dp)
                }
            }
        }
    }
}