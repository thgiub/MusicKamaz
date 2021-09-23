package ru.kamaz.music.view_models

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.MutableStateFlow
import ru.kamaz.music.databinding.ImageTextItemBinding
import ru.kamaz.music_api.models.CategoryMusicModel
import ru.sir.presentation.base.recycler_view.RecyclerViewBaseItem
import ru.sir.presentation.extensions.launchWhenStarted

class MusicCategoryViewModel:RecyclerViewBaseItem<CategoryMusicModel, ImageTextItemBinding>() {
    private val img = MutableStateFlow(0)
    private val category = MutableStateFlow("")
    private lateinit var data: CategoryMusicModel
    override fun initVars() {
        img.launchWhenStarted(parent.lifecycleScope){
            binding.customImg.setImageResource(it)
        }
        category.launchWhenStarted(parent.lifecycleScope){
            binding.customTxt.text = it
        }
    }

    override fun bindData(data: CategoryMusicModel) {
        this.data = data
        img.value= data.img
        category.value= data.category
    }
}