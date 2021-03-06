package com.mrpine.smartertv

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mrpine.smartertv.databinding.ActivityMainBinding


@Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MainActivity : AppCompatActivity() {

    private lateinit var callIntent: Intent
    private lateinit var ipIntent: String
    private lateinit var portIntent: String

    private lateinit var serverURI: String
    private lateinit var topic: String

    //val buttons = arrayOf(button, power)
    private val buttonIDs = arrayOf("ok", "power", "up", "right", "down", "left", "info", "exit")
    private val buttonCodes = arrayOf("0x35", "0xc", "0x16", "0x12", "0x17", "0x13", "0x33", "0x1b")


    private var rcButtons = arrayOfNulls<RCButton>(buttonIDs.size)

    private lateinit var mqtt: Mqtt
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(binding.root)

    }


    override fun onBackPressed() {
        toast("Exit with home button", Toast.LENGTH_LONG)
    }

    private fun toast(text: String, dur: Int) {
        Toast.makeText(this, text, dur).show()
    }

    /*fun toastAndPrint(text: String, dur: Int) {
        Toast.makeText(this, text, dur).show()
        println(text)
    }*/

    private fun EditText.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun setNotification() {
        if (Build.VERSION.SDK_INT >= 26) {

            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("test", name, importance).apply {
                description = descriptionText
            }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }
    }
}


