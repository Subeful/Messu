package com.subefu.messu.utils

import android.content.Context
import android.widget.Toast
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


object Cipher {
    fun encryptPassword(context: Context, password: String): String{
        val sifrPassword = StringBuilder()
        try {
            val md5 = MessageDigest.getInstance("Md5")
            val bytes = md5.digest(password.toByteArray())
            bytes.forEach {
                sifrPassword.append(String.format("%02x", it))
            }
        } catch (e: NoSuchAlgorithmException) {
            Toast.makeText(context, "error instance md5", Toast.LENGTH_SHORT).show()
        }
        return sifrPassword.toString()
    }
}