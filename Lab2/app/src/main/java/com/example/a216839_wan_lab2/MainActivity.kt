package com.example.a216839_wan_lab2
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.GpsFixed
import androidx.compose.material.icons.outlined.Handshake
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.MonitorHeart
import androidx.compose.material.icons.outlined.PeopleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// Header gradient
val PurpleHeader   = Color(0xFF7B2FF7)
val BlueHeader     = Color(0xFF3B6FE8)
// General UI
val IconBlueBg     = Color(0xFFEEF2FF)
val CardWhite      = Color(0xFFFFFFFF)
val BackgroundGray = Color(0xFFF4F6FA)
val TextDark       = Color(0xFF1A1A2E)
val TextGray       = Color(0xFF888888)
val AccentBlue     = Color(0xFF2B5CE6)
val GoldColor      = Color(0xFFFFD700)
val DividerColor   = Color(0xFFEEEEEE)
// My Service icon colors & backgrounds
val ColorCal   = Color(0xFF5C6BC0) ; val BgCal   = Color(0xFFE8EAF6)
val ColorTrack = Color(0xFFEF5350) ; val BgTrack = Color(0xFFFFEBEE)
val ColorLoc   = Color(0xFF26A69A) ; val BgLoc   = Color(0xFFE0F2F1)
val ColorHeart = Color(0xFFEC407A) ; val BgHeart = Color(0xFFFCE4EC)
val ColorFam   = Color(0xFFFF7043) ; val BgFam   = Color(0xFFFBE9E7)
val ColorBook  = Color(0xFFA142F5) ; val BgBook  = Color(0xFFE3F2FD)
val BgHandshake = Color(0xFFEFFF3C)

// ENTRY POINT
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { HomeScreen() }
    }
}
// ROOT SCREEN  —  wires scroll state, FAB, bottom bar, and main content
@Composable
fun HomeScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val scrollState  = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val showFab by remember { derivedStateOf { scrollState.value > 300 } }

    Scaffold(
        containerColor = BackgroundGray,
        bottomBar = {
            BottomNavigationBar(selectedTab) { selectedTab = it }
        },
        floatingActionButton = {
            if (showFab) {
                FloatingActionButton(
                    onClick = { coroutineScope.launch { scrollState.animateScrollTo(0) } },
                    containerColor = AccentBlue,
                    contentColor   = Color.White,
                    shape          = CircleShape,
                    modifier       = Modifier.size(52.dp)
                ) {
                    Icon(
                        imageVector        = Icons.Filled.KeyboardArrowUp,
                        contentDescription = "Scroll to top",
                        modifier           = Modifier.size(28.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            HomeUI(scrollState)
        }
    }
}
// BOTTOM NAVIGATION BAR
@Composable
fun BottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    data class NavItem(val label: String, val icon: ImageVector)
    val items = listOf(
        NavItem("Home",           Icons.Filled.Home),
        NavItem("Health Records", Icons.Outlined.Assignment),
        NavItem("Notifications",  Icons.Filled.Notifications),
        NavItem("Settings",       Icons.Filled.Settings)
    )
    NavigationBar(
        containerColor = CardWhite,
        tonalElevation = 0.dp,
        modifier       = Modifier.shadow(elevation = 16.dp)
    ) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedTab == index,
                onClick  = { onTabSelected(index) },
                icon     = { Icon(item.icon, item.label, modifier = Modifier.size(24.dp)) },
                label    = { Text(item.label, fontSize = 10.sp, textAlign = TextAlign.Center) },
                colors   = NavigationBarItemDefaults.colors(
                    selectedIconColor   = AccentBlue,
                    selectedTextColor   = AccentBlue,
                    unselectedIconColor = TextGray,
                    unselectedTextColor = TextGray,
                    indicatorColor      = IconBlueBg
                )
            )
        }
    }
}
// REUSABLE — Pager dot indicators
@Composable
fun PagerDotIndicators(currentPage: Int, pageCount: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            Box(
                modifier = Modifier
                    .size(
                        width  = if (currentPage == index) 20.dp else 8.dp,
                        height = 8.dp
                    )
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (currentPage == index) Color.White
                        else Color.White.copy(alpha = 0.40f)
                    )
            )
        }
    }
}
// MAIN CONTENT — scrollable home page
@Composable
fun HomeUI(scrollState: ScrollState = rememberScrollState()) {
    // ── Local data model ──────────────────────────────────────────────────────
    data class MenuItem(
        val title    : String,
        val subtitle : String,
        val icon     : ImageVector,
        val iconColor: Color,
        val iconBg   : Color,
        val badge    : String? = null
    )
    val menuItems = listOf(
        MenuItem("Appointments",       "Book your next visit",     Icons.Outlined.CalendarToday, ColorCal,   BgCal,   "2"),
        MenuItem("Disease Tracker",    "Monitor your conditions",  Icons.Outlined.GpsFixed,      ColorTrack, BgTrack, "New"),
        MenuItem("Health Facilities",  "Find clinics & hospitals", Icons.Outlined.LocationOn,    ColorLoc,   BgLoc),
        MenuItem("Health Screenings",  "Check your schedule",      Icons.Outlined.MonitorHeart,  ColorHeart, BgHeart, "1"),
        MenuItem("Dependents",         "Manage family members",    Icons.Outlined.PeopleOutline, ColorFam,   BgFam),
        MenuItem("MyDaR",              "Access your records",      Icons.Outlined.MenuBook,      ColorBook,  BgBook),
        MenuItem("Organ Donor Pledge", "Register your pledge",     Icons.Outlined.Handshake,     ColorBook,  BgHandshake)
    )
    // ── Banner pager state + auto-scroll ─────────────────────────────────────
    val pagerState = rememberPagerState(pageCount = { 3 })

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            pagerState.animateScrollToPage((pagerState.currentPage + 1) % 3)
        }
    }
    // ── Symptom checker state ─────────────────────────────────────────────────
    var symptomInput  by remember { mutableStateOf("") }
    var resultMessage by remember { mutableStateOf("") }

    fun getAdvice(input: String): String {
        val kw = input.trim().lowercase()
        if (kw.isEmpty()) {
            return "⚠️ Please enter something first."
        }
        val advice = when {
            kw.contains("fever") ->
                "🌡️ Symptom detected: Fever.\nAdvice: Stay hydrated and rest. Visit a clinic if it persists over 3 days."
            kw.contains("headache") ->
                "🤕 Symptom detected: Headache.\nAdvice: Try resting in a dark room. See a doctor if severe or recurring."
            kw.contains("cough") ->
                "😷 Symptom detected: Cough.\nAdvice: Drink warm fluids. Visit a clinic if it lasts more than 2 weeks."
            kw.contains("dizzy") || kw.contains("dizziness") ->
                "💫 Symptom detected: Dizziness.\nAdvice: Sit or lie down immediately. Seek help if frequent."
            kw.contains("chest") ->
                "❤️ Symptom detected: Chest discomfort.\nAdvice: Please visit a clinic or Emergency immediately."
            kw.contains("nausea") || kw.contains("vomit") ->
                "🤢 Symptom detected: Nausea.\nAdvice: Avoid heavy meals, drink water slowly. See a doctor if it persists."
            kw.contains("fatigue") || kw.contains("tired") ->
                "😴 Symptom detected: Fatigue.\nAdvice: Ensure enough sleep. See a doctor if it continues."
            else ->
                "🔍 No specific symptom detected.\nAdvice: Please consult a healthcare professional for proper diagnosis."
        }
        return "You entered: \"$input\"\n\n$advice"
    }
    // ── Scrollable column ─────────────────────────────────────────────────────
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
            .verticalScroll(scrollState)
    ) {
        // ── Section 1 : Banner + overlapping summary cards ────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
        ) {
            // Banner pager
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .align(Alignment.TopCenter)
            ) {
                HorizontalPager(
                    state    = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (page) {
                        // Page 0 — gradient welcome slide
                        0 -> Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Brush.linearGradient(listOf(PurpleHeader, BlueHeader)))
                        ) {
                            // Decorative translucent circles
                            Box(
                                modifier = Modifier
                                    .size(200.dp)
                                    .offset(x = 100.dp, y = (-50).dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.06f))
                                    .align(Alignment.TopEnd)
                            )
                            Box(
                                modifier = Modifier
                                    .size(130.dp)
                                    .offset(x = 50.dp, y = 30.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.06f))
                                    .align(Alignment.TopEnd)
                            )
                            // Welcome text + CTA
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(start = 20.dp, bottom = 52.dp)
                            ) {
                                Text(
                                    "WELCOME TO",
                                    color         = Color.White.copy(alpha = 0.70f),
                                    fontSize      = 11.sp,
                                    fontWeight    = FontWeight.Medium,
                                    letterSpacing = 2.sp
                                )
                                Spacer(Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("My",       color = GoldColor,   fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                                    Text("SehatNow", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    "Securely access & manage\nyour health records anytime.",
                                    color      = Color.White.copy(alpha = 0.85f),
                                    fontSize   = 12.sp,
                                    lineHeight = 18.sp
                                )
                                Spacer(Modifier.height(12.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color.White.copy(alpha = 0.20f))
                                        .padding(horizontal = 20.dp, vertical = 9.dp)
                                ) {
                                    Text(
                                        "Find out more →",
                                        color      = Color.White,
                                        fontSize   = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                                Spacer(Modifier.height(10.dp))
                                PagerDotIndicators(
                                    currentPage = pagerState.currentPage,
                                    pageCount   = 3
                                )
                            }
                        }
                        // Page 1 — merah
                        1 -> Box(modifier = Modifier.fillMaxSize()) {
                            Image(
                                painter            = painterResource(id = R.drawable.merah),
                                contentDescription = "Hijau Banner",
                                contentScale       = ContentScale.Crop,
                                modifier           = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(start = 20.dp, bottom = 52.dp)
                            ) {
                                PagerDotIndicators(currentPage = pagerState.currentPage, pageCount = 3)
                            }
                        }

                        // Page 2 — image banner (hijauu)
                        2 -> Box(modifier = Modifier.fillMaxSize()) {
                            Image(
                                painter            = painterResource(id = R.drawable.hijauu),
                                contentDescription = "Fasting Banner",
                                contentScale       = ContentScale.Crop,
                                modifier           = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(start = 20.dp, bottom = 52.dp)
                            ) {
                                PagerDotIndicators(currentPage = pagerState.currentPage, pageCount = 3)
                            }
                        }
                    }
                }
                // Static top bar — name & profile icons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart)
                        .padding(horizontal = 20.dp, vertical = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.25f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("S", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Good Morning,",  color = Color.White.copy(alpha = 0.75f), fontSize = 11.sp)
                            Text("SYASYA NABILAH", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            Icons.Filled.Notifications to "Notifications",
                            Icons.Filled.Settings      to "Settings"
                        ).forEach { (icon, desc) ->
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, desc, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
            // Summary stat cards — overlaps bottom of banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 20.dp)
                    .zIndex(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(Modifier.weight(1f), "Appointments", "2", ColorCal,   BgCal)
                StatCard(Modifier.weight(1f), "Screenings",   "1", ColorHeart, BgHeart)
                StatCard(Modifier.weight(1f), "Dependents",   "3", ColorFam,   BgFam)
            }
        }
        Spacer(Modifier.height(12.dp))
        // ── Section 2 : Symptom checker card ─────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .shadow(4.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(CardWhite)
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Text(
                text       = "🩺 Quick Symptom Checker",
                fontSize   = 17.sp,
                fontWeight = FontWeight.Bold,
                color      = TextDark
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text     = "Enter a symptom to get basic health advice",
                fontSize = 12.sp,
                color    = TextGray
            )
            Spacer(Modifier.height(14.dp))
            OutlinedTextField(
                value         = symptomInput,
                onValueChange = { symptomInput = it },
                placeholder   = { Text("Enter Your Symptom", fontSize = 13.sp) },
                singleLine    = true,
                shape         = RoundedCornerShape(14.dp),
                modifier      = Modifier.fillMaxWidth(),
                colors        = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = AccentBlue,
                    unfocusedBorderColor = DividerColor
                )
            )
            Spacer(Modifier.height(10.dp))
            Button(
                onClick  = { resultMessage = getAdvice(symptomInput) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape  = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                Text("Get Advice", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
            if (resultMessage.isNotEmpty()) {
                Spacer(Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(IconBlueBg)
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Text(
                        text       = resultMessage,
                        fontSize   = 13.sp,
                        color      = TextDark,
                        lineHeight = 20.sp
                    )
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        // ── Section 3 : My Services card ─────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .shadow(4.dp, RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .background(CardWhite)
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text("My Services", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Text("See All →",   color = AccentBlue, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(16.dp))

            menuItems.forEachIndexed { index, item ->
                MenuListRow(
                    title     = item.title,
                    subtitle  = item.subtitle,
                    icon      = item.icon,
                    iconColor = item.iconColor,
                    iconBg    = item.iconBg,
                    badge     = item.badge
                )
                if (index < menuItems.size - 1) {
                    HorizontalDivider(
                        color     = DividerColor,
                        thickness = 1.dp,
                        modifier  = Modifier.padding(start = 60.dp)
                    )
                }
            }
        }
        Spacer(Modifier.height(24.dp))
    }
}
// REUSABLE — Summary stat card (Appointments / Screenings / Dependents)
@Composable
fun StatCard(modifier: Modifier, label: String, value: String, color: Color, bg: Color) {
    Column(
        modifier = modifier
            .shadow(6.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(CardWhite)
            .padding(vertical = 14.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier         = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(bg),
            contentAlignment = Alignment.Center
        ) {
            Text(value, color = color, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(Modifier.height(6.dp))
        Text(label, fontSize = 10.sp, color = TextGray, textAlign = TextAlign.Center, fontWeight = FontWeight.Medium)
    }
}
// REUSABLE — Single service row inside "My Services" card
@Composable
fun MenuListRow(
    title    : String,
    subtitle : String,
    icon     : ImageVector,
    iconColor: Color,
    iconBg   : Color,
    badge    : String? = null
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Icon + text
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier         = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, title, tint = iconColor, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(title,    fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                Text(subtitle, fontSize = 12.sp, color = TextGray)
            }
        }
        // Badge (optional) + chevron
        Row(
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (badge != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(AccentBlue)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(badge, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            Text("›", fontSize = 22.sp, color = TextGray, fontWeight = FontWeight.Light)
        }
    }
}
// PREVIEW
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewHomeScreen() {
    HomeScreen()
}