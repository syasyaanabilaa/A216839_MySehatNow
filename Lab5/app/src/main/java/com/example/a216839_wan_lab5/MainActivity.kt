package com.example.a216839_wan_lab5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.a216839_wan_lab5.data.database.AppDatabase
import com.example.a216839_wan_lab5.data.repository.AppointmentRepository
import com.example.a216839_wan_lab5.data.repository.BmiRepository
import com.example.a216839_wan_lab5.data.repository.HealthProfileRepository
import com.example.a216839_wan_lab5.viewmodel.*
import com.example.a216839_wan_lab5.ui.theme.MySehatNowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MySehatNowTheme {

                // ── Build Room DB (singleton) ──────────────────────────────────
                val db = AppDatabase.getInstance(applicationContext)

                // ── Repositories ──────────────────────────────────────────────
                val profileRepo     = HealthProfileRepository(db.healthProfileDao())
                val appointmentRepo = AppointmentRepository(db.appointmentDao())
                val bmiRepo         = BmiRepository(db.bmiRecordDao())

                // ── ViewModels via factories ───────────────────────────────────
                val profileVM     : HealthProfileViewModel = viewModel(factory = HealthProfileViewModelFactory(profileRepo))
                val appointmentVM : AppointmentViewModel   = viewModel(factory = AppointmentViewModelFactory(appointmentRepo))
                val bmiVM         : BmiViewModel           = viewModel(factory = BmiViewModelFactory(bmiRepo))

                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = Screen.Home.route) {

                    composable(Screen.Home.route) {
                        HomeScreen(
                            onNavigateToProfile      = { navController.navigate(Screen.ProfileLanding.route) },
                            onNavigateToAppointments = { navController.navigate(Screen.Appointments.route) },
                            onNavigateToBmi          = { navController.navigate(Screen.BmiCalculator.route) }
                        )
                    }

                    composable(Screen.ProfileLanding.route) {
                        ProfileLandingScreen(
                            onStartSetup = { navController.navigate(Screen.ProfileForm.route) },
                            onBack       = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.ProfileForm.route) {
                        ProfileFormScreen(
                            viewModel = profileVM,
                            onBack    = { navController.popBackStack() },
                            onSubmit  = {
                                navController.navigate(Screen.ProfilePreview.route) {
                                    launchSingleTop = true
                                }
                            }
                        )
                    }

                    composable(Screen.ProfilePreview.route) {
                        ProfilePreviewScreen(
                            viewModel = profileVM,
                            onEdit    = { navController.popBackStack() },
                            onHome    = {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo(Screen.Home.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(Screen.Appointments.route) {
                        AppointmentsScreen(
                            viewModel = appointmentVM,
                            onBack    = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.BmiCalculator.route) {
                        BmiCalculatorScreen(
                            viewModel = bmiVM,
                            onBack    = { navController.popBackStack() }
                        )
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
    MySehatNowTheme { HomeScreen(onNavigateToProfile = {}) }
}