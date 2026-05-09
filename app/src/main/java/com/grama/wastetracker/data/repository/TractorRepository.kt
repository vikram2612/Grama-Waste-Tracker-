package com.grama.wastetracker.data.repository

import com.google.firebase.database.FirebaseDatabase
import com.grama.wastetracker.data.model.TractorLocation
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface TractorRepository {
    fun observeTractorLocation(): Flow<TractorLocation?>
    suspend fun updateTractorLocation(location: TractorLocation): Result<Unit>
    suspend fun updateTractorStatus(isActive: Boolean): Result<Unit>
    suspend fun getTractorLocation(): Result<TractorLocation>
}

class TractorRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase
) : TractorRepository {

    private val tractorRef = database.getReference("tractor_location")

    override fun observeTractorLocation(): Flow<TractorLocation?> = callbackFlow {
        val listener = tractorRef.addValueEventListener(
            object : com.google.firebase.database.ValueEventListener {
                override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                    val location = snapshot.getValue(TractorLocation::class.java)
                    trySend(location)
                }

                override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                    close(error.toException())
                }
            }
        )
        awaitClose { tractorRef.removeEventListener(listener) }
    }

    override suspend fun updateTractorLocation(location: TractorLocation): Result<Unit> {
        return try {
            tractorRef.setValue(location).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateTractorStatus(isActive: Boolean): Result<Unit> {
        return try {
            val updates = mapOf(
                "isActive" to isActive,
                "lastUpdated" to System.currentTimeMillis()
            )
            tractorRef.updateChildren(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTractorLocation(): Result<TractorLocation> {
        return try {
            val snapshot = tractorRef.get().await()
            val location = snapshot.getValue(TractorLocation::class.java)
            if (location != null) {
                Result.success(location)
            } else {
                Result.failure(Exception("Tractor location not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
