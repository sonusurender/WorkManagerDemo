package com.androhub.workmanagerexample

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit

class MyWorker(context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {

    companion object {
        private val TAG = MyWorker::class.java.simpleName
        private const val NOTIFICATION_CHANNEL_ID = "my_worker_notification_id"
        private const val NOTIFICATION_ID = 324

        private val constraints = Constraints.Builder()
            // WIFI network constraint
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .build()

        // one time work request
        val oneTimeWorkRequest = OneTimeWorkRequestBuilder<MyWorker>()
            .setConstraints(constraints)
            // backoff criteria for retrying the worker
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                // minimum 10 seconds required
                10, TimeUnit.SECONDS
            )
            .addTag(TAG)
            // pass data to a worker
            .setInputData(workDataOf("data" to "My name goes here"))
            // initial delay to execute the worker
            //.setInitialDelay(5, TimeUnit.SECONDS)
            .build()

        // periodic work request to execute work periodically
        val periodicWorkRequest = PeriodicWorkRequestBuilder<MyWorker>(
            15 //minimum 15 minutes is required
            , TimeUnit.MINUTES)
            .setConstraints(constraints)
            .addTag(TAG)
            //.setInitialDelay(5, TimeUnit.SECONDS)
            .build()

        fun executeOneTimeWorker(context: Context) {
            WorkManager.getInstance(context)
                .enqueueUniqueWork(TAG, ExistingWorkPolicy.REPLACE, oneTimeWorkRequest)
        }

        fun cancelOneTimeWorker(context: Context) {
            WorkManager.getInstance(context)
                .cancelWorkById(oneTimeWorkRequest.id)
        }

        fun executePeriodicWorker(context: Context) {
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    TAG,
                    ExistingPeriodicWorkPolicy.KEEP,
                    periodicWorkRequest
                )
        }

    }


    override fun doWork(): Result {
        // get the input data passed
        val data = inputData.getString("data")
        Log.d(TAG, "Input Data: $data")

        Log.d(TAG, "Do Work before sleep")
        Thread.sleep(5000L)
        Log.d(TAG, "Do Work after sleep")

        // success result -> can be failure and retry also
        return Result.success()
    }

    override fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(NOTIFICATION_ID, createNotification())
    }

    private fun createNotification(): Notification {
        createNotificationChannel()
        return NotificationCompat
            .Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(applicationContext.getString(R.string.app_name))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = applicationContext.getString(R.string.channel_name)
            val descriptionText = applicationContext.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}