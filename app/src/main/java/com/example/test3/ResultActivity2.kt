package com.example.test3

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_result.*
import kotlinx.android.synthetic.main.activity_result.buttonMain
import kotlinx.android.synthetic.main.activity_result.buttonSave
import kotlinx.android.synthetic.main.activity_result.buttonShare
import kotlinx.android.synthetic.main.activity_result.ivResult
import kotlinx.android.synthetic.main.activity_result2.*
import okhttp3.*
import retrofit2.Call
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
/* --- Django test --- */
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/* ------------------- */


class ResultActivity2 : AppCompatActivity() {
    /* --- Django test --- */
    internal lateinit var retrofit: Retrofit
    internal lateinit var comment: Call<ResponseBody>
    internal lateinit var result:String
    /* ------------------- */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result2)
        ivResult.setImageBitmap(curBitmap)

        buttonSave.setOnClickListener {
            curBitmap?.let { saveOnGallery(it) }
        }

        buttonMain.setOnClickListener {
            curBitmap = null
            curFile = null
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        buttonShare.setOnClickListener {
            Toast.makeText(this, "Need to implement.", Toast.LENGTH_SHORT).show()
            //ivResult.setImageURI(Uri.fromFile(File(filesDir, fileName)))
        }
        buttonDelete.setOnClickListener {
            Toast.makeText(this, "Image deleted", Toast.LENGTH_SHORT).show()
            File(curFile).delete()
            curBitmap = null
            curFile = null
            val intent = Intent(this, ResultsRecyclerActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
    override fun onBackPressed() {
        curBitmap = null
        curFile = null
        finish()
    }

    private fun saveOnGallery(bitmap: Bitmap) {
        val folderPath = Environment.getExternalStorageDirectory().absolutePath + "/Pictures/"
        val timestamp : String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fileName = "${timestamp}.jpeg"
        File(folderPath).let{
            if (!it.isDirectory) it.mkdirs()
        }
        val out = FileOutputStream(folderPath + fileName)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        Toast.makeText(this, "Saved the photo on gallery", Toast.LENGTH_SHORT).show()
    }
}


