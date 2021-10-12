package ru.kamaz.music.ui.category

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import ru.kamaz.music.databinding.FragmentCategoryMusicBinding
import ru.kamaz.music.di.components.MusicComponent
import ru.kamaz.music.ui.producers.MusicArtistViewHolder
import ru.kamaz.music.ui.producers.MusicCategoryViewHolder
import ru.kamaz.music.ui.producers.MusicFavoriteViewHolder
import ru.kamaz.music.ui.producers.MusicListViewHolderProducer
import ru.kamaz.music.view_models.music_category.CategoryViewModel
import ru.sir.core.None
import ru.sir.presentation.base.BaseApplication
import ru.sir.presentation.base.BaseFragment
import ru.sir.presentation.base.recycler_view.RecyclerViewAdapter
import ru.sir.presentation.navigation.UiAction

class CategoryFragment : BaseFragment<CategoryViewModel, FragmentCategoryMusicBinding>(CategoryViewModel::class.java) {
    override fun inject(app: BaseApplication) {
        app.getComponent<MusicComponent>().inject(this)
    }


    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    )= FragmentCategoryMusicBinding.inflate(inflater,container,false)

    override fun initVars() {
        binding.rvCategory.adapter = recyclerViewAdapter2()
    }

    fun clickListener(id: Int){
        when(id){
            0 -> binding.rvCategory.adapter=recyclerViewAdapterFavorite()
            1 ->{
                binding.rvCategory.layoutManager = GridLayoutManager(context, 4)
                binding.rvCategory.adapter = recyclerViewAdapterArtist()
            }
        }
    }

    private fun recyclerViewAdapter2() = RecyclerViewAdapter.Builder(this, viewModel.huitems)
        .addProducer(MusicCategoryViewHolder())
        .build { it }

    private fun recyclerViewAdapterFavorite() = RecyclerViewAdapter.Builder(this, viewModel.favorite)
        .addProducer(MusicFavoriteViewHolder())
        .build { it }

    private fun recyclerViewAdapterArtist() = RecyclerViewAdapter.Builder(this, viewModel.artist)
        .addProducer(MusicArtistViewHolder())
        .build { it }

}