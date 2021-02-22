package com.mrpine.smartertv

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment

class RadioFragment: Fragment() {

    lateinit var serverURI: String

    private val buttonIDs = arrayOf(    "okR",      "powerR",   "muteR",    "timerR")
    private val buttonCodes = arrayOf(  "0xff11ee", "0xff619e", "0xffa15e", "0xffc936")

    private var rcButtons = arrayOfNulls<RCButton>(buttonIDs.size)



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.radio_fragment, container, false)
        val numberInput: EditText = v.findViewById(R.id.numberInputR)
        val numberSend: Button = v.findViewById(R.id.numberSendR)

        if(arguments != null){
            serverURI = "tcp://${requireArguments()["ip"]}:${requireArguments()["port"]}"
        }

        val mqtt = Mqtt(serverURI, "TV", context as Context)

        for (index in buttonIDs.indices) {
            val id = buttonIDs[index]
            val buttonObject: Button = v.findViewById(resources.getIdentifier(id, "id", "com.kieferd.smartertv"))
            println(buttonObject)
            val code = buttonCodes[index]
            val rcButton = RCButton(buttonObject, id, code, context as Context, serverURI, "Radio")
            rcButtons[index] = rcButton
        }

        /*numberInput.setOnEditorActionListener { _, _, _ ->
            numberInput.hideKeyboard()
            mqtt.sendNumber(numberInput.text.toString())
            numberInput.text = SpannableStringBuilder("")
            true
        }

        numberSend.setOnClickListener {
            numberInput.hideKeyboard()
            mqtt.sendNumber(numberInput.text.toString())
            numberInput.text = SpannableStringBuilder("")
        }*/



        return v
    }

    private fun EditText.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}