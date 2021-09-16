package ru.kamaz.music.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.kamaz.music.R
import ru.kamaz.music.ui.music_сategory.MusicCategoryFragment


class CategoryRVAdapter(private val data:List<MusicCategoryFragment.DataModel>) : RecyclerView.Adapter<CategoryRVAdapter.CustomViewHolder>() {

    inner class CustomViewHolder(parent: View):RecyclerView.ViewHolder(parent){

        val img = parent.findViewById<ImageView>(R.id.custom_img)
        val txt = parent.findViewById<TextView>(R.id.custom_txt)
        val layout = parent.findViewById<LinearLayout>(R.id.custom_lin)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        return CustomViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.image_text_item,parent,false))
    }

    override fun getItemCount(): Int = data.count()

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {

        holder.img.setImageResource(data[position].category)
        holder.txt.text = data[position].name

        holder.layout.setOnClickListener {

        }
    }
}

