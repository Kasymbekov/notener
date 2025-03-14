package com.nurdev.notener

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.nurdev.notener.utils.removeLines
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.URL
import java.net.URLEncoder
import javax.net.ssl.HttpsURLConnection

class NotificationListener : NotificationListenerService() {
    private val dotenv = dotenv {// token
        directory = "./assets"
        filename = "env"
    }

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
            val list = listOf(
                "Project Manager",
                "Project manager",
                "project manager",
                "PM",
                "ПМ",
                "Проектный Менеджер",
                "Проектный менеджер",
                "проектный менеджер"
            )

            if (packageName == "org.telegram.messenger" && notificationText != null) {
                if (relatedWords.any { notificationText.contains(it)}) {
                    CoroutineScope(Dispatchers.IO).launch {
                        sendSeparateMessage(
                            chatId = 759623334,
                            message = "$notificationText"
                        )
                    }
                } else if (list.any { notificationText.contains(it)}) {
                    CoroutineScope(Dispatchers.IO).launch {
                        sendSeparateMessage(
                            chatId = 759623334,
                            message = "$notificationText"
                        )
                    }
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

    private fun sendSeparateMessage(chatId: Long, message: String) {
        val url = URL("https://api.telegram.org/bot${dotenv["apiKey_notify"]}/sendMessage")
        val parameters = "chat_id=$chatId&text=${URLEncoder.encode(message, "UTF-8")}"

        with(url.openConnection() as HttpsURLConnection) {
            requestMethod = "POST"
            doOutput = true

            outputStream.write(parameters.toByteArray())

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                println("Message sent successfully")
            } else {
                println("Failed to send message: $responseCode")
            }
        }
    }
}