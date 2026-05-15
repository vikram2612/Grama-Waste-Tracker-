package com.grama.wastetracker.data.model

/**
 * Represents a registered user in the system.
 */
data class User(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val villageName: String = "",
    val role: String = ROLE_CITIZEN, // "citizen" or "admin"
    val fcmToken: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        const val ROLE_CITIZEN = "citizen"
        const val ROLE_ADMIN = "admin"
        const val ROLE_TRACTOR = "tractor"
    }
}

/**
 * Represents the real-time location and status of the waste collection tractor.
 */
data class TractorLocation(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val isActive: Boolean = false,
    val speed: Double = 0.0,
    val heading: Float = 0f,
    val driverName: String = "",
    val lastUpdated: Long = System.currentTimeMillis(),
    val routeName: String = ""
)

/**
 * Represents a blackspot report — an illegal garbage dumping location.
 */
data class BlackspotReport(
    val id: String = "",
    val userId: String = "",
    val userName: String = "",
    val description: String = "",
    val imageUrl: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val address: String = "",
    val status: String = STATUS_PENDING, // "pending" or "resolved"
    val createdAt: Long = System.currentTimeMillis(),
    val resolvedAt: Long = 0,
    val resolvedBy: String = "",
    val adminNotes: String = ""
) {
    companion object {
        const val STATUS_PENDING = "pending"
        const val STATUS_RESOLVED = "resolved"
    }
}

/**
 * Represents a notification entry stored in the database.
 */
data class AppNotification(
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "", // "tractor_alert", "report_update", "general"
    val targetUserId: String = "",
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Represents a waste segregation guide item.
 */
data class WasteGuideItem(
    val id: String = "",
    val category: String = "", // "dry", "wet", "hazardous"
    val title: String = "",
    val titleKn: String = "",
    val description: String = "",
    val descriptionKn: String = "",
    val items: List<String> = emptyList(),
    val itemsKn: List<String> = emptyList(),
    val disposalTip: String = "",
    val disposalTipKn: String = "",
    val iconName: String = ""
)

/**
 * Chat message for the AI assistant.
 */
data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val content: String = "",
    val isFromUser: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Result of AI waste classification.
 */
data class WasteClassification(
    val category: String = "", // "dry", "wet", "hazardous"
    val confidence: Float = 0f,
    val explanation: String = "",
    val disposalAdvice: String = ""
)
