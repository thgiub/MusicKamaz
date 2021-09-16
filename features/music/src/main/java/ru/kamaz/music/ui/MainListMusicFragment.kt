package ru.kamaz.music.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import ru.kamaz.music.databinding.FragmentMainListMusicBinding
import ru.kamaz.music.di.components.MusicComponent
import ru.kamaz.music.ui.producers.MusicListViewHolderProducer
import ru.kamaz.music.view_models.MainListMusicViewModel
import ru.kamaz.music_api.models.Track
import ru.sir.presentation.base.BaseApplication
import ru.sir.presentation.base.BaseFragment
import ru.sir.presentation.base.recycler_view.RecyclerViewAdapter


class MainListMusicFragment
    :BaseFragment<MainListMusicViewModel, FragmentMainListMusicBinding>(MainListMusicViewModel::class.java) {
    override fun inject(app: BaseApplication) {
        app.getComponent<MusicComponent>().inject(this)
    }

    private var holder = MusicCategoryEnum.LIST_ALL_MUSIC

    enum class MusicCategoryEnum(int: Int){
        LIST_ALL_MUSIC(0),
        FOLDER_WITH_MUSIC(1),
        CATEGORY_MUSIC(2)
    }
    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentMainListMusicBinding.inflate(inflater, container, false)

    override fun initVars() {
        updateRecyclerViewAdapter(holder)
        //binding.rvAllMusic.adapter = recyclerViewAdapter()
    }

    override fun setListeners() {
        binding.sourceSelection.listMusic.setOnClickListener {
            MusicCategoryEnum.LIST_ALL_MUSIC
        }
        binding.sourceSelection.folderMusic.setOnClickListener {
            MusicCategoryEnum.FOLDER_WITH_MUSIC
        }
        binding.sourceSelection.categoryMusic.setOnClickListener {
            MusicCategoryEnum.CATEGORY_MUSIC
        }
        super.setListeners()
    }

    fun onTrackClicked(track: Track) {
        viewModel.onItemClick(track)
    }

    fun updateRecyclerViewAdapter(holder:MusicCategoryEnum ){
        when(holder){
            MusicCategoryEnum.LIST_ALL_MUSIC-> startListAllMusic()
            MusicCategoryEnum.CATEGORY_MUSIC->startCategoryMusic()
            MusicCategoryEnum.FOLDER_WITH_MUSIC->startFolderWithMusic()
        }
    }

    private fun recyclerViewAdapter() = RecyclerViewAdapter.Builder(this, viewModel.items)
        .addProducer(MusicListViewHolderProducer())
        .build { it }

    fun startFolderWithMusic(){
        this.holder=MusicCategoryEnum.FOLDER_WITH_MUSIC
    }
    fun startListAllMusic(){
        this.holder=MusicCategoryEnum.LIST_ALL_MUSIC
        binding.rvAllMusic.adapter = recyclerViewAdapter()
    }

    fun startCategoryMusic(){
        this.holder=MusicCategoryEnum.CATEGORY_MUSIC
    }
}



