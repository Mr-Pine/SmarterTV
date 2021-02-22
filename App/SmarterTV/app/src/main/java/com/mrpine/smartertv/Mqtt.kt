package com.mrpine.smartertv

import android.content.Context
import android.widget.Toast
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*

class Mqtt(serverURI: String, private val topic: String, private val context: Context){

    var isConnected = false

    private val clientId = MqttClient.generateClientId()
    private val client = MqttAndroidClient(context, serverURI, clientId)

    init {
        mqttConnect()
    }

    private fun mqttConnect() {
        try {
            val token = client.connect()

            token.actionCallback = object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    // We are connected
                    //Log.d(TAG, "onSuccess");
                    println("connected")
                    toast("Connected", Toast.LENGTH_SHORT)
                    isConnected = true
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    toast(exception.toString(), Toast.LENGTH_SHORT)
                    println("error on connecting: $exception")
                    isConnected = false
                }
            }

        } catch (e: MqttException) {
            println(e)
        }
    }

    fun publishMessage(message: String) {

        val payloadBytes = message.toByteArray()
        val payload = MqttMessage(payloadBytes)

        val connected = client.isConnected.toString()
        toastAndPrint("Connected: $connected", Toast.LENGTH_LONG)

        if (client.isConnected) {
            client.publish(topic, payload)
        } else {
            mqttConnect()
            publishMessage(message)
        }
    }

    fun toastAndPrint(text: String, dur: Int) {
        Toast.makeText(context , text, dur).show()
        println(text)
    }

    fun toast(text: String, dur: Int) {
        Toast.makeText(context, text, dur).show()
    }

    fun sendNumber(number : String){
        for(digitIndex in 0 until number.length){
            val char = number[digitIndex]
            publishMessage("0x$char")
            Thread.sleep(400)
        }
        publishMessage("0x35")
    }
}