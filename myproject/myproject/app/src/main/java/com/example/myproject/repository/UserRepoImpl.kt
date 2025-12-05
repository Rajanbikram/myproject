package com.example.myproject.repository

import android.util.Log
import com.example.myproject.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UserRepoImpl : UserRepo {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val ref: DatabaseReference = database.getReference("Users")

    override fun login(
        email: String,
        password: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Login success")
                } else {
                    val error = task.exception?.localizedMessage ?: "Login failed"
                    Log.e("UserRepoImpl", "Login Error", task.exception)
                    callback(false, error)
                }
            }
    }

    override fun register(
        email: String,
        password: String,
        callback: (Boolean, String, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Registration success", auth.currentUser?.uid ?: "")
                } else {
                    Log.e("UserRepoImpl", "Registration Error", task.exception)
                    val errorMessage = when (task.exception) {
                        is FirebaseAuthUserCollisionException ->
                            "This email is already registered."
                        is FirebaseAuthWeakPasswordException ->
                            "Password is too weak. Must be at least 6 characters."
                        is FirebaseAuthInvalidCredentialsException ->
                            "Invalid email format."
                        else -> task.exception?.localizedMessage ?: "Registration failed"
                    }
                    callback(false, errorMessage, "")
                }
            }
    }

    override fun addUserToDatabase(
        userId: String,
        model: User,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(userId).setValue(model).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true, "User data saved successfully")
            } else {
                Log.e("UserRepoImpl", "Database Error", task.exception)
                callback(false, task.exception?.localizedMessage ?: "Failed to save user data")
            }
        }
    }

    override fun forgetPassword(
        email: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Reset email sent to $email")
                } else {
                    callback(false, task.exception?.localizedMessage ?: "Failed to send reset email")
                }
            }
    }

    override fun deleteAccount(
        userId: String,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(userId).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callback(true, "Account deleted successfully")
            } else {
                callback(false, task.exception?.localizedMessage ?: "Failed to delete account")
            }
        }
    }

    override fun editProfile(
        userId: String,
        model: User,
        callback: (Boolean, String) -> Unit
    ) {
        ref.child(userId).updateChildren(model.toMap())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "Profile updated successfully")
                } else {
                    callback(false, task.exception?.localizedMessage ?: "Update failed")
                }
            }
    }

    override fun getUserById(
        userId: String,
        callback: (Boolean, String, User?) -> Unit
    ) {
        ref.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        callback(true, "Profile fetched", user)
                    } else {
                        callback(false, "User data is empty", null)
                    }
                } else {
                    callback(false, "User does not exist", null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, null)
            }
        })
    }

    override fun getAllUser(callback: (Boolean, String, List<User>?) -> Unit) {
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val allUsers = mutableListOf<User>()
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val user = data.getValue(User::class.java)
                        if (user != null) {
                            allUsers.add(user)
                        }
                    }
                    callback(true, "Data fetched successfully", allUsers)
                } else {
                    callback(false, "No users found", emptyList())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false, error.message, emptyList())
            }
        })
    }
}
