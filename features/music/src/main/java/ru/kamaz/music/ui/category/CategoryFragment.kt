package ru.kamaz.music.ui.category

import DialogWithData

import android.os.Bundle
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import ru.kamaz.music.databinding.FragmentCategoryMusicBinding
import ru.kamaz.music.di.components.MusicComponent
import ru.kamaz.music.domain.GlobalConstants
import ru.kamaz.music.ui.enums.PlayListFlow
import ru.kamaz.music.ui.getTypedSerializable
import ru.kamaz.music.ui.producers.*
import ru.kamaz.music.view_models.music_category.CategoryViewModel
import ru.sir.presentation.base.BaseApplication
import ru.sir.presentation.base.BaseFragment
import ru.sir.presentation.base.recycler_view.RecyclerViewAdapter

class CategoryFragment :
    BaseFragment<CategoryViewModel, FragmentCategoryMusicBinding>(CategoryViewModel::class.java) {
    override fun inject(app: BaseApplication) {
        app.getComponent<MusicComponent>().inject(this)
    }

    private val main: PlayListFlow by lazy {
        arguments?.getTypedSerializable( GlobalConstants.MAIN) ?: PlayListFlow.MAIN_WINDOW
    }


    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentCategoryMusicBinding.inflate(inflater, container, false)

    override fun initVars() {
     /*   if(main == PlayListFlow.MAIN_WINDOW){
            binding.rvCategory.adapter = recyclerViewAdapterPlayList()
            Log.i("category1", "initVars: ")
        }
        if (main==PlayListFlow.SECOND_WINDOW){
            binding.rvCategory.adapter = recyclerViewAdapter2()
            Log.i("category2", "initVars: ")
        }*/

        binding.rvCategory.layoutManager = GridLayoutManager(context, 5)
        binding.rvCategory.adapter = recyclerViewAdapter2()
        Log.i("category2", "initVars: ")

    }

    fun clickListener(id: Int) {
        when (id) {
            0 -> binding.rvCategory.adapter = recyclerViewAdapterFavorite()
            1 -> {
                binding.rvCategory.layoutManager = GridLayoutManager(context, 4)
                binding.rvCategory.adapter = recyclerViewAdapterArtist()
            }
            2 -> {
                binding.rvCategory.adapter = recyclerViewAdapterGenres()
            }
            3 -> {
                binding.rvCategory.adapter = recyclerViewAdapterAlbums()
            }
            4 -> {
                binding.rvCategory.adapter = recyclerViewAdapterPlayList()
            }
        }
    }


    fun clickAddNewPlayList() {
        dialog()
    }

    private fun dialog(){
       DialogWithData().show(childFragmentManager, DialogWithData.TAG)
    }

    private fun  recyclerViewAdapter2() = RecyclerViewAdapter.Builder(this, viewModel.huitems)
        .addProducer(MusicCategoryViewHolder())
        .build { it }

    private fun recyclerViewAdapterFavorite() =
        RecyclerViewAdapter.Builder(this, viewModel.favorite)
            .addProducer(MusicFavoriteViewHolder())
            .build { it }

    private fun recyclerViewAdapterArtist() = RecyclerViewAdapter.Builder(this, viewModel.artist)
        .addProducer(MusicArtistViewHolder())
        .build { it }

    private fun recyclerViewAdapterPlayList() =
        RecyclerViewAdapter.Builder(this, viewModel.playlist)
            .addProducer(MusicPlayListAddNewHolder())
            .addProducer(MusicPlayListViewHolder())
            .build { it }

    private fun recyclerViewAdapterGenres() =
        RecyclerViewAdapter.Builder(this, viewModel.genres)
            .addProducer(MusicGenresViewHolder())
            .build { it }
    private fun recyclerViewAdapterAlbums() =
        RecyclerViewAdapter.Builder(this, viewModel.albums)
            .addProducer(MusicAlbumsViewHolder())
            .build { it }

}