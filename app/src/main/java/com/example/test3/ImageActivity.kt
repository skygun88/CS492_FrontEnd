package com.example.test3

import android.annotation.SuppressLint
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
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_image.*
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


class ImageActivity : AppCompatActivity() {
    /* --- Django test --- */
    internal lateinit var retrofit: Retrofit
    internal lateinit var comment: Call<ResponseBody>
    internal lateinit var result:String
    /* ------------------- */

    var curBitmap: Bitmap? = null
    var curFile: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        if (intent.hasExtra("tempFile")){
            val tempFilePath = intent.getStringExtra("tempFile")
            curFile = tempFilePath

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
        /* ----- Django Test ----- */
        /*
        retrofit = Retrofit.Builder().baseUrl(ApiService.API_URL).build()
        apiService = retrofit.create(ApiService::class.java)
        comment = apiService.get_Test("json")
        comment.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                try {
                    Log.d("DD_Test", response.body()!!.string())
                    result = response.body()!!.string()
                    Log.d("DD_Test2", result+"hi")
                    tvDj.text = result
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                result = "error!!"
                Log.e("D_Test", "페일!")
            }
        })
        */
        /* ---------------------- */

        buttonSave.setOnClickListener {
            curBitmap?.let { savePhoto(it) }
        }
        buttonReset.setOnClickListener {
            ivResult.setImageResource(R.drawable.test_image)
            curBitmap = null
        }

        buttonMain.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        buttonHttp.setOnClickListener {
            testRetrofit()
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

    private fun testRetrofit(){

        //creating a file
        val file = File(curFile!!)
        val fileName = "tempFile.png"


        var requestBody : RequestBody = RequestBody.create(MediaType.parse("image/*"),file)
        var body : MultipartBody.Part = MultipartBody.Part.createFormData("uploaded_file",fileName,requestBody)

        //The gson builder
        var gson : Gson =  GsonBuilder()
            .setLenient()
            .create()

        var okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()

        //creating retrofit object
        var retrofit = Retrofit.Builder()
            //.baseUrl("http://e4aa3c9680ac.ngrok.io") // need to change
            .baseUrl("http://e4aa3c9680ac.ngrok.io") // need to change
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()

        //creating our api

        var server = retrofit.create(retrofitInterface::class.java)
        Log.d("Bodytest", body.toString())
        // 파일, 사용자 아이디, 파일이름
        server.post_Porfile_Request("img", body).enqueue(object: Callback<UserData> {
            override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                if (response.isSuccessful) {
                    //Log.d("레트로핏 결과2",""+ ''response.body().toString())
                    Log.d("레트로핏 결과2",""+ "hi")
                    val fileContents = response.body()!!.img
                    val newBitmap = bitmapConverter.StringToBitmap(fileContents)
                    ivResult.setImageBitmap(newBitmap)
                    curBitmap = newBitmap
                    curFile = saveTempFile(curBitmap!!)


                } else {
                    Log.d("레트로핏 결과3",""+ response.body().toString())
                }
            }
            override fun onFailure(call: Call<UserData>, t: Throwable) {
                Log.d("레트로핏 결과1",t.message)
            }
        })
    }

}


