package ru.kamaz.music.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.kamaz.music.R

class PagerAdapter (private val context: Context, private val words: List<String>): RecyclerView.Adapter<PagerAdapter.PageHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerAdapter.PageHolder {
        val ppp = PageHolder(LayoutInflater.from(context).inflate(R.layout.tap_fragment, parent, false))
        return ppp
    }

    override fun onBindViewHolder(holder: PagerAdapter.PageHolder, position: Int) {
        holder.textView.text=words[position]
    }

    override fun getItemCount(): Int = words.size

    inner class PageHolder(view: View): RecyclerView.ViewHolder(view){
        val textView: TextView =view.findViewById(R.id.textView)
    }
}


