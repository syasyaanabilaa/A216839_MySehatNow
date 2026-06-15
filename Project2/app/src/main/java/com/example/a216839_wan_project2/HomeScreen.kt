package com.example.a216839_wan_project2

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.a216839_wan_project2.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ── DATA MODELS ───────────────────────────────────────────────────────────────
data class NavItem(val label: String, val icon: ImageVector)

data class ServiceItem(
    val title       : String,
    val subtitle    : String,
    val icon        : ImageVector,
    val iconColor   : Color,
    val iconBg      : Color,
    val badge       : String? = null,
    val expandDetail: String
)

data class HealthTip(
    val title      : String,
    val description: String,
    val iconColor  : Color,
    val bgColor    : Color,
    val icon       : ImageVector
)

data class StatCardData(val label: String, val value: String, val color: Color, val bg: Color)

sealed class SymptomResult {
    object Empty                             : SymptomResult()
    data class Found(val message: String)    : SymptomResult()
    data class NotFound(val message: String) : SymptomResult()
}

// ── SYMPTOM CHECKER LOGIC ─────────────────────────────────────────────────────
object SymptomChecker {
    private data class SymptomEntry(
        val keywords: List<String>,
        val emoji   : String,
        val name    : String,
        val advice  : String
    )

    private val symptoms = listOf(
        SymptomEntry(listOf("fever","panas"),                              "🌡️","Fever",
            "Stay hydrated and rest. Take paracetamol if needed. Visit a clinic if it persists over 3 days or exceeds 39°C."),
        SymptomEntry(listOf("headache","sakit kepala"),                    "🤕","Headache",
            "Rest in a dark, quiet room. Stay hydrated. See a doctor if severe, recurring, or accompanied by vision changes."),
        SymptomEntry(listOf("cough","batuk"),                              "😷","Cough",
            "Drink warm fluids and honey. Avoid smoke and irritants. See a doctor if it lasts more than 2 weeks or produces blood."),
        SymptomEntry(listOf("dizzy","dizziness","pening"),                 "💫","Dizziness",
            "Sit or lie down immediately. Avoid sudden movements. Seek medical help if it is frequent or accompanied by vomiting."),
        SymptomEntry(listOf("chest pain","chest discomfort","sakit dada"), "❤️","Chest Discomfort",
            "⚠️ This may be serious. Please go to an Emergency Department or call 999 immediately if the pain is severe."),
        SymptomEntry(listOf("nausea","vomit","mual","muntah"),             "🤢","Nausea",
            "Avoid heavy meals. Sip water slowly. Eat plain foods like crackers. See a doctor if it persists or you cannot keep fluids down."),
        SymptomEntry(listOf("fatigue","tired","letargi","penat"),          "😴","Fatigue",
            "Ensure 7–9 hours of sleep. Stay hydrated and eat balanced meals. See a doctor if fatigue is sudden or severe."),
        SymptomEntry(listOf("sore throat","sakit tekak"),                  "🦠","Sore Throat",
            "Gargle warm salt water. Drink warm fluids. See a doctor if you have white patches or difficulty swallowing."),
        SymptomEntry(listOf("rash","ruam","gatal"),                        "🔴","Skin Rash",
            "Avoid scratching. Apply cool compress. See a doctor if the rash spreads rapidly or is accompanied by breathing difficulty."),
        SymptomEntry(listOf("shortness of breath","sesak nafas","breathing"),"🫁","Shortness of Breath",
            "⚠️ Sit upright and try to stay calm. If sudden and severe, call 999 or go to an Emergency Department immediately.")
    )

    fun check(input: String): SymptomResult {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return SymptomResult.Empty
        val lower = trimmed.lowercase()
        val match = symptoms.firstOrNull { e -> e.keywords.any { lower.contains(it) } }
        return if (match != null)
            SymptomResult.Found("${match.emoji} Symptom detected: ${match.name}\n\n📋 Advice: ${match.advice}")
        else
            SymptomResult.NotFound("🔍 No specific symptom matched for \"$trimmed\".\n\n📋 Please consult a healthcare professional for a proper diagnosis.")
    }
}

// ── STATIC DATA ───────────────────────────────────────────────────────────────
val navItems = listOf(
    NavItem("Home",           Icons.Filled.Home),
    NavItem("Health Records", Icons.Outlined.Assignment),
    NavItem("BMI",            Icons.Filled.Calculate),
    NavItem("Profile",        Icons.Outlined.AccountCircle)
)

val serviceItems = listOf(
    ServiceItem("Appointments",       "Book your next visit",       Icons.Outlined.CalendarToday,   ColorCal,   BgCal,       "2",   "Your next appointment is on 20 Jul 2025 at KK Cheras. Tap to reschedule or view details."),
    ServiceItem("Disease Tracker",    "Monitor your conditions",    Icons.Outlined.GpsFixed,         ColorTrack, BgTrack,     "New", "Tracking: Hypertension, Type 2 Diabetes. Last updated: today. Tap to view trends."),
    ServiceItem("Health Facilities",  "Find clinics & hospitals",   Icons.Outlined.LocationOn,       ColorLoc,   BgLoc,       null,  "Nearest clinic: Klinik Kesihatan Kajang (2.1 km). Nearest hospital: Hospital Kajang (4.8 km)."),
    ServiceItem("Health Screenings",  "Check your schedule",        Icons.Outlined.MonitorHeart,     ColorHeart, BgHeart,     "1",   "Upcoming: Blood Pressure Check on 25 Jul 2025 at KK Cheras. Tap to confirm attendance."),
    ServiceItem("Dependents",         "Manage family members",      Icons.Outlined.PeopleOutline,    ColorFam,   BgFam,       null,  "3 dependents registered: Ahmad (Son, 12), Mak (Mother, 58), Ayah (Father, 61). Tap to manage."),
    ServiceItem("MyDaR",              "Access your records",        Icons.Outlined.MenuBook,         ColorBook,  BgBook,      null,  "Last record updated: 10 Jul 2025. 12 documents on file including lab results and prescriptions."),
    ServiceItem("Organ Donor Pledge", "Register your pledge",       Icons.Outlined.Handshake,        ColorBook,  BgHandshake, null,  "Status: Not yet registered. Join over 600,000 Malaysians who have pledged. Tap to register today.")
)

val healthTips = listOf(
    HealthTip("Stay Hydrated",    "Drink 8 glasses of water daily to maintain energy and focus.",             ColorCal,   BgCal,   Icons.Outlined.WaterDrop),
    HealthTip("Move Daily",       "Even 30 minutes of walking improves cardiovascular health.",                ColorTrack, BgTrack, Icons.Outlined.DirectionsRun),
    HealthTip("Sleep Well",       "7–9 hours of quality sleep boosts immunity and mental health.",             ColorFam,   BgFam,   Icons.Outlined.Bedtime),
    HealthTip("Eat Balanced",     "Include fruits, vegetables, and whole grains in every meal.",               ColorHeart, BgHeart, Icons.Outlined.Restaurant),
    HealthTip("Manage Stress",    "Practice deep breathing or mindfulness for 5 minutes a day.",               ColorBook,  BgBook,  Icons.Outlined.SelfImprovement),
    HealthTip("Regular Checkups", "Annual health screenings can catch issues before they become serious.",     ColorLoc,   BgLoc,   Icons.Outlined.MedicalServices)
)

val statCards = listOf(
    StatCardData("Appointments", "2", ColorCal,   BgCal),
    StatCardData("Screenings",   "1", ColorHeart, BgHeart),
    StatCardData("Dependents",   "3", ColorFam,   BgFam)
)

// ── HOME SCREEN ───────────────────────────────────────────────────────────────
@Composable
fun HomeScreen(
    onNavigateToProfile      : () -> Unit,
    onNavigateToAppointments : () -> Unit = {},
    onNavigateToBmi          : () -> Unit = {},
    onNavigateToHealthCentre : () -> Unit = {},
    onNavigateToCommunity    : () -> Unit = {}
) {
    var selectedTab   by remember { mutableStateOf(0) }
    val scrollState    = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val showFab       by remember { derivedStateOf { scrollState.value > 300 } }
    var symptomInput  by remember { mutableStateOf("") }
    var symptomResult by remember { mutableStateOf<SymptomResult>(SymptomResult.Empty) }

    Scaffold(
        containerColor = BackgroundGray,
        bottomBar = {
            BottomNavigationBar(
                selectedTab   = selectedTab,
                onTabSelected = { idx ->
                    selectedTab = idx
                    if (idx == 1) onNavigateToProfile()
                    if (idx == 2) onNavigateToBmi()
                    if (idx == 3) onNavigateToProfile()
                }
            )
        },
        floatingActionButton = {
            ScrollToTopFab(
                visible = showFab,
                onClick = { coroutineScope.launch { scrollState.animateScrollTo(0) } }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            HomeContent(
                scrollState              = scrollState,
                symptomInput             = symptomInput,
                symptomResult            = symptomResult,
                onSymptomChange          = { symptomInput = it; symptomResult = SymptomResult.Empty },
                onCheckClick             = { symptomResult = SymptomChecker.check(symptomInput) },
                onNavigateToAppointments = onNavigateToAppointments,
                onNavigateToBmi          = onNavigateToBmi,
                onNavigateToHealthCentre = onNavigateToHealthCentre,
                onNavigateToCommunity    = onNavigateToCommunity
            )
        }
    }
}

// ── FAB ───────────────────────────────────────────────────────────────────────
@Composable
fun ScrollToTopFab(visible: Boolean, onClick: () -> Unit) {
    val scale by animateFloatAsState(
        targetValue   = if (visible) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label         = "fab_scale"
    )
    if (scale > 0f) {
        FloatingActionButton(
            onClick        = onClick,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor   = MaterialTheme.colorScheme.onPrimary,
            shape          = CircleShape,
            modifier       = Modifier
                .size(48.dp)
                .graphicsLayer { scaleX = scale; scaleY = scale }
        ) {
            Icon(Icons.Filled.KeyboardArrowUp, "Scroll to top", modifier = Modifier.size(24.dp))
        }
    }
}

// ── BOTTOM NAV ────────────────────────────────────────────────────────────────
@Composable
fun BottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 0.dp,
        modifier       = Modifier.shadow(elevation = 12.dp)
    ) {
        navItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedTab == index,
                onClick  = { onTabSelected(index) },
                icon     = { Icon(item.icon, item.label, modifier = Modifier.size(22.dp)) },
                label    = { Text(item.label, fontSize = 10.sp, textAlign = TextAlign.Center) },
                colors   = NavigationBarItemDefaults.colors(
                    selectedIconColor   = MaterialTheme.colorScheme.primary,
                    selectedTextColor   = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor      = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}

// ── PAGER DOTS ────────────────────────────────────────────────────────────────
@Composable
fun PagerDotIndicators(currentPage: Int, pageCount: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isActive = currentPage == index
            Box(
                modifier = Modifier
                    .size(width = if (isActive) 18.dp else 6.dp, height = 6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(if (isActive) Color.White else Color.White.copy(alpha = 0.35f))
                    .animateContentSize(animationSpec = tween(300))
            )
        }
    }
}

// ── HOME CONTENT ──────────────────────────────────────────────────────────────
@Composable
fun HomeContent(
    scrollState             : ScrollState,
    symptomInput            : String,
    symptomResult           : SymptomResult,
    onSymptomChange         : (String) -> Unit,
    onCheckClick            : () -> Unit,
    onNavigateToAppointments: () -> Unit = {},
    onNavigateToBmi         : () -> Unit = {},
    onNavigateToHealthCentre: () -> Unit = {},
    onNavigateToCommunity   : () -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    LaunchedEffect(Unit) {
        while (true) {
            delay(3_500)
            pagerState.animateScrollToPage((pagerState.currentPage + 1) % 3)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
            .verticalScroll(scrollState)
    ) {
        // ── BANNER ────────────────────────────────────────────────────────────
        BannerSection(pagerState = pagerState, onNavigateToBmi = onNavigateToBmi)

        Spacer(Modifier.height(24.dp))

        // ── QUICK ACCESS CARDS ────────────────────────────────────────────────
        SectionLabel(title = "Quick Access")
        Spacer(Modifier.height(12.dp))
        QuickAccessRow(
            onHealthCentre = onNavigateToHealthCentre,
            onCommunity    = onNavigateToCommunity
        )

        Spacer(Modifier.height(24.dp))

        // ── SYMPTOM CHECKER ───────────────────────────────────────────────────
        SectionLabel(title = "Quick Tools")
        Spacer(Modifier.height(12.dp))
        ProSectionCard {
            SymptomCheckerContent(
                input         = symptomInput,
                result        = symptomResult,
                onInputChange = onSymptomChange,
                onCheckClick  = onCheckClick
            )
        }

        Spacer(Modifier.height(24.dp))

        // ── MY SERVICES ───────────────────────────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            SectionLabel(title = "My Services", inline = true)
            Text(
                text       = "See all →",
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color      = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.height(12.dp))
        ProSectionCard { ServicesContent(onAppointmentsClick = onNavigateToAppointments) }

        Spacer(Modifier.height(24.dp))

        // ── HEALTH TIPS ───────────────────────────────────────────────────────
        Row(
            modifier              = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            SectionLabel(title = "Health Tips", inline = true)
            Text(
                text       = "See all →",
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color      = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.height(12.dp))
        HealthTipsRow()

        Spacer(Modifier.height(32.dp))
    }
}

// ── SECTION LABEL ─────────────────────────────────────────────────────────────
@Composable
fun SectionLabel(title: String, inline: Boolean = false) {
    val modifier = if (inline) Modifier else Modifier.padding(horizontal = 20.dp)
    Text(
        text       = title,
        modifier   = modifier,
        fontSize   = 16.sp,
        fontWeight = FontWeight.Bold,
        color      = MaterialTheme.colorScheme.onBackground,
        letterSpacing = (-0.2).sp
    )
}

// ── SECTION CARD ─────────────────────────────────────────────────────────────
@Composable
fun ProSectionCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape     = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
            content  = content
        )
    }
}

// ── QUICK ACCESS ROW ─────────────────────────────────────────────────────────
@Composable
fun QuickAccessRow(
    onHealthCentre: () -> Unit,
    onCommunity   : () -> Unit
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickAccessCard(
            modifier    = Modifier.weight(1f),
            icon        = Icons.Outlined.LocalHospital,
            iconBg      = Color(0xFF3B1FA3),
            label       = "Health Centres",
            sublabel    = "Find clinics near you",
            cardBg      = MaterialTheme.colorScheme.primaryContainer,
            labelColor  = MaterialTheme.colorScheme.onPrimaryContainer,
            onClick     = onHealthCentre
        )
        QuickAccessCard(
            modifier    = Modifier.weight(1f),
            icon        = Icons.Outlined.People,
            iconBg      = MaterialTheme.colorScheme.secondary,
            label       = "AI Health Tips",
            sublabel    = "Get personalised advice",
            cardBg      = MaterialTheme.colorScheme.secondaryContainer,
            labelColor  = MaterialTheme.colorScheme.onSecondaryContainer,
            onClick     = onCommunity
        )
    }
}

@Composable
fun QuickAccessCard(
    modifier   : Modifier,
    icon       : ImageVector,
    iconBg     : Color,
    label      : String,
    sublabel   : String,
    cardBg     : Color,
    labelColor : Color,
    onClick    : () -> Unit
) {
    Card(
        modifier  = modifier.clickable { onClick() },
        shape     = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors    = CardDefaults.cardColors(containerColor = cardBg)
    ) {
        Column(
            modifier            = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier         = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = icon,
                    contentDescription = null,
                    tint               = Color.White,
                    modifier           = Modifier.size(22.dp)
                )
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text       = label,
                fontWeight = FontWeight.Bold,
                fontSize   = 13.sp,
                color      = labelColor,
                lineHeight = 18.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text     = sublabel,
                fontSize = 11.sp,
                color    = labelColor.copy(alpha = 0.6f)
            )
        }
    }
}

// ── BANNER ────────────────────────────────────────────────────────────────────
@Composable
fun BannerSection(
    pagerState     : androidx.compose.foundation.pager.PagerState,
    onNavigateToBmi: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp)
    ) {
        // Pager fills the top 295dp
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(295.dp)
                .align(Alignment.TopCenter)
        ) {
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                when (page) {
                    0    -> WelcomeBannerPage(currentPage = pagerState.currentPage, pageCount = 3)
                    1    -> ImageBannerPage(resId = R.drawable.merah,  desc = "Health Campaign",   currentPage = pagerState.currentPage, pageCount = 3)
                    2    -> ImageBannerPage(resId = R.drawable.hijauu, desc = "Wellness Campaign", currentPage = pagerState.currentPage, pageCount = 3)
                    else -> WelcomeBannerPage(currentPage = pagerState.currentPage, pageCount = 3)
                }
            }
            TopBarRow(
                modifier        = Modifier.align(Alignment.TopStart),
                onNavigateToBmi = onNavigateToBmi
            )
        }

        // Floating stat cards at the bottom, overlapping banner
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp)
                .zIndex(1f),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            statCards.forEach { card ->
                StatCard(modifier = Modifier.weight(1f), data = card)
            }
        }
    }
}

@Composable
fun WelcomeBannerPage(currentPage: Int, pageCount: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(listOf(PurpleHeader, BlueHeader))
            )
    ) {
        // Decorative circles
        Box(
            modifier = Modifier
                .size(220.dp)
                .offset(x = 120.dp, y = (-60).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
                .align(Alignment.TopEnd)
        )
        Box(
            modifier = Modifier
                .size(140.dp)
                .offset(x = 60.dp, y = 20.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.05f))
                .align(Alignment.TopEnd)
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 22.dp, bottom = 54.dp)
        ) {
            Text(
                text          = "WELCOME TO",
                color         = Color.White.copy(alpha = 0.65f),
                fontSize      = 10.sp,
                fontWeight    = FontWeight.SemiBold,
                letterSpacing = 2.sp
            )
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text       = "My",
                    color      = GoldColor,
                    fontSize   = 30.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text       = "SehatNow",
                    color      = Color.White,
                    fontSize   = 30.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text       = "Securely access & manage\nyour health records anytime.",
                color      = Color.White.copy(alpha = 0.80f),
                fontSize   = 12.sp,
                lineHeight = 19.sp
            )
            Spacer(Modifier.height(16.dp))
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White.copy(alpha = 0.18f)
            ) {
                Text(
                    text     = "Find out more →",
                    color    = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 9.dp)
                )
            }
            Spacer(Modifier.height(14.dp))
            PagerDotIndicators(currentPage = currentPage, pageCount = pageCount)
        }
    }
}

@Composable
fun ImageBannerPage(resId: Int, desc: String, currentPage: Int, pageCount: Int) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painterResource(resId),
            desc,
            contentScale = ContentScale.Crop,
            modifier     = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 22.dp, bottom = 54.dp)
        ) {
            PagerDotIndicators(currentPage = currentPage, pageCount = pageCount)
        }
    }
}

@Composable
fun TopBarRow(modifier: Modifier = Modifier, onNavigateToBmi: () -> Unit = {}) {
    Row(
        modifier              = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment   = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar circle
            Box(
                modifier         = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = "S",
                    color      = Color.White,
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Column {
                Text(
                    text     = "Good Morning,",
                    color    = Color.White.copy(alpha = 0.70f),
                    fontSize = 11.sp
                )
                Text(
                    text       = "SYASYA NABILAH",
                    color      = Color.White,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.2).sp
                )
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TopBarIconButton(
                icon        = Icons.Filled.Calculate,
                description = "BMI",
                onClick     = onNavigateToBmi
            )
            TopBarIconButton(
                icon        = Icons.Filled.Settings,
                description = "Settings",
                onClick     = {}
            )
        }
    }
}

@Composable
private fun TopBarIconButton(
    icon       : ImageVector,
    description: String,
    onClick    : () -> Unit
) {
    Box(
        modifier         = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.16f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = description,
            tint               = Color.White,
            modifier           = Modifier.size(18.dp)
        )
    }
}

// ── STAT CARD ─────────────────────────────────────────────────────────────────
@Composable
fun StatCard(modifier: Modifier, data: StatCardData) {
    Card(
        modifier  = modifier,
        shape     = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier            = Modifier.padding(vertical = 14.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier         = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(data.bg),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text       = data.value,
                    color      = data.color,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Spacer(Modifier.height(7.dp))
            Text(
                text      = data.label,
                style     = MaterialTheme.typography.labelSmall,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                fontSize  = 10.sp
            )
        }
    }
}

// ── SYMPTOM CHECKER ───────────────────────────────────────────────────────────
@Composable
fun SymptomCheckerContent(
    input        : String,
    result       : SymptomResult,
    onInputChange: (String) -> Unit,
    onCheckClick : () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier         = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(BgHeart),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Outlined.MonitorHeart,
                contentDescription = null,
                tint               = ColorHeart,
                modifier           = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                text       = "Symptom Checker",
                fontSize   = 14.sp,
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text     = "Enter a symptom for quick advice",
                fontSize = 12.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

    Spacer(Modifier.height(14.dp))

    OutlinedTextField(
        value         = input,
        onValueChange = onInputChange,
        placeholder   = {
            Text(
                "e.g. fever, headache, cough…",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        },
        singleLine = true,
        shape      = RoundedCornerShape(14.dp),
        modifier   = Modifier.fillMaxWidth(),
        colors     = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    )

    Spacer(Modifier.height(10.dp))

    Button(
        onClick = onCheckClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp),
        shape  = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor   = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Text("Get Advice", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }

    when (result) {
        is SymptomResult.Empty    -> Unit
        is SymptomResult.Found    -> {
            Spacer(Modifier.height(12.dp))
            ResultBox(result.message, MaterialTheme.colorScheme.primaryContainer, MaterialTheme.colorScheme.onPrimaryContainer)
        }
        is SymptomResult.NotFound -> {
            Spacer(Modifier.height(12.dp))
            ResultBox(result.message, MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.colorScheme.onSecondaryContainer)
        }
    }
}

@Composable
fun ResultBox(text: String, containerColor: Color, textColor: Color) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)),
        shape = RoundedCornerShape(14.dp),
        color = containerColor
    ) {
        Text(
            text       = text,
            style      = MaterialTheme.typography.bodyMedium,
            color      = textColor,
            modifier   = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            lineHeight = 22.sp
        )
    }
}

// ── HEALTH TIPS ───────────────────────────────────────────────────────────────
@Composable
fun HealthTipsRow() {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        healthTips.forEach { tip -> HealthTipCard(tip = tip) }
    }
}

@Composable
fun HealthTipCard(tip: HealthTip) {
    Card(
        modifier  = Modifier.width(155.dp),
        shape     = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier         = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(tip.bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = tip.icon,
                    contentDescription = tip.title,
                    tint               = tip.iconColor,
                    modifier           = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text       = tip.title,
                fontSize   = 13.sp,
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text       = tip.description,
                fontSize   = 11.sp,
                color      = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 16.sp
            )
        }
    }
}

// ── SERVICES ──────────────────────────────────────────────────────────────────
@Composable
fun ServicesContent(onAppointmentsClick: () -> Unit = {}) {
    serviceItems.forEachIndexed { index, item ->
        ExpandableServiceRow(
            item       = item,
            onNavigate = if (item.title == "Appointments") onAppointmentsClick else null
        )
        if (index < serviceItems.size - 1) {
            HorizontalDivider(
                color    = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                thickness = 0.5.dp,
                modifier  = Modifier.padding(start = 58.dp)
            )
        }
    }
}

@Composable
fun ExpandableServiceRow(item: ServiceItem, onNavigate: (() -> Unit)? = null) {
    var expanded by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness    = Spring.StiffnessLow
                )
            )
            .clickable {
                if (onNavigate != null) onNavigate() else expanded = !expanded
            }
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon box
            Box(
                modifier         = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(item.iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = item.icon,
                    contentDescription = item.title,
                    tint               = item.iconColor,
                    modifier           = Modifier.size(22.dp)
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = item.title,
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text     = item.subtitle,
                    fontSize = 12.sp,
                    color    = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(
                verticalAlignment   = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (item.badge != null) {
                    Surface(
                        shape = RoundedCornerShape(99.dp),
                        color = MaterialTheme.colorScheme.primary
                    ) {
                        Text(
                            text       = item.badge,
                            color      = MaterialTheme.colorScheme.onPrimary,
                            fontSize   = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier   = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                Icon(
                    imageVector        = if (onNavigate != null)
                        Icons.Filled.KeyboardArrowRight
                    else if (expanded)
                        Icons.Filled.KeyboardArrowUp
                    else
                        Icons.Filled.KeyboardArrowDown,
                    contentDescription = "action",
                    tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier           = Modifier.size(20.dp)
                )
            }
        }

        if (expanded && onNavigate == null) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 58.dp, end = 0.dp, bottom = 12.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text       = item.expandDetail,
                    fontSize   = 12.sp,
                    color      = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier   = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                    lineHeight = 19.sp
                )
            }
        }
    }
}