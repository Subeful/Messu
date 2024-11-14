package com.subefu.messu.screen.bot_menu.profile

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
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
import java.text.SimpleDateFormat
import java.util.Date
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

        init(inflater)

        binding.ibProfileLogOut.setOnClickListener { logout(container) }

        binding.lnProfileLanguage.setOnClickListener { chooseLanguage(inflater.context, it) }
        binding.lnProfileAskAdmin.setOnClickListener { askAdmin(inflater.context) }
        binding.lnProfileStatistics.setOnClickListener {Toast.makeText(inflater.context, getString(R.string.development), Toast.LENGTH_SHORT).show() }
        binding.lnProfileSettings.setOnClickListener {
            val intent = Intent(inflater.context, ProfileSettingsActivity::class.java)
            intent.putExtra("username", binding.tvProfileUsername.text.toString())
            inflater.context.startActivity(intent)
        }
        binding.imProfileAva.setOnClickListener { chooseAva(inflater) }

        return binding.root
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        functionUpdateFragment = context as UpdateFragment
        functionChangeFragment = context as SetFragment
    }

    fun init(inflater: LayoutInflater){
        setName()
        loadAva()
        languagePreferences = inflater.context.getSharedPreferences("language", Context.MODE_PRIVATE)
    }

    fun setName(){
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    binding.tvProfileUsername.setText(snapshot.child("username").value.toString())
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun logout(container: ViewGroup?){
        alert = AlertDialog.Builder(container!!.context)
            .setTitle("${getString(R.string.logout)}?")
            .setPositiveButton(getString(R.string.yes), object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    setLastEntrance()
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

    fun askAdmin(context: Context){
        createChat(context, "1l49RmrCccRAVyrjCXq2p8gU8Wm2")
        val intent = Intent(context.applicationContext, ChatActivity::class.java)

        val id = FirebaseAuth.getInstance().currentUser!!.uid.toString()
        intent.putExtra("chat_id", generateChatId("1l49RmrCccRAVyrjCXq2p8gU8Wm2", id))
        context.startActivity(intent)
    }

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
                    functionUpdateFragment.updateFragment(ProfileFragment())
                    return
                }
            })
            .setNegativeButton(context?.getString(R.string.no), object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, which: Int) { return }
            })
        alertDialog.create().show()
    }

    fun chooseAva(inflater: LayoutInflater){
        val view = LayoutInflater.from(inflater.context).inflate(R.layout.alert_choose_ava, null)

        val im_1 = view.findViewById<ImageButton>(R.id.alert_choose_1)
        val im_2 = view.findViewById<ImageButton>(R.id.alert_choose_3)
        val im_3 = view.findViewById<ImageButton>(R.id.alert_choose_2)
        val im_4 = view.findViewById<ImageButton>(R.id.alert_choose_4)

        im_1.setOnClickListener { setAva(1) }
        im_2.setOnClickListener { setAva(2) }
        im_3.setOnClickListener { setAva(3) }
        im_4.setOnClickListener { setAva(4) }

        AlertDialog.Builder(inflater.context).setView(view).create().show()
    }
    fun setAva(imageId: Int){
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("profileImage").setValue(imageId)

        when(imageId){
            1 -> binding.imProfileAva.setBackgroundResource(R.drawable.png_animal_1)
            2 -> binding.imProfileAva.setBackgroundResource(R.drawable.png_animal_2)
            3 -> binding.imProfileAva.setBackgroundResource(R.drawable.png_animal_3)
            4 -> binding.imProfileAva.setBackgroundResource(R.drawable.png_animal_4)
        }
    }
    fun loadAva(){
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("profileImage").addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    when(snapshot.value.toString()){
                        "1" -> binding.imProfileAva.setBackgroundResource(R.drawable.png_animal_1)
                        "2" -> binding.imProfileAva.setBackgroundResource(R.drawable.png_animal_2)
                        "3" -> binding.imProfileAva.setBackgroundResource(R.drawable.png_animal_3)
                        "4" -> binding.imProfileAva.setBackgroundResource(R.drawable.png_animal_4)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun setLastEntrance(){
        val date = getCurrentDate()
        FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("status").setValue(date)
    }
    fun getCurrentDate():String{
        val format = SimpleDateFormat("HH:mm dd.MM.yy")
        val date = format.format(Date().time)
        return date
    }


}
