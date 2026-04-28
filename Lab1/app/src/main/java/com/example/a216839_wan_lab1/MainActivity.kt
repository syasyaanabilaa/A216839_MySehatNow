package com.example.a216839_wan_lab1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.GpsFixed
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.a216839_wan_lab1.R

// Colors
val PurpleHeader = Color(0xFF7B2FF7); val BlueHeader = Color(0xFF3B6FE8)
val IconBlue = Color(0xFF2B5CE6);     val IconBlueBg = Color(0xFFEEF2FF)
val CardWhite = Color(0xFFFFFFFF);    val BackgroundGray = Color(0xFFF4F6FA)
val TextDark = Color(0xFF1A1A2E);     val TextGray = Color(0xFF888888)
val AccentBlue = Color(0xFF2B5CE6);   val GoldColor = Color(0xFFFFD700)
val DividerColor = Color(0xFFEEEEEE)
val ColorCal = Color(0xFF5C6BC0); val BgCal = Color(0xFFE8EAF6)
val ColorTrack = Color(0xFFEF5350); val BgTrack = Color(0xFFFFEBEE)
val ColorLoc = Color(0xFF26A69A); val BgLoc = Color(0xFFE0F2F1)
val ColorHeart = Color(0xFFEC407A); val BgHeart = Color(0xFFFCE4EC)
val ColorFam = Color(0xFFFF7043); val BgFam = Color(0xFFFBE9E7)
val ColorBook = Color(0xFF42A5F5); val BgBook = Color(0xFFE3F2FD)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { HomeScreen() }
    }
}

@Composable
fun HomeScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    Scaffold(
        containerColor = BackgroundGray,
        bottomBar = { BottomNavigationBar(selectedTab) { selectedTab = it } }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) { HomeUI() }
    }
}

@Composable
fun BottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    data class NavItem(val label: String, val icon: ImageVector)
    val items = listOf(
        NavItem("Home", Icons.Filled.Home),
        NavItem("Health Records", Icons.Outlined.Assignment),
        NavItem("Notifications", Icons.Filled.Notifications),
        NavItem("Settings", Icons.Filled.Settings)
    )
    NavigationBar(containerColor = CardWhite, tonalElevation = 0.dp,
        modifier = Modifier.shadow(elevation = 16.dp)) {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedTab == index,
                onClick  = { onTabSelected(index) },
                icon = { Icon(item.icon, item.label, modifier = Modifier.size(24.dp)) },
                label = { Text(item.label, fontSize = 10.sp, textAlign = TextAlign.Center) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AccentBlue, selectedTextColor = AccentBlue,
                    unselectedIconColor = TextGray, unselectedTextColor = TextGray,
                    indicatorColor = IconBlueBg
                )
            )
        }
    }
}

@Composable
fun HomeUI() {
    data class MenuItem(val title: String, val subtitle: String, val icon: ImageVector,
                        val iconColor: Color, val iconBg: Color, val badge: String? = null)
    val menuItems = listOf(
        MenuItem("Appointments",      "Book your next visit",     Icons.Outlined.CalendarToday, ColorCal,   BgCal,   "2"),
        MenuItem("Disease Tracker",   "Monitor your conditions",  Icons.Outlined.GpsFixed,      ColorTrack, BgTrack, "New"),
        MenuItem("Health Facilities", "Find clinics & hospitals", Icons.Outlined.LocationOn,    ColorLoc,   BgLoc),
        MenuItem("Health Screenings", "Check your schedule",      Icons.Outlined.MonitorHeart,  ColorHeart, BgHeart, "1"),
        MenuItem("Dependents",        "Manage family members",    Icons.Outlined.PeopleOutline, ColorFam,   BgFam),
        MenuItem("MyDaR",             "Access your records",      Icons.Outlined.MenuBook,      ColorBook,  BgBook)
    )

    Column(modifier = Modifier
        .fillMaxSize()
        .background(BackgroundGray)
        .verticalScroll(rememberScrollState())) {

        // Header Banner
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .background(Brush.linearGradient(listOf(PurpleHeader, BlueHeader)))) {

            Box(modifier = Modifier.size(180.dp).offset(x = 120.dp, y = (-40).dp)
                .clip(CircleShape).background(Color.White.copy(alpha = 0.06f)).align(Alignment.TopEnd))
            Box(modifier = Modifier.size(120.dp).offset(x = 60.dp, y = 40.dp)
                .clip(CircleShape).background(Color.White.copy(alpha = 0.06f)).align(Alignment.TopEnd))

            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(46.dp).clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center) {
                        Text("S", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text("Good Morning,", color = Color.White.copy(alpha = 0.75f), fontSize = 11.sp)
                        Text("SYASYA NABILAH", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(Icons.Filled.Notifications to "Notifications",
                        Icons.Filled.Settings to "Settings").forEach { (icon, desc) ->
                        Box(modifier = Modifier.size(40.dp).clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center) {
                            Icon(icon, desc, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }

            Column(modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, end = 20.dp, bottom = 24.dp)) {
                Text("WELCOME TO", color = Color.White.copy(alpha = 0.70f),
                    fontSize = 11.sp, fontWeight = FontWeight.Medium, letterSpacing = 2.sp)
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("My", color = GoldColor, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                    Text("SehatNow", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(Modifier.height(8.dp))
                Text("Securely access & manage\nyour health records anytime.",
                    color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp, lineHeight = 18.sp)
                Spacer(Modifier.height(14.dp))
                Box(modifier = Modifier.clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.20f))
                    .padding(horizontal = 20.dp, vertical = 9.dp)) {
                    Text("Find out more  →", color = Color.White, fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Stats Strip
        Row(modifier = Modifier.fillMaxWidth().offset(y = (-20).dp).padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            StatCard(Modifier.weight(1f), "Appointments", "2", ColorCal, BgCal)
            StatCard(Modifier.weight(1f), "Screenings",   "1", ColorHeart, BgHeart)
            StatCard(Modifier.weight(1f), "Dependents",   "3", ColorFam, BgFam)
        }

        // Services Card
        Column(modifier = Modifier.fillMaxWidth().offset(y = (-8).dp).padding(horizontal = 16.dp)
            .shadow(4.dp, RoundedCornerShape(24.dp)).clip(RoundedCornerShape(24.dp))
            .background(CardWhite).padding(horizontal = 20.dp, vertical = 20.dp)) {

            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text("My Services", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextDark)
                Text("See All →", color = AccentBlue, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(Modifier.height(16.dp))

            menuItems.forEachIndexed { index, item ->
                MenuListRow(item.title, item.subtitle, item.icon, item.iconColor, item.iconBg, item.badge)
                if (index < menuItems.size - 1)
                    Divider(color = DividerColor, thickness = 1.dp,
                        modifier = Modifier.padding(start = 60.dp))
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun StatCard(modifier: Modifier, label: String, value: String, color: Color, bg: Color) {
    Column(modifier = modifier.shadow(4.dp, RoundedCornerShape(16.dp))
        .clip(RoundedCornerShape(16.dp)).background(CardWhite)
        .padding(vertical = 14.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(bg),
            contentAlignment = Alignment.Center) {
            Text(value, color = color, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(Modifier.height(6.dp))
        Text(label, fontSize = 10.sp, color = TextGray, textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium)
    }
}

@Composable
fun MenuListRow(title: String, subtitle: String, icon: ImageVector,
                iconColor: Color, iconBg: Color, badge: String? = null) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(iconBg),
                contentAlignment = Alignment.Center) {
                Icon(icon, title, tint = iconColor, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(14.dp))
            Column {
                Text(title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                Text(subtitle, fontSize = 12.sp, color = TextGray)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (badge != null) {
                Box(modifier = Modifier.clip(RoundedCornerShape(20.dp)).background(AccentBlue)
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                    contentAlignment = Alignment.Center) {
                    Text(badge, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            Text("›", fontSize = 22.sp, color = TextGray, fontWeight = FontWeight.Light)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewHomeScreen() { HomeScreen() }