package com.example.myproject.repository

import com.example.myproject.model.RequestModel

interface RequestRepo {

    fun addRequest(
        userId: String,
        model: RequestModel,
        callback: (Boolean, String) -> Unit
    )

    fun getRequests(
        userId: String,
        callback: (Boolean, String, List<RequestModel>?) -> Unit
    )

    fun updateRequest(
        userId: String,
        requestId: String,
        model: RequestModel,
        callback: (Boolean, String) -> Unit
    )

    fun deleteRequest(
        userId: String,
        requestId: String,
        callback: (Boolean, String) -> Unit
    )
}