package com.mrpine.smartertv

import android.content.Context
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.mrpine.smartertv.databinding.TvFragmentBinding

class TVFragment: Fragment() {

    private lateinit var serverURI: String

    private val buttonIDs = arrayOf("ok", "power", "up", "right", "down", "left", "info", "exit", "mute", "input")
    private val buttonCodes = arrayOf("0x35", "0xc", "0x16", "0x12", "0x17", "0x13", "0x33", "0x1b", "", "")

    private var rcButtons = arrayOfNulls<RCButton>(buttonIDs.size)

    private var _binding: TvFragmentBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = TvFragmentBinding.inflate(inflater)

        val view = binding.root
        val numberInput = binding.numberInput
        val numberSend = binding.numberSend



        if(arguments != null){
            serverURI = "tcp://${requireArguments()["ip"]}:${requireArguments()["port"]}"
        }

        val mqtt = Mqtt(serverURI, "TV", context as Context)

        for (index in buttonIDs.indices) {
            val id = buttonIDs[index]
            val buttonObject: Button = view.findViewById(resources.getIdentifier(id, "id", "com.mrpine.smartertv"))
            println(buttonObject)
            val code = buttonCodes[index]
            val rcButton = RCButton(buttonObject, id, code, context as Context, serverURI, "TV")
            rcButtons[index] = rcButton
        }

        numberInput.setOnEditorActionListener { _, _, _ ->
            numberInput.hideKeyboard()
            mqtt.sendNumber(numberInput.text.toString())
            numberInput.text = SpannableStringBuilder("")
            true
        }

        numberSend.setOnClickListener {
            numberInput.hideKeyboard()
            mqtt.sendNumber(numberInput.text.toString())
            numberInput.text = SpannableStringBuilder("")
        }



        return view
    }

    private fun EditText.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }
}