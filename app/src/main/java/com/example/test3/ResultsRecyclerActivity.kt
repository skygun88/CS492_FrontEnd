package com.example.test3

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.android.synthetic.main.activity_template.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ResultsRecyclerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_template)

        File(Environment.getExternalStorageDirectory().absolutePath + "/ProofPic/").let {
            if (!it.isDirectory) it.mkdirs()
        }

        val imgList = ArrayList<String>()
        File("${Environment.getExternalStorageDirectory()}/ProofPic/").walkBottomUp().forEach {
            imgList.add(it.toString())
        }
        Log.d("array Test", imgList.toString())
        Toast.makeText(this, "There are ${imgList.size - 1} images", Toast.LENGTH_SHORT).show()

        val list = ArrayList<ImageItem>()
        for ((i, item) in imgList.withIndex()){
            if (i == imgList.size - 1) break
            list.add(ImageItem(item, i))
        }

        val adapter = ImgRecyclerAdapter(list) {param ->
            Log.d("RecycleTest", "${param.idx}")
            var idx: Int = param.idx
            Uri.fromFile(File(imgList[idx])).let {
                curBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, it)
                curFile = imgList[idx]
            }
            val intent = Intent(this, ResultActivity2::class.java)
            startActivity(intent)
        }
        recyclerView.adapter = adapter

        recyclerView.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}