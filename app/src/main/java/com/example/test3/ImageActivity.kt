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
const val baseUrl = "http://d43cc272cd6c.ngrok.io"

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

//            val file = File(curFile)
//
//            if (Build.VERSION.SDK_INT < 28) {
//                var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, Uri.fromFile(file))
//
//                Log.d("테스트", "width : ${bitmap.width}, height: ${bitmap.height}")
//                ivResult.setImageBitmap(bitmap)
//                curBitmap = bitmap
//
//            } else {
//                val decode = ImageDecoder.createSource(
//                    this.contentResolver,
//                    Uri.fromFile(file)
//                )
//                var bitmap = ImageDecoder.decodeBitmap(decode)
//                Log.d("테스트", "width : ${bitmap.width}, height: ${bitmap.height}")
//                ivResult.setImageBitmap(bitmap)
//                curBitmap = bitmap
//
//            }
            ivRequest.setImageBitmap(curBitmap)
        }

        buttonRequest.setOnClickListener {
            testRetrofit()
//            if (success == true) {
//                Toast.makeText(this, "Conversion is successfully finished", Toast.LENGTH_SHORT).show()
//                val intent = Intent(this, ResultActivity::class.java)
//                startActivity(intent)
//            }
//            else {
//                Toast.makeText(this, "Conversion is failed, Need to try again later", Toast.LENGTH_SHORT).show()
//            }
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

        Log.d("Bodytest", "1")
        //The gson builder
        var gson : Gson =  GsonBuilder()
            .setLenient()
            .create()

        var okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
        Log.d("Bodytest", "2")
        //creating retrofit object
        var retrofit = Retrofit.Builder()
            //.baseUrl("http://e4aa3c9680ac.ngrok.io") // need to change
            .baseUrl(baseUrl) // need to change
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
        Log.d("Bodytest", "3")
        //creating our api

        var server = retrofit.create(retrofitInterface::class.java)
        Log.d("Bodytest", body.toString())
        // 파일, 사용자 아이디, 파일이름
        server.post_Porfile_Request(templateIdx, body).enqueue(object: Callback<UserData> {
            override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                if (response.isSuccessful) {
                    //Log.d("레트로핏 결과2",""+ ''response.body().toString())
                    Log.d("Bodytest 2",""+ "hi")
                    val fileContents = response.body()!!.img
                    val newBitmap = bitmapConverter.StringToBitmap(fileContents)
                    //ivRequest.setImageBitmap(newBitmap)
                    curBitmap = newBitmap
                    curFile = saveTempFile(curBitmap!!)
                    Toast.makeText(applicationContext, "Conversion is successfully finished", Toast.LENGTH_SHORT).show()
                    val intent = Intent(applicationContext, ResultActivity::class.java)
                    startActivity(intent)
                } else {
                    Log.d("Bodytest 3",""+ response.body().toString())
                }
            }
            override fun onFailure(call: Call<UserData>, t: Throwable) {
                Log.d("Bodytest 4",t.message)
                Toast.makeText(applicationContext, "Conversion is failed, Need to try again later", Toast.LENGTH_SHORT).show()

            }
        })
    }

}


