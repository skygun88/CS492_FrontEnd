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
        list.add(RecycleItem(getDrawable(R.drawable.t10_noface)!!, getString(R.string.title01)))
        list.add(RecycleItem(getDrawable(R.drawable.t11_noface)!!, getString(R.string.title02)))
        list.add(RecycleItem(getDrawable(R.drawable.t12_noface)!!,getString(R.string.title03)))
        list.add(RecycleItem(getDrawable(R.drawable.t13_noface)!!,getString(R.string.title04)))
        list.add(RecycleItem(getDrawable(R.drawable.t14_noface)!!, getString(R.string.title05)))
        list.add(RecycleItem(getDrawable(R.drawable.t20_noface)!!, getString(R.string.title06)))
        list.add(RecycleItem(getDrawable(R.drawable.t21_noface)!!, getString(R.string.title07)))
        list.add(RecycleItem(getDrawable(R.drawable.t22_noface)!!, getString(R.string.title08)))
        list.add(RecycleItem(getDrawable(R.drawable.t23_noface)!!, getString(R.string.title09)))
        list.add(RecycleItem(getDrawable(R.drawable.t24_noface)!!, getString(R.string.title10)))


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
                getString(R.string.title07) -> idx = 6
                getString(R.string.title08) -> idx = 7
                getString(R.string.title09) -> idx = 8
                getString(R.string.title10) -> idx = 9
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