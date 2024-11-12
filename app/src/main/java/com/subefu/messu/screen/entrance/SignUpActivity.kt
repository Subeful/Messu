package com.subefu.messu.screen.entrance

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.subefu.messu.MainActivity
import com.subefu.messu.databinding.ActivitySignUpBinding
import com.subefu.messu.utils.Cipher

class SignUpActivity : AppCompatActivity() {

    lateinit var binding: ActivitySignUpBinding
    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        binding.btSignUpSignUp.setOnClickListener {
            val email = binding.etSignUpEmail.text.toString().trim()
            val username = binding.etSignUpUsername.text.toString().trim()
            val temporarilyPassword = binding.etSignUpPassword.text.toString().trim()
            val password = Cipher.encryptPassword(context, temporarilyPassword)

            if(email.isEmpty() || password.isEmpty() || username.isEmpty())
                Toast.makeText(context, "Field can't be empty", Toast.LENGTH_SHORT).show()
            else if(email.length < 7 || password.length < 4 || username.length < 2)
                Toast.makeText(context, "Field can't be that short", Toast.LENGTH_SHORT).show()
            else{
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->

                    val db = FirebaseDatabase.getInstance().getReference("Users")
                    val id = FirebaseAuth.getInstance().currentUser!!.uid

                    if(id != null){
                        val userInfo = mapOf("id" to id,
                            "email" to email,
                            "username" to username,
                            "password" to password,
                            "chats" to "",
                            "profileImage" to ""
                        )

                        db.child(id).setValue(userInfo)
                        startActivity(Intent(context, MainActivity::class.java))
                    }
                    else
                        Toast.makeText(context, "Oh, we catch some error. Try again", Toast.LENGTH_LONG).show()
                    }.addOnFailureListener{
                        Toast.makeText(context, "Print trust data", Toast.LENGTH_SHORT).show()

                }
            }
        }

        binding.btSignUpBack.setOnClickListener {
            finish()
        }

    }
    fun init(){
        context = this

        setFieldIfNotNull()
    }

    fun setFieldIfNotNull(){
        val mail = intent.getStringExtra("user_mail").toString()
        val password = intent.getStringExtra("user_password").toString()

        if(mail.isEmpty().not())    binding.etSignUpEmail.setText(mail)
        if(password.isEmpty().not())    binding.etSignUpPassword.setText(password)
    }
}