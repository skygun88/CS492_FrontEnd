package com.example.test3

/* --- Django test --- */
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.kakao.kakaolink.v2.KakaoLinkResponse
import com.kakao.kakaolink.v2.KakaoLinkService
import com.kakao.message.template.ButtonObject
import com.kakao.message.template.ContentObject
import com.kakao.message.template.FeedTemplate
import com.kakao.message.template.LinkObject
import com.kakao.network.ErrorResult
import com.kakao.network.callback.ResponseCallback
import kotlinx.android.synthetic.main.activity_image.*

import kotlinx.android.synthetic.main.activity_result.buttonMain
import kotlinx.android.synthetic.main.activity_result.buttonSave
import kotlinx.android.synthetic.main.activity_result.buttonShare
import kotlinx.android.synthetic.main.activity_result.ivResult
import kotlinx.android.synthetic.main.activity_result2.*
import okhttp3.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.nio.file.attribute.AclEntry.newBuilder
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/* ------------------- */

var imgUrl: String? = null

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
            buttonShare.isEnabled = false
            Toast.makeText(this, "Share the image to Kakao Talk", Toast.LENGTH_SHORT).show()
            imgurRetrofit()
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

    private fun sendMessage(){
        val message = "보낼 내용"
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, message)
        intent.setPackage("com.kakao.talk")
        startActivity(intent)
    }

    private fun kakaoLink() {
        val params = FeedTemplate.newBuilder(
                ContentObject.newBuilder(
                    "ID picture",
                    imgUrl,
                    LinkObject.newBuilder()
                        .build()
                )
                .setImageHeight(800)
                .setImageWidth(507)
                .build()
            )
            .build()

        val serverCallbackArgs: MutableMap<String, String> = HashMap()
        serverCallbackArgs["user_id"] = "\${current_user_id}"
        serverCallbackArgs["product_id"] = "\${shared_product_id}"

        KakaoLinkService.getInstance().sendDefault(
            this,
            params,
            serverCallbackArgs,
            object : ResponseCallback<KakaoLinkResponse?>() {
                override fun onFailure(errorResult: ErrorResult) {
                    buttonShare.isEnabled = true
                    //Logger.e(errorResult.toString())
                }
                override fun onSuccess(result: KakaoLinkResponse?) { // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다. 전송 성공 유무는 서버콜백 기능을 이용하여야 한다.
                    buttonShare.isEnabled = true
                }
            }
        )
    }

    private fun imgurRetrofit(){
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
        var server = retrofit.create(imgurInterface::class.java)
        Log.d(TAG, body.toString())
        server.post_Porfile_Request(1, body).enqueue(object: Callback<UserData> {
            override fun onResponse(call: Call<UserData>, response: Response<UserData>) {
                if (response.isSuccessful) {
                    Log.d(TAG,response.body()!!.img)
                    val fileContents = response.body()!!.img
                    imgUrl = fileContents
                    kakaoLink()

                } else {
                    Log.d(TAG, response.body().toString())
                    buttonShare.isEnabled = true
                }
            }
            override fun onFailure(call: Call<UserData>, t: Throwable) {
                Log.d(TAG,"failed")
                Toast.makeText(applicationContext, "Upload failed", Toast.LENGTH_SHORT).show()
                buttonShare.isEnabled = true
            }
        })
    }

}



