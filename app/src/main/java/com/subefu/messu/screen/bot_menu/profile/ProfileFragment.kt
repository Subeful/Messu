package com.subefu.messu.screen.bot_menu.profile

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.subefu.messu.R
import com.subefu.messu.databinding.FragmentProfileBinding
import com.subefu.messu.screen.chat.ChatActivity
import com.subefu.messu.screen.entrance.LoginActivity
import com.subefu.messu.utils.ChatUtil.createChat
import com.subefu.messu.utils.ChatUtil.generateChatId
import com.subefu.messu.utils.SetFragment
import com.subefu.messu.utils.UpdateFragment
import java.util.Locale

class ProfileFragment : Fragment() {

    lateinit var binding: FragmentProfileBinding
    lateinit var alert: AlertDialog.Builder
    lateinit var alertDialog: AlertDialog.Builder
    lateinit var languagePreferences: SharedPreferences

    lateinit var functionUpdateFragment: UpdateFragment
    lateinit var functionChangeFragment: SetFragment

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        binding.ibProfileLogOut.setOnClickListener {
            alert = AlertDialog.Builder(container!!.context)
                .setTitle("${getString(R.string.logout)}?")
                .setPositiveButton(getString(R.string.yes), object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        FirebaseAuth.getInstance().signOut()
                        startActivity(Intent(context, LoginActivity::class.java))
                    }
                }).setNegativeButton(getString(R.string.no), object : DialogInterface.OnClickListener{
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        return
                    }
                })
            alert.create().show()
        }

        binding.lnProfileLanguage.setOnClickListener {
            chooseLanguage(inflater.context, it)
        }
        binding.lnProfileAskAdmin.setOnClickListener {
            askAdmin(inflater.context)
        }

        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    binding.tvProfileUsername.setText(snapshot.child("username").value.toString())
                }
                override fun onCancelled(error: DatabaseError) {}
            })

        languagePreferences = inflater.context.getSharedPreferences("language", Context.MODE_PRIVATE)

        setLanguage(inflater.context)
        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        functionUpdateFragment = context as UpdateFragment
        functionChangeFragment = context as SetFragment
    }

    fun setLanguage(context: Context){
        val language = context.getSharedPreferences("language", Context.MODE_PRIVATE)?.getString("language", "en")

        val locale = Locale(language.toString())
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        resources.updateConfiguration(config, resources.displayMetrics)
    }

    @SuppressLint("MissingInflatedId")
    fun chooseLanguage(context: Context, v: View?) {
        val view: View = LayoutInflater.from(context).inflate(R.layout.alert_choose_langusge, null)
        val ru_B = view.findViewById<RadioButton>(R.id.radioB_ru)
        val en_B = view.findViewById<RadioButton>(R.id.radioB_en)

        val editor: SharedPreferences.Editor = languagePreferences.edit()
        var language = ""

        when (languagePreferences.getString("language", "en")) {
            "ru" -> {ru_B.isChecked = true
                language = "ru"}
            else -> {en_B.isChecked = true
                language = "en"}
        }

        ru_B.setOnClickListener {
            en_B.isChecked = false
            language = "ru"
        }

        en_B.setOnClickListener {
            ru_B.isChecked = false
            language = "en"
        }

        alertDialog = AlertDialog.Builder(v!!.context)
            .setView(view)
            .setTitle(v.context.getString(R.string.change_language))
            .setPositiveButton(context?.getString(R.string.yes), object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    editor.putString("language", language).apply()
                    setLanguage(context)
                    functionUpdateFragment.updateFragment(ProfileFragment())
                    return
                }
            })
            .setNegativeButton(context?.getString(R.string.no), object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) { return }
            })
        alertDialog.create().show()
    }

    fun askAdmin(context: Context){
        createChat(context, "oCUUSPWvD7aUOTiX4QeeBWXIywm2")
        val intent = Intent(context.applicationContext, ChatActivity::class.java)

        val id = FirebaseAuth.getInstance().currentUser!!.uid.toString()
        intent.putExtra("chat_id", generateChatId("oCUUSPWvD7aUOTiX4QeeBWXIywm2", id))
        context.startActivity(intent)
    }

}