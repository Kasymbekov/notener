package com.nurdev.notener.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputEditText
import com.nurdev.notener.R
import com.nurdev.notener.SharedPreferencesManager
import com.nurdev.notener.databinding.ActivityMainBinding
import com.nurdev.notener.utils.isDigitsOnly
import io.github.cdimascio.dotenv.dotenv

class MainActivity : AppCompatActivity() {
    private lateinit var prefs: SharedPreferencesManager
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding for ActivityMainBinding must not be null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        prefs = SharedPreferencesManager(this)

        if (!isNotificationListenerEnabled(this)) {
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            startActivity(intent)
        }

        val cardNumber = prefs.getDataFromSharedPreferences()
        findViewById<TextView>(R.id.currentFilter).text = "Текущий фильтр: *$cardNumber"

        findViewById<Button>(R.id.btn).setOnClickListener {
            val card = findViewById<TextInputEditText>(R.id.card).text
            if (card?.length == 4 && isDigitsOnly(card.toString())) {
                changeCardNumber()
                Log.e("nurs", "Prefs (activity): ${prefs.getDataFromSharedPreferences()}")
            } else {
                Toast.makeText(this, "Заполните корректно", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun changeCardNumber() {
        prefs.saveDataToSharedPreferences(findViewById<TextInputEditText>(R.id.card).text.toString())
        val cardNumber = prefs.getDataFromSharedPreferences()
        findViewById<TextView>(R.id.currentFilter).text = "Текущий фильтр: *$cardNumber"
        findViewById<TextInputEditText>(R.id.card).text?.clear()
        Toast.makeText(this, "Фильтр установлен", Toast.LENGTH_SHORT).show()
    }

    private fun isNotificationListenerEnabled(context: Context): Boolean {
        val enabledListeners = NotificationManagerCompat.getEnabledListenerPackages(context)
        return enabledListeners.contains(context.packageName)
    }
}