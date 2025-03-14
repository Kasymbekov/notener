package com.nurdev.notener

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.nurdev.notener.data.NetworkClient
import com.nurdev.notener.utils.removeLines
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URL
import java.net.URLEncoder
import javax.net.ssl.HttpsURLConnection

class NotificationListener : NotificationListenerService() {
    private val dotenv = dotenv {// token
        directory = "./assets"
        filename = "env"
    }
    private val client by lazy { NetworkClient.createClient(applicationContext) }

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        // val prefs = SharedPreferencesManager(this) // used with mbank
        sbn?.let {
            val packageName = sbn.packageName
            val notificationTitle = sbn.notification.extras.getString("android.title")
            val notificationText = sbn.notification.extras.getString("android.text")

            Log.d("nurs", "Package: $packageName")
            Log.d("nurs", "Title: $notificationTitle")
            Log.d("nurs", "Text: $notificationText")

            // telegram filter
            val relatedWords = listOf("Android", "Mobile", "Kotlin", "Андроид")

            if (packageName == "org.telegram.messenger" && notificationText != null) {
                if (relatedWords.any { notificationText.contains(it) }) {
                    sendMessage(759623334, notificationText)
                }
                cancelNotification(sbn.key) // remove the notification from the status bar
            }
            // mbank filter
            /* if (packageName == "com.maanavan.mb_kyrgyzstan") {
               if (notificationText != null) {
                   if (notificationText.contains("Пополнение")) {
                       Log.e(
                           "nurs",
                           "Prefs (notifylistener): ${prefs.getDataFromSharedPreferences()}"
                       )
                       if (notificationText.contains(
                               "*${
                                   prefs.getDataFromSharedPreferences().toString()
                               }"
                           )
                       ) {
                           var message = notificationText
                           if (notificationText.contains("Cash-in")) {
                               message = removeLines(notificationText.replace("Cash-in", ""))
                           }
                           CoroutineScope(Dispatchers.IO).launch {
                               sendSeparateMessage(
                                   chatId = 5304050752,
                                   message = "MBank\n$message"
                               )
                           }
                       }
                   }
               }
           } */
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        sbn?.let {
            Log.d("nurs", "Notification removed: ${sbn.packageName}")
        }
    }

    private fun sendMessage(chatId: Long, message: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val url =
                URL("https://api.telegram.org/bot${dotenv["apiKey_notify"]}/sendMessage?chat_id=$chatId&text=$message")
            val requestBody = "".toRequestBody()
            val request = Request
                .Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                println("Response code: ${response.code}")
            }
        }
    }
}