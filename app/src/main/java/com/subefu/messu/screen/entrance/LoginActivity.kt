package com.subefu.messu.screen.entrance

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.subefu.messu.MainActivity
import com.subefu.messu.databinding.ActivityLoginBinding
import com.subefu.messu.utils.Cipher

class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        context = this

        binding.btLoginLogin.setOnClickListener {
            val email = binding.etLoginEmail.text.toString()
            val temporarilyPassword = binding.etLoginPassword.text.toString()
            val password = Cipher.encryptPassword(context, temporarilyPassword)

            if(email.isEmpty() || password.isEmpty())
                Toast.makeText(context, "Field can't be empty", Toast.LENGTH_SHORT).show()
            else if(email.length < 7 || password.length < 4)
                Toast.makeText(context, "Field can't be that short", Toast.LENGTH_SHORT).show()
            else{
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if (it.isSuccessful)
                            startActivity(Intent(context, MainActivity::class.java))
                        else
                            Toast.makeText(context, "User not be find", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        binding.lnLoginSignUp.setOnClickListener {
            startActivity(Intent(context, SignUpActivity::class.java))
        }

    }

    override fun onBackPressed() {
        finishAffinity()
    }
}