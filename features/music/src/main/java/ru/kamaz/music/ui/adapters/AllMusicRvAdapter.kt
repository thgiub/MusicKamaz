package ru.kamaz.music.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.kamaz.itis.phoneapp.ui.pojo.RecyclerViewItem
import ru.kamaz.music.databinding.TestTextItemBinding
import ru.kamaz.music_api.models.Track

class AllMusicRvAdapter(private val item: List<RecyclerViewItem>) :
    RecyclerView.Adapter<AllMusicRvAdapter.MyViewHolder>() {


    lateinit var binding2: TestTextItemBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        binding2 = TestTextItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding2)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val element = item[position]
        when(holder){
            is MyViewHolder-> holder.bind(element as Track)

        }


    }
    override fun getItemCount() = item.size

    class MyViewHolder(private val binding2: TestTextItemBinding) :
        RecyclerView.ViewHolder(binding2.root), View.OnClickListener {

        fun bind(item:Track) {

            binding2.musicName.text=item.title
            binding2.artistName.text= item.artist
            //binding2.duration.text= item.duration
        }

        override fun onClick(v: View?) {
            when (v) {
                itemView -> {

                }

            }
        }
    }

}