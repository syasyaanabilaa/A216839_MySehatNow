package com.example.a216839_wan_project1

// ── ROUTES ────────────────────────────────────────────────────────────────────
// Sealed class acts as a type-safe wrapper for navigation route strings.
// This prevents typos and makes refactoring easy.
sealed class Screen(val route: String) {
    object Home           : Screen("home")
    object ProfileLanding : Screen("profile_landing")
    object ProfileForm    : Screen("profile_form")
    object ProfilePreview : Screen("profile_preview")
    object Appointments   : Screen("appointments")
    object BmiCalculator   : Screen("BmiCalculator")
}

