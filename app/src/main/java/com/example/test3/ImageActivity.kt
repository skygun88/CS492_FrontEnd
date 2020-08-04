package com.example.test3

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
import kotlinx.android.synthetic.main.activity_image.*
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ImageActivity : AppCompatActivity() {
    var curBitmap: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        if (intent.hasExtra("tempFile")){
            val tempFilePath = intent.getStringExtra("tempFile")

            val file = File(tempFilePath)

            if (Build.VERSION.SDK_INT < 28) {
                var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.fromFile(file))

                Log.d("테스트", "width : ${bitmap.width}, height: ${bitmap.height}")
                ivResult.setImageBitmap(bitmap)
                curBitmap = bitmap


            } else {
                val decode = ImageDecoder.createSource(
                    this.contentResolver,
                    Uri.fromFile(file)
                )
                var bitmap = ImageDecoder.decodeBitmap(decode)
                Log.d("테스트", "width : ${bitmap.width}, height: ${bitmap.height}")
                ivResult.setImageBitmap(bitmap)
                curBitmap = bitmap

            }
        }


        buttonSave.setOnClickListener {
            curBitmap?.let { savePhoto(it) }
        }
        buttonReset.setOnClickListener {
            ivResult.setImageResource(R.drawable.test_image)
            curBitmap = null
        }
    }

    private fun savePhoto(bitmap: Bitmap) {
        val folderPath = Environment.getExternalStorageDirectory().absolutePath + "/Pictures/"
        val timestamp : String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val fileName = "${timestamp}.jpeg"
        val folder = File(folderPath)
        if (!folder.isDirectory) {
            folder.mkdirs()
        }

        val out = FileOutputStream(folderPath + fileName)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        Toast.makeText(this, "Saved the photo on gallery", Toast.LENGTH_SHORT).show()
    }

}