package com.example.a216839_wan_lab4

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.a216839_wan_lab4.ui.theme.MySehatNowTheme

// ── ENTRY POINT ───────────────────────────────────────────────────────────────
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MySehatNowTheme {
                val navController    = rememberNavController()
                val profileViewModel : HealthProfileViewModel = viewModel()

                NavHost(
                    navController    = navController,
                    startDestination = Screen.Home.route
                ) {
                    composable(route = Screen.Home.route) {
                        HomeScreen(
                            onNavigateToProfile      = { navController.navigate(Screen.Appointments.route) },
                            onNavigateToAppointments = { navController.navigate(Screen.Appointments.route) }
                        )
                    }
                    composable(route = Screen.ProfileLanding.route) {
                        ProfileLandingScreen(
                            onStartSetup = { navController.navigate(Screen.ProfileForm.route) },
                            onBack       = { navController.popBackStack() }
                        )
                    }
                    composable(route = Screen.ProfileForm.route) {
                        ProfileFormScreen(
                            viewModel = profileViewModel,
                            onBack    = { navController.popBackStack() },
                            onSubmit  = {
                                navController.navigate(Screen.ProfilePreview.route) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                    composable(route = Screen.ProfilePreview.route) {
                        ProfilePreviewScreen(
                            viewModel = profileViewModel,
                            onEdit    = { navController.popBackStack() },
                            onHome    = {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Home.route) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(route = Screen.Appointments.route) {
                        AppointmentsScreen(onBack = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}

// ── PREVIEWS ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewHomeScreen() {
    MySehatNowTheme {
        HomeScreen(
            onNavigateToProfile = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewProfileLanding() {
    MySehatNowTheme {
        ProfileLandingScreen(
            onStartSetup = {},
            onBack       = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewProfileForm() {
    MySehatNowTheme {
        ProfileFormScreen(
            viewModel = viewModel(),
            onBack    = {},
            onSubmit  = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewProfilePreview() {
    MySehatNowTheme {
        ProfilePreviewScreen(
            viewModel = viewModel(),
            onEdit    = {},
            onHome    = {}
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PreviewAppointments() {
    MySehatNowTheme {
        AppointmentsScreen(onBack = {})
    }
}