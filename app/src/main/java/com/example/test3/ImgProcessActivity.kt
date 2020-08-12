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
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_image.*
import kotlinx.android.synthetic.main.activity_img_process.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

var curBitmap: Bitmap? = null
var curFile: String? = null

class ImgProcessActivity : AppCompatActivity() {
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

        buttonOk.setOnClickListener {
            curFile = saveTempFile(curBitmap!!)
            val intent = Intent(this, TemplateActivity::class.java)
            startActivity(intent)
            //finish()
        }
    }
    override fun onBackPressed() {
        finish()
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
            .apply { curFile = absolutePath}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /* 1. Get image by camera */
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val file = File(curFile)
            launchImageCrop(Uri.fromFile(file))
        }
        /* 2. Get image from gallery */
        else if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                launchImageCrop(uri)
            }
        }
        /* 3. Crop the image */
        else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                var bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, result.uri)
                val newBitmap = resizeBitmap(bitmap)
                curBitmap = newBitmap
                setImage(result.uri)
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.e("CropTest", "Crop error: ${result.getError()}" )
            }
        }
        else {
            finish()
        }
    }

    private fun setImage(uri: Uri){
        Glide.with(this)
            .load(uri)
            .into(ivProcess)
    }

    private fun launchImageCrop(uri: Uri){
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1000, 1580)
            .setCropShape(CropImageView.CropShape.RECTANGLE) // default is rectangle
            .start(this)
    }

//    private fun rotate(bitmap: Bitmap, degree: Int) : Bitmap {
//        val width = bitmap.width
//        val height = bitmap.height
//        val mtx = Matrix()
//        mtx.setRotate(degree.toFloat())
//        return Bitmap.createBitmap(bitmap, 0, 0, width, height, mtx, true)
//    }

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

