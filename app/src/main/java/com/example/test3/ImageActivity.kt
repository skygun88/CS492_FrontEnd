package com.example.test3

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
const val baseUrl = "http://73545bc25381.ngrok.io"

class ImageActivity : AppCompatActivity() {
    /* --- Django test --- */
    internal lateinit var retrofit: Retrofit
    internal lateinit var comment: Call<ResponseBody>
    internal lateinit var result:String
    var templateIdx : Int = 0
    /* ------------------- */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)

        if (intent.hasExtra("index")) {
            templateIdx = intent.getIntExtra("index", 0)
            ivRequest.setImageBitmap(curBitmap)
        }

        buttonRequest.setOnClickListener {
            buttonRequest.isEnabled = false
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
            .readTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(90, TimeUnit.SECONDS)
            .build()

        //creating retrofit object
        var retrofit = Retrofit.Builder()
            .baseUrl(baseUrl) // need to change
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()

        //creating our api
        var server = retrofit.create(retrofitInterface::class.java)
        Log.d("Bodytest", body.toString())
        server.post_Porfile_Request(templateIdx, body).enqueue(object: Callback<UserData> {
            override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                if (response.isSuccessful) {
                    Log.d("Bodytest 2",""+ "hi")
                    val fileContents = response.body()!!.img
                    val newBitmap = bitmapConverter.StringToBitmap(fileContents)
                    curBitmap = newBitmap
                    curFile = saveTempFile(curBitmap!!)
                    Toast.makeText(applicationContext, "Conversion is successfully finished", Toast.LENGTH_SHORT).show()
                    buttonRequest.isEnabled = true

                    /* move to next activity */
                    val intent = Intent(applicationContext, ResultActivity::class.java)
                    startActivity(intent)
                } else {
                    Log.d("Bodytest 3", response.body().toString())
                    buttonRequest.isEnabled = true
                }
            }
            override fun onFailure(call: Call<UserData>, t: Throwable) {
                Log.d("Bodytest 4",t.message)
                Toast.makeText(applicationContext, "Conversion is failed, Need to try again later", Toast.LENGTH_SHORT).show()
                buttonRequest.isEnabled = true
            }
        })
    }

}


