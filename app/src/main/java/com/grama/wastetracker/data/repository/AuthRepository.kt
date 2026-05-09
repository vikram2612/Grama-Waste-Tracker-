package com.grama.wastetracker.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.grama.wastetracker.data.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

interface AuthRepository {
    val currentUser: FirebaseUser?
    val isLoggedIn: Boolean
    suspend fun login(email: String, password: String): Result<FirebaseUser>
    suspend fun register(email: String, password: String, user: User): Result<FirebaseUser>
    suspend fun getUserData(uid: String): Result<User>
    fun observeUserData(uid: String): Flow<User?>
    suspend fun updateFcmToken(uid: String, token: String)
    suspend fun resetPassword(email: String): Result<Unit>
    fun logout()
}

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase
) : AuthRepository {

    private val usersRef = database.getReference("users")

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    override val isLoggedIn: Boolean
        get() = auth.currentUser != null

    override suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        user: User
    ): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user!!
            val userData = user.copy(uid = firebaseUser.uid, email = email)
            usersRef.child(firebaseUser.uid).setValue(userData).await()
            Result.success(firebaseUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserData(uid: String): Result<User> {
        return try {
            val snapshot = usersRef.child(uid).get().await()
            val user = snapshot.getValue(User::class.java)
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun observeUserData(uid: String): Flow<User?> = callbackFlow {
        val listener = usersRef.child(uid)
            .addValueEventListener(object : com.google.firebase.database.ValueEventListener {
                override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    trySend(user)
                }

                override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                    close(error.toException())
                }
            })
        awaitClose { usersRef.child(uid).removeEventListener(listener) }
    }

    override suspend fun updateFcmToken(uid: String, token: String) {
        try {
            usersRef.child(uid).child("fcmToken").setValue(token).await()
        } catch (_: Exception) {
            // Silent failure for token update
        }
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun logout() {
        auth.signOut()
    }
}
