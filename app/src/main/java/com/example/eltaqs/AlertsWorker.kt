package com.example.eltaqs

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class AlertsWorker(
    private val context: Context,
    private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        return Result.success()
    }
}