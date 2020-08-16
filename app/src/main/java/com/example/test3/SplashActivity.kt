package com.example.test3

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Base64.*
import android.util.Log
import android.widget.Toast
//import com.kakao.util.helper.Utility
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

const val SPLASH_VIEW_TIME = 1200
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val a= getKeyHash(this)
//        Log.d("KeyTest", a)
        Handler().postDelayed({ //delay를 위한 handler
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, SPLASH_VIEW_TIME.toLong())
    }


}

//public fun getKeyHash(context: Context): String {
//    var packageInfo: PackageInfo = Utility.getPackageInfo(context, PackageManager.GET_SIGNATURES)
//    for (signature in packageInfo.signatures) {
//        try {
//            var md : MessageDigest = MessageDigest.getInstance("SHA")
//            md.update(signature.toByteArray())
//
//            return encodeToString(md.digest(), NO_WRAP)
//        } catch (e: NoSuchAlgorithmException) {
//            Log.w("TAG", "디버그 keyHash" + signature, e)
//        }
//    }
//    return ""
//}