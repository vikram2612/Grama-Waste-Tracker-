package com.grama.wastetracker.data.repository

import android.net.Uri
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.grama.wastetracker.data.model.BlackspotReport
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface BlackspotRepository {
    fun observeAllReports(): Flow<List<BlackspotReport>>
    fun observeUserReports(userId: String): Flow<List<BlackspotReport>>
    suspend fun submitReport(report: BlackspotReport, imageUri: Uri?): Result<String>
    suspend fun markAsResolved(reportId: String, adminId: String, notes: String): Result<Unit>
    suspend fun getReport(reportId: String): Result<BlackspotReport>
    suspend fun deleteReport(reportId: String): Result<Unit>
}

class BlackspotRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase,
    private val storage: FirebaseStorage
) : BlackspotRepository {

    private val reportsRef = database.getReference("blackspot_reports")
    private val storageRef = storage.getReference("blackspot_images")

    override fun observeAllReports(): Flow<List<BlackspotReport>> = callbackFlow {
        val listener = reportsRef.orderByChild("createdAt")
            .addValueEventListener(object : com.google.firebase.database.ValueEventListener {
                override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                    val reports = snapshot.children.mapNotNull {
                        it.getValue(BlackspotReport::class.java)
                    }.sortedByDescending { it.createdAt }
                    trySend(reports)
                }

                override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                    close(error.toException())
                }
            })
        awaitClose { reportsRef.removeEventListener(listener) }
    }

    override fun observeUserReports(userId: String): Flow<List<BlackspotReport>> = callbackFlow {
        val listener = reportsRef.orderByChild("userId").equalTo(userId)
            .addValueEventListener(object : com.google.firebase.database.ValueEventListener {
                override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                    val reports = snapshot.children.mapNotNull {
                        it.getValue(BlackspotReport::class.java)
                    }.sortedByDescending { it.createdAt }
                    trySend(reports)
                }

                override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                    close(error.toException())
                }
            })
        awaitClose { reportsRef.removeEventListener(listener) }
    }

    override suspend fun submitReport(report: BlackspotReport, imageUri: Uri?): Result<String> {
        return try {
            val reportId = reportsRef.push().key ?: throw Exception("Failed to generate report ID")
            var imageUrl = ""

            // Upload image if provided
            if (imageUri != null) {
                val imageRef = storageRef.child("$reportId.jpg")
                imageRef.putFile(imageUri).await()
                imageUrl = imageRef.downloadUrl.await().toString()
            }

            val finalReport = report.copy(
                id = reportId,
                imageUrl = imageUrl,
                createdAt = System.currentTimeMillis()
            )

            reportsRef.child(reportId).setValue(finalReport).await()
            Result.success(reportId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markAsResolved(
        reportId: String,
        adminId: String,
        notes: String
    ): Result<Unit> {
        return try {
            val updates = mapOf(
                "status" to BlackspotReport.STATUS_RESOLVED,
                "resolvedAt" to System.currentTimeMillis(),
                "resolvedBy" to adminId,
                "adminNotes" to notes
            )
            reportsRef.child(reportId).updateChildren(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getReport(reportId: String): Result<BlackspotReport> {
        return try {
            val snapshot = reportsRef.child(reportId).get().await()
            val report = snapshot.getValue(BlackspotReport::class.java)
            if (report != null) {
                Result.success(report)
            } else {
                Result.failure(Exception("Report not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteReport(reportId: String): Result<Unit> {
        return try {
            reportsRef.child(reportId).removeValue().await()
            // Also try to delete the associated image
            try {
                storageRef.child("$reportId.jpg").delete().await()
            } catch (_: Exception) {
                // Image may not exist
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
