package com.example.myproject.repository

import android.util.Log
import com.example.myproject.model.RequestModel
import com.google.firebase.firestore.FirebaseFirestore

class RequestRepoImpl : RequestRepo {

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Helper to get the requests collection reference
    private fun requestsRef(userId: String) =
        db.collection("users").document(userId).collection("requests")

    override fun addRequest(
        userId: String,
        model: RequestModel,
        callback: (Boolean, String) -> Unit
    ) {
        val docRef = requestsRef(userId).document()
        val requestWithId = model.copy(requestId = docRef.id)

        docRef.set(requestWithId.toMap())
            .addOnSuccessListener {
                callback(true, "Request added successfully")
            }
            .addOnFailureListener { e ->
                Log.e("RequestRepoImpl", "addRequest error", e)
                callback(false, e.localizedMessage ?: "Failed to add request")
            }
    }

    override fun getRequests(
        userId: String,
        callback: (Boolean, String, List<RequestModel>?) -> Unit
    ) {
        requestsRef(userId).get()
            .addOnSuccessListener { snapshot ->
                val requests = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(RequestModel::class.java)
                }
                callback(true, "Requests fetched", requests)
            }
            .addOnFailureListener { e ->
                Log.e("RequestRepoImpl", "getRequests error", e)
                callback(false, e.localizedMessage ?: "Failed to fetch requests", null)
            }
    }

    override fun updateRequest(
        userId: String,
        requestId: String,
        model: RequestModel,
        callback: (Boolean, String) -> Unit
    ) {
        requestsRef(userId).document(requestId).update(model.toMap())
            .addOnSuccessListener {
                callback(true, "Request updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e("RequestRepoImpl", "updateRequest error", e)
                callback(false, e.localizedMessage ?: "Failed to update request")
            }
    }

    override fun deleteRequest(
        userId: String,
        requestId: String,
        callback: (Boolean, String) -> Unit
    ) {
        requestsRef(userId).document(requestId).delete()
            .addOnSuccessListener {
                callback(true, "Request deleted successfully")
            }
            .addOnFailureListener { e ->
                Log.e("RequestRepoImpl", "deleteRequest error", e)
                callback(false, e.localizedMessage ?: "Failed to delete request")
            }
    }
}