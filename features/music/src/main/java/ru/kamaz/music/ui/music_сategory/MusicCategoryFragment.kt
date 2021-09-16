package ru.kamaz.music.ui.music_сategory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import ru.kamaz.music.R
import ru.kamaz.music.databinding.FragmentCategoryMusicBinding
import ru.kamaz.music.di.components.MusicComponent
import ru.kamaz.music.ui.adapters.AllMusicRvAdapter
import ru.kamaz.music.ui.adapters.CategoryRVAdapter


import ru.kamaz.music.view_models.music_category.MusicCategoryViewModel
import ru.sir.presentation.base.BaseApplication
import ru.sir.presentation.base.BaseFragment



class MusicCategoryFragment: BaseFragment<MusicCategoryViewModel, FragmentCategoryMusicBinding>(MusicCategoryViewModel::class.java) {
    override fun inject(app: BaseApplication) {
        app.getComponent<MusicComponent>().inject(this)
    }

    data class DataModel(
        val category: Int,
        val name:String
    )

    val category = listOf(
        DataModel(R.drawable.ic_like_false,"Избранное"),
        DataModel(R.drawable.ic_like_false,"Исполнители"),
        DataModel(R.drawable.ic_like_false,"Жанры"),
        DataModel(R.drawable.ic_like_false,"Альбомы"),
        DataModel(R.drawable.ic_like_false,"Плейлисты")
    )


    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): FragmentCategoryMusicBinding=
        FragmentCategoryMusicBinding.inflate(inflater,container,false)

    override fun initVars() {
        binding.rvCategoryMusic.layoutManager = LinearLayoutManager(context)
        binding.rvCategoryMusic.adapter = CategoryRVAdapter(category)
    }

}