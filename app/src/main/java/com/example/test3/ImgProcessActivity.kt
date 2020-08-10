package com.example.test3

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_img_process.*
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


class ImgProcessActivity : AppCompatActivity() {
    lateinit var curPhotoPath : String
    var curBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_img_process)

        if(intent.hasExtra("requestcode")){

            val intentIntVal = intent.getIntExtra("requestcode", 1)
            Log.d("intent_test", "hello -> $intentIntVal")
            when (intentIntVal){
                REQUEST_IMAGE_CAPTURE -> takeCapture()
                REQUEST_IMAGE_GALLERY -> getFromGallery()
            }

        }
        buttonCw.setOnClickListener {
            curBitmap = rotate(curBitmap!!, 90)
            ivProcess.setImageBitmap(curBitmap)

        }
        buttonCcw.setOnClickListener {
            curBitmap = rotate(curBitmap!!, -90)
            ivProcess.setImageBitmap(curBitmap)
        }

        buttonOk.setOnClickListener {
            val tempFileName = saveTempFile(curBitmap!!)
            val intent = Intent(this, ImageActivity::class.java)
            intent.putExtra("tempFile", tempFileName)
            startActivity(intent)
            finish()
        }
    }

    private fun saveTempFile(bitmap: Bitmap): String {
        val tempFileName : String = "temp_image_file"
        val storageDir : File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val result = File.createTempFile("JPEG_${tempFileName}_", ".jpg", storageDir).absolutePath
        val out = FileOutputStream(result)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
        return result

    }

    private fun getFromGallery() {
        val galIntent = Intent(Intent.ACTION_GET_CONTENT)
        galIntent.setType("image/*")
        startActivityForResult(Intent.createChooser(galIntent, "load image"), REQUEST_IMAGE_GALLERY)
    }

    private fun takeCapture() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile : File? = try {
                    createImageFile()
                }
                catch (ex: IOException){
                    null
                }
                photoFile?.also {
                    val photoUri: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.test3.fileprovider",
                        it
                    )

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)

                }


            }
        }
    }

    private fun createImageFile(): File {
        val timestamp : String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir : File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir)
            .apply { curPhotoPath = absolutePath}
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /* 1. Get image by camera */
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val file = File(curPhotoPath)

            if (Build.VERSION.SDK_INT < 28) {
                var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.fromFile(file))
                val newBitmap = resizeBitmap(bitmap)
                ivProcess.setImageBitmap(newBitmap)
                curBitmap = newBitmap
                Log.d("Bitmap size", "width: ${curBitmap!!.width}, height: ${curBitmap!!.height}")
            }
            else {
                val decode = ImageDecoder.createSource(
                    this.contentResolver,
                    Uri.fromFile(file)
                )
                var bitmap = ImageDecoder.decodeBitmap(decode)
                val newBitmap = resizeBitmap(bitmap)
                ivProcess.setImageBitmap(newBitmap)
                curBitmap = newBitmap
                Log.d("Bitmap size", "width: ${curBitmap!!.width}, height: ${curBitmap!!.height}")
            }
        }

        /* 2. Get image from gallery */
        else if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == Activity.RESULT_OK) {
            val curImageUri: Uri? = data?.data
            try {
                var bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, curImageUri)
                //imageView.setImageBitmap(bitmap)

                //val new_bitmap = rotate(bitmap, 90)
                val newBitmap = resizeBitmap(bitmap)
                ivProcess.setImageBitmap(newBitmap)
                curBitmap = newBitmap
                Log.d("Bitmap size", "width: ${curBitmap!!.width}, height: ${curBitmap!!.height}")
            } catch (e: Exception) {
                null
            }
        }
        else {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun rotate(bitmap: Bitmap, degree: Int) : Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val mtx = Matrix()
        mtx.setRotate(degree.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, mtx, true)
    }

    private fun resizeBitmap(bitmap: Bitmap): Bitmap {
        val curWidth = bitmap.width
        val curHeight = bitmap.height
        val maxSize = 1000
        var newWidth = maxSize
        var newheight = maxSize
        if (curWidth > maxSize || curHeight > maxSize){
            when (curWidth > curHeight){
                true -> newheight = curHeight * maxSize / curWidth
                false -> newWidth = curWidth * maxSize / curHeight
            }
            return Bitmap.createScaledBitmap(bitmap, newWidth, newheight, true)
        }
        return bitmap
    }
}

