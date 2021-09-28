package ru.kamaz.music.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import ru.kamaz.music.databinding.FragmentCategoryMusicBinding
import ru.kamaz.music.di.components.MusicComponent
import ru.kamaz.music.view_models.music_category.CategoryViewModel
import ru.sir.presentation.base.BaseApplication
import ru.sir.presentation.base.BaseFragment

class CategoryFragment : BaseFragment<CategoryViewModel, FragmentCategoryMusicBinding>(CategoryViewModel::class.java) {
    override fun inject(app: BaseApplication) {
        app.getComponent<MusicComponent>().inject(this)
    }

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    )= FragmentCategoryMusicBinding.inflate(inflater,container,false)
}