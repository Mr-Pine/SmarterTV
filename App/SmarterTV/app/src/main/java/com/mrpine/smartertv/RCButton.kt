package com.mrpine.smartertv

import android.content.Context
import android.widget.Button

class RCButton(button : Button, private val id: String, private val code: String, context: Context, serverURI : String, topic: String)  {
    private val mqtt = Mqtt(serverURI, topic, context)

    init {
        button.setOnClickListener {
            mqtt.publishMessage(code)
            mqtt.toastAndPrint(id, 0)
        }
    }
}