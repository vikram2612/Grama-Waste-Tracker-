package com.grama.wastetracker.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.grama.wastetracker.data.model.AppNotification
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface NotificationRepository {
    fun observeNotifications(userId: String): Flow<List<AppNotification>>
    suspend fun sendNotification(notification: AppNotification): Result<Unit>
    suspend fun markAsRead(notificationId: String, userId: String): Result<Unit>
}

class NotificationRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase
) : NotificationRepository {

    private val notificationsRef = database.getReference("notifications")

    override fun observeNotifications(userId: String): Flow<List<AppNotification>> = callbackFlow {
        val listener = notificationsRef.child(userId).orderByChild("createdAt")
            .addValueEventListener(object : com.google.firebase.database.ValueEventListener {
                override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                    val list = snapshot.children.mapNotNull {
                        it.getValue(AppNotification::class.java)
                    }.sortedByDescending { it.createdAt }
                    trySend(list)
                }
                override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                    close(error.toException())
                }
            })
        awaitClose { notificationsRef.child(userId).removeEventListener(listener) }
    }

    override suspend fun sendNotification(notification: AppNotification): Result<Unit> = try {
        val id = notificationsRef.child(notification.targetUserId).push().key!!
        notificationsRef.child(notification.targetUserId).child(id)
            .setValue(notification.copy(id = id)).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun markAsRead(notificationId: String, userId: String): Result<Unit> = try {
        notificationsRef.child(userId).child(notificationId).child("isRead").setValue(true).await()
        Result.success(Unit)
    } catch (e: Exception) { Result.failure(e) }
}
