package com.subefu.messu

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.subefu.messu.databinding.ActivityMainBinding
import com.subefu.messu.screen.bot_menu.chats.ChatsFragment
import com.subefu.messu.screen.bot_menu.new_chat.NewChatFragment
import com.subefu.messu.screen.bot_menu.profile.ProfileFragment
import com.subefu.messu.screen.entrance.LoginActivity
import com.subefu.messu.utils.SetFragment
import com.subefu.messu.utils.UpdateFragment
import java.util.Locale

class MainActivity : AppCompatActivity(), SetFragment, UpdateFragment {

    lateinit var binding: ActivityMainBinding
    lateinit var context: Context
    lateinit var botNavMain: BottomNavigationView

    lateinit var language: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkIsAuthorisation()
        setLanguage(this)

        init()

        botNavMain.setOnItemSelectedListener{it ->
            when(it.itemId){
                R.id.bot_menu_chats -> setFragment(ChatsFragment())
                R.id.bot_menu_new_chat -> setFragment(NewChatFragment())
                R.id.bot_menu_profile -> setFragment(ProfileFragment())
            }
            true
        }
    }

    fun checkIsAuthorisation(){
        if(FirebaseAuth.getInstance().currentUser == null){
            finish()
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    fun init(){
        context = this

        setFragment(ChatsFragment())
        botNavMain = binding.botNavMain
        botNavMain.itemActiveIndicatorColor = getColorStateList(R.color.transparent)

        language = getSharedPreferences("langusge", MODE_PRIVATE)
        language.edit().putString("language", "en").apply()
    }

    fun setFragment(fragment: Fragment){ supportFragmentManager.beginTransaction().replace(binding.frameMain.id, fragment).commit() }

    fun setLanguage(context: Context){
        val language = context.getSharedPreferences("language", Context.MODE_PRIVATE)?.getString("language", "ru")
        val locale = Locale(language.toString())
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    override fun setOtherFragment(fragment: Fragment) {
        setFragment(NewChatFragment())
    }

    override fun updateFragment(fragment: Fragment) {
        setLanguage(context)
        finish()
        startActivity(Intent(intent))
        setFragment(fragment)
    }
}