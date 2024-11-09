package com.subefu.messu

import android.content.Intent
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

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var botNavMain: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkIsAuthorisation()

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
        setFragment(ChatsFragment())
        botNavMain = binding.botNavMain
        botNavMain.itemActiveIndicatorColor = getColorStateList(R.color.transparent)
    }

    fun setFragment(fragment: Fragment){
        supportFragmentManager.beginTransaction().replace(binding.frameMain.id, fragment).commit()
    }
}