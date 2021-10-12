package ru.kamaz.music.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import ru.kamaz.music.R
import ru.kamaz.music.databinding.FragmentMainListMusicBinding
import ru.kamaz.music.di.components.MusicComponent
import ru.kamaz.music.ui.all_musiclist.TrackFragment
import ru.kamaz.music.ui.category.CategoryFragment
import ru.kamaz.music.view_models.MainListMusicViewModel
import ru.sir.presentation.base.BaseApplication
import ru.sir.presentation.base.BaseFragment
import ru.sir.presentation.extensions.launchWhenStarted


class MainListMusicFragment
    :BaseFragment<MainListMusicViewModel, FragmentMainListMusicBinding>(MainListMusicViewModel::class.java) {
    override fun inject(app: BaseApplication) {
        app.getComponent<MusicComponent>().inject(this)
    }

   // private var holder = MusicCategoryEnum.LIST_ALL_MUSIC

 /*   enum class MusicCategoryEnum( val category: Int){
        LIST_ALL_MUSIC(0),
        FOLDER_WITH_MUSIC(1),
        CATEGORY_MUSIC(2),
        FAVORITE_MUSIC(3)
    }*/
    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentMainListMusicBinding.inflate(inflater, container, false)

    override fun initVars() {
        startListAllMusic()
        viewModel.howRvModeNow.launchWhenStarted(lifecycleScope){
            Log.i("item", "$it click")
           // rvChange(it)
        }
    }

    override fun setListeners() {
        binding.sourceSelection.listMusic.setOnClickListener {
           startListAllMusic()
        }
        binding.sourceSelection.folderMusic.setOnClickListener {
            startFolderListFragment()
        }
        binding.sourceSelection.categoryMusic.setOnClickListener {
           startCategoryMusic()
        }
        super.setListeners()
    }

    private fun startCategoryMusic(){
        parentFragmentManager.beginTransaction().replace(R.id.fragment, CategoryFragment()).commit()
        //parentFragmentManager.beginTransaction().add(R.id.fragment, CategoryFragment()).commit()
    }

    private fun  startListAllMusic(){
        parentFragmentManager.beginTransaction().replace(R.id.fragment, TrackFragment()).commit()
        binding.sourceSelection.listMusic.background
    }

    private fun startFolderListFragment(){
        parentFragmentManager.beginTransaction().replace(R.id.fragment, FolderFragment()).commit()
    }

/*    fun onTrackClicked(track: Track) {
        viewModel.onItemClick(track)
    }

    fun updateRecyclerViewAdapter(holder:MusicCategoryEnum ){
        Log.i("stt", "updateRecyclerViewAdapter: ")
        when(holder){
            MusicCategoryEnum.LIST_ALL_MUSIC-> startListAllMusic()
            MusicCategoryEnum.CATEGORY_MUSIC->startCategoryMusic()
            MusicCategoryEnum.FOLDER_WITH_MUSIC->startFolderWithMusic()
        }
    }

    fun rvChange(rv: Int){
        when(rv){
            4-> startFavoriteMusic()
        }

    }

    private fun recyclerViewAdapter() = RecyclerViewAdapter.Builder(this, viewModel.items)
        .addProducer(MusicListViewHolderProducer())
        .build { it }

    private fun recyclerViewAdapter2() = RecyclerViewAdapter.Builder(this, viewModel.huitems)
        .addProducer(MusicCategoryViewHolder())
        .build { it }

    private fun recyclerViewFavoriteMusicAdapter() = RecyclerViewAdapter.Builder(this, viewModel.favorite)
        .addProducer(MusicFavoriteViewHolder())
        .build { it }

    fun startFolderWithMusic(){
        Log.i("startFolderWithMusic", "startFolderWithMusic")
        this.holder=MusicCategoryEnum.FOLDER_WITH_MUSIC
    }
    fun startListAllMusic(){
        Log.i("startFolderWithMusic", "startFolderWithMusic")
        this.holder=MusicCategoryEnum.LIST_ALL_MUSIC
        binding.rvAllMusic.adapter = recyclerViewAdapter()
    }

    fun startCategoryMusic(){
        Log.i("startCategoryMusic", "startCategoryMusic")
        this.holder=MusicCategoryEnum.CATEGORY_MUSIC
        binding.rvAllMusic.adapter = recyclerViewAdapter2()
    }

    fun startFavoriteMusic(){
        Log.i("item", " click")
        this.holder=MusicCategoryEnum.FAVORITE_MUSIC
        binding.rvAllMusic.adapter = recyclerViewFavoriteMusicAdapter()
    }*/
}



