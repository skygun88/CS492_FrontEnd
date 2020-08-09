package com.example.test3

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity //base
import android.os.Bundle // base
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*


const val REQUEST_IMAGE_CAPTURE = 1
const val REQUEST_IMAGE_GALLERY = 2

class MainActivity : AppCompatActivity() {
    lateinit var curPhotoPath : String
    var curBitmap: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setPermission() // check initial permission

        buttonMain.setOnClickListener {
            //getFromGallery()
            goNextIntent(REQUEST_IMAGE_GALLERY)
        }

        buttonCamera.setOnClickListener {
            //takeCapture()
            goNextIntent(REQUEST_IMAGE_CAPTURE)
        }


    }

    private fun goNextIntent(type: Int){
        val intent = Intent(this, ImgProcessActivity::class.java)
        intent.putExtra("requestcode", type)
        startActivity(intent)
    }


    private fun getFromGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        startActivityForResult(Intent.createChooser(intent, "load image"), REQUEST_IMAGE_GALLERY)
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

    /* Ted permission setup */
    private fun setPermission() {
        val permission = object : PermissionListener{
            override fun onPermissionGranted() { // if permission okay -> execute
                Toast.makeText(this@MainActivity, "Permission is accpeted", Toast.LENGTH_SHORT).show()
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) { // if permission deny -> execute
                Toast.makeText(this@MainActivity, "Permission is denied", Toast.LENGTH_SHORT).show()
            }

        }

        TedPermission.with(this)
                .setPermissionListener(permission)
                .setRationaleMessage("Camera Permission Setup")
                .setDeniedMessage("Denied the permission.")
                .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA)
                .check()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        /* 1. Get image by camera */
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val file = File(curPhotoPath)

            if (Build.VERSION.SDK_INT < 28) {
                var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.fromFile(file))

                Log.d("테스트", "width : ${bitmap.width}, height: ${bitmap.height}")
                ivResult.setImageBitmap(bitmap)
                curBitmap = bitmap



            }
            else {
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

        /* 2. Get image from gallery */
        else if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == Activity.RESULT_OK){
            val curImageUri : Uri? = data?.data
            try {
                var bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, curImageUri)
                //imageView.setImageBitmap(bitmap)

                //val new_bitmap = rotate(bitmap, 90)
                ivResult.setImageBitmap(bitmap)
                curBitmap = bitmap


//                val nextIntent = Intent(this, ImgProcessActivity::class.java)
//                nextIntent.putExtra("img", curBitmap)
//                startActivity(nextIntent)

            }
            catch (e: Exception){
                null
            }
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

    private fun getName(uri: Uri): String {
        val projection = arrayOf(MediaStore.Images.ImageColumns.DISPLAY_NAME)
        val cursor = managedQuery(uri, projection, null, null, null)
        val column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

    private fun rotate(bitmap: Bitmap, degree: Int) : Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val mtx = Matrix()
        mtx.setRotate(degree.toFloat())

        return Bitmap.createBitmap(bitmap, 0, 0, width, height, mtx, true)

    }
}


