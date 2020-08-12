package com.example.test3

import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*
import java.io.File


class ImgRecyclerAdapter(
    private val items: ArrayList<ImageItem>,
    val param: (ImageItem) -> Unit
) :
    RecyclerView.Adapter<ImgRecyclerAdapter.ViewHolder>() {

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ImgRecyclerAdapter.ViewHolder, position: Int) {
        val item = items[position]
        val listener = View.OnClickListener {
            param(item)
        }
        holder.apply {
            bind(listener, item)
            itemView.tag = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImgRecyclerAdapter.ViewHolder {
        val inflatedView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ImgRecyclerAdapter.ViewHolder(inflatedView, param)
    }

    class ViewHolder(v: View, param: (ImageItem) -> Unit) : RecyclerView.ViewHolder(v) {
        private var view: View = v
        fun bind(listener: View.OnClickListener, item: ImageItem) {
            val filename = item.path.split("/").let { it[it.size - 1] }
            val creation = filename.split(".")[0]
            creation.split("_").let{
                val day = it[0]
                val time = it[1]
                val year = day.substring(0,4)
                val month = day.substring(4, 6)
                val date = day.substring(6, 8)
                val hour = time.substring(0,2)
                val min = time.substring(2,4)
                val sec = time.substring(4,6)
                view.title.text = "[${item.idx + 1}] $year.$month.$date $hour:$min:$sec"
            }
            view.thumbnail.setImageURI(Uri.fromFile(File(item.path)))
            view.setOnClickListener(listener)
        }
    }
}