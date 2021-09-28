package ru.kamaz.music.ui.adapters

class AllMusicRvAdapter{/*(private val item: List<MusicCategoryFragment.DataModel>) :
    RecyclerView.Adapter<AllMusicRvAdapter.MyViewHolder>() {


    lateinit var binding2: ImageTextItemBinding



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        binding2 = ImageTextItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding2)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val element = item[position]
        when(holder){

            else -> holder.bind(element as MusicCategoryFragment.DataModel)
        }


    }
    override fun getItemCount() = item.size

    class MyViewHolder(private val binding2:ImageTextItemBinding ) :
        RecyclerView.ViewHolder(binding2.root), View.OnClickListener {

        fun bind(item:MusicCategoryFragment.DataModel) {

            binding2.musicCategoryText.text = item.name
            //binding2.musicName.text=item.title
            //binding2.artistName.text= item.artist
            //binding2.duration.text= item.duration
        }

        override fun onClick(v: View?) {
            when (v) {
                itemView -> {

                }

            }
        }
    }*/
}