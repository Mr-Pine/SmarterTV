package com.kieferd.smartertv

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.kieferd.smartertv.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)


        binding.register.setOnClickListener {
            auth.createUserWithEmailAndPassword(binding.email.text.toString(), binding.password.text.toString()).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    println("Registration Successful")
                    val user = auth.currentUser
                    val data = HashMap<String, Any>()
                    data["ip"] = binding.ip.text.toString()
                    data["port"] = binding.port.text.toString()
                    firestore.collection("Users").document(user?.uid.toString()).set(data)
                }else{
                    val error = task.exception
                    println("Error on registration $error")
                }
            }
        }

        binding.logIn.setOnClickListener{
            auth.signInWithEmailAndPassword(binding.email.text.toString(), binding.password.text.toString()).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    println("Login successful")
                }else{
                    println("error on login: ${task.exception}")
                }
            }
        }

        auth.addAuthStateListener { authLambda ->
            val user = authLambda.currentUser
            if (user != null) {
                val uid = user.uid
//                var ipAddress: String
//                var portFirestore: String
                firestore.collection("Users").document(uid).get().addOnSuccessListener { doc ->
                    val ipAddress = doc["ip"].toString()
                    val portFirestore = doc["port"].toString()
                    val intent = Intent(this@LoginActivity, Main2Activity::class.java)
                    intent.putExtra("id", ipAddress)
                    intent.putExtra("port", portFirestore)
                    startActivity(intent)
                }
            } else {
                println("User not signed in")
            }
        }
    }
}
