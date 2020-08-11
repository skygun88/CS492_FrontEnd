package com.example.test3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import kotlinx.android.synthetic.main.activity_template.*

class TemplateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_template)

        val list = ArrayList<RecycleItem>()
        list.add(RecycleItem(getDrawable(R.drawable.sample1)!!, getString(R.string.title01)))
        list.add(RecycleItem(getDrawable(R.drawable.sample2)!!, getString(R.string.title02)))
        list.add(RecycleItem(getDrawable(R.drawable.sample3)!!, getString(R.string.title03)))
        list.add(RecycleItem(getDrawable(R.drawable.sample4)!!, getString(R.string.title04)))
        list.add(RecycleItem(getDrawable(R.drawable.sample5)!!, getString(R.string.title05)))
        list.add(RecycleItem(getDrawable(R.drawable.sample6)!!, getString(R.string.title06)))


        val adapter = RecyclerAdapter(list) {param ->
            Log.d("RecycleTest", param.title)
            var idx: Int = 0
            when (param.title){
                getString(R.string.title01) -> idx = 0
                getString(R.string.title02) -> idx = 1
                getString(R.string.title03) -> idx = 2
                getString(R.string.title04) -> idx = 3
                getString(R.string.title05) -> idx = 4
                getString(R.string.title06) -> idx = 5
            }
            val intent = Intent(this, ImageActivity::class.java)
            intent.putExtra("index", idx)
            startActivity(intent)
            //finish()
        }
        recyclerView.adapter = adapter

        recyclerView.addItemDecoration(
            DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        )
    }
}