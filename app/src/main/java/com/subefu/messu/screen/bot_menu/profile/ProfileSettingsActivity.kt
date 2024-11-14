package com.subefu.messu.screen.bot_menu.profile

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.subefu.messu.R
import com.subefu.messu.databinding.ActivityProfileSettingsBinding


class ProfileSettingsActivity : AppCompatActivity() {

    lateinit var binding: ActivityProfileSettingsBinding
    lateinit var context: Context
    lateinit var uid: String

    lateinit var b_name: String
    lateinit var b_email: String

    lateinit var configChats: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSettingsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        init()

        binding.settingsApplyUser.setOnClickListener { updateUserData() }
        binding.settingsApplyChat.setOnClickListener { updateViewConfig() }
    }

    fun init() {
        context = this
        uid = FirebaseAuth.getInstance().currentUser!!.uid
        configChats = getSharedPreferences("configChats", MODE_PRIVATE)

        val name = intent.getStringExtra("username") ?: "null"
        binding.settingsTvUsername.text = name
        binding.settingsUsername.setText(name)

        setEmail()
        setImage()

        setChatConfig()

        b_name = name
        //b_email initialization in setEmail
    }

    fun setImage(){
        FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("profileImage").get()
            .addOnCompleteListener {
                if(it.isSuccessful)
                    when(it.result.value.toString()){
                        "1" -> {binding.settingsAva.setBackgroundResource(R.drawable.png_animal_1)}
                        "2" -> {binding.settingsAva.setBackgroundResource(R.drawable.png_animal_2)}
                        "3" -> {binding.settingsAva.setBackgroundResource(R.drawable.png_animal_3)}
                        "4" -> {binding.settingsAva.setBackgroundResource(R.drawable.png_animal_4)}
                    }
            }

    }
    fun setEmail(){
        FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("email").get()
            .addOnCompleteListener {
                if(it.isSuccessful) {
                    binding.settingsEmail.setText(it.result.value.toString())
                    b_email = it.result.value.toString()
                }
            }
    }

    fun setChatConfig(){
        val textSize = configChats.getInt("textSize", 16).toString()
        val textColor = configChats.getString("textColor", "#000000").toString()
        val textColor_m = configChats.getString("textColor_m", "#ADEBFF").toString()
        val textColor_y = configChats.getString("textColor_y", "#BCBDC0").toString()

        binding.settingsTextSize.setText(textSize)
        binding.settingsTextColor.setText(textColor)
        binding.settingsTextMColor.setText(textColor_m)
        binding.settingsTextYColor.setText(textColor_y)

        updateViewConfig()
    }

    fun updateViewConfig(){
        val textSize = binding.settingsTextSize.text.toString()
        val textColor = binding.settingsTextColor.text.toString()
        val textColor_m = binding.settingsTextMColor.text.toString()
        val textColor_y = binding.settingsTextYColor.text.toString()

        if(!checkNormalValue(textSize, textColor, textColor_m, textColor_y)) return

        //mess 1
        binding.settingsMessage1.setCardBackgroundColor(Color.parseColor(textColor_y))
        binding.settingsMessageText1.textSize = textSize.toFloat()
        binding.settingsMessageText1.setTextColor(Color.parseColor(textColor))
        binding.settingsMessageTime1.setTextColor(Color.parseColor(textColor))
        //mess 2
        binding.settingsMessage2.setCardBackgroundColor(Color.parseColor(textColor_m))
        binding.settingsMessageText2.textSize = textSize.toFloat()
        binding.settingsMessageText2.setTextColor(Color.parseColor(textColor))
        binding.settingsMessageTime2.setTextColor(Color.parseColor(textColor))
        //mess 3
        binding.settingsMessage3.setCardBackgroundColor(Color.parseColor(textColor_y))
        binding.settingsMessageText3.textSize = textSize.toFloat()
        binding.settingsMessageText3.setTextColor(Color.parseColor(textColor))
        binding.settingsMessageTime3.setTextColor(Color.parseColor(textColor))

        saveChatConfig(textSize, textColor, textColor_m, textColor_y)
    }
    fun saveChatConfig(textSize: String, textColor: String, textColor_m: String, textColor_y: String){
        val editor = configChats.edit()
        editor.putInt("textSize", textSize.toInt())
        editor.putString("textColor", textColor)
        editor.putString("textColor_m", textColor_m)
        editor.putString("textColor_y", textColor_y)
        editor.apply()
    }

    fun updateUserData(){
        val name = binding.settingsUsername.text.toString().trim()
        val email = binding.settingsEmail.text.toString().trim()

        if(name == b_name && email == b_email) return

        if(name != b_name && email == b_email)
            FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("username").setValue(name)
        else if((name == b_name && email != b_email)) {
            FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("email").setValue(email)

            val user = FirebaseAuth.getInstance().currentUser
            if(user != null ){
                user.verifyBeforeUpdateEmail(email)
            }
        }
        else{
            FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("username").setValue(name)
            FirebaseDatabase.getInstance().getReference().child("Users").child(uid).child("email").setValue(email)
            val user = FirebaseAuth.getInstance().currentUser
            if(user != null ){
                user.verifyBeforeUpdateEmail(email)
            }
        }
    }

    fun checkNormalValue(textSize: String, textColor: String, textColor_m: String, textColor_y: String): Boolean{
        try{ textSize.toFloat() }
        catch (e : Exception){
            Toast.makeText(context, "error text size", Toast.LENGTH_SHORT).show()
            return false
        }

        if (!textColor_m.startsWith("#") || !textColor_y.startsWith("#") || !textColor.startsWith("#")){
            Toast.makeText(context, "error color(must start with '#')", Toast.LENGTH_LONG).show()
            return false
        }
        if(textColor_y.length != 7 || textColor_m.length != 7 || textColor.length != 7){
            Toast.makeText(context, "error color(length mast 7 sign)", Toast.LENGTH_LONG).show()
            return false
        }

        return true
    }
}
