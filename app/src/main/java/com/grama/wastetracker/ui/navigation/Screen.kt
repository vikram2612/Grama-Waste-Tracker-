package com.grama.wastetracker.ui.navigation

/**
 * Navigation route constants for the app.
 */
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object ReportBlackspot : Screen("report_blackspot")
    object BlackspotList : Screen("blackspot_list")
    object WasteGuide : Screen("waste_guide")
    object AiAssistant : Screen("ai_assistant")
    object Notifications : Screen("notifications")
    object Profile : Screen("profile")
    object Settings : Screen("settings")

    // Admin screens
    object AdminDashboard : Screen("admin_dashboard")
    object AdminReports : Screen("admin_reports")
    object AdminTractor : Screen("admin_tractor")
    object AdminReportDetail : Screen("admin_report_detail/{reportId}") {
        fun createRoute(reportId: String) = "admin_report_detail/$reportId"
    }
}
