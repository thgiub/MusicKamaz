package ru.kamaz.music.ui

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.GridLayoutManager
import ru.kamaz.music.databinding.FragmentListBinding
import ru.kamaz.music.di.components.MusicComponent
import ru.kamaz.music.ui.category.CategoryFragment
import ru.kamaz.music.ui.producers.MusicFoldersViewHolder
import ru.kamaz.music.ui.producers.MusicListViewHolderProducer
import ru.kamaz.music.view_models.list.ListViewModel
import ru.sir.presentation.base.BaseApplication
import ru.sir.presentation.base.BaseFragment
import ru.sir.presentation.base.recycler_view.RecyclerViewAdapter


class FolderFragment:BaseFragment<ListViewModel, FragmentListBinding>(ListViewModel::class.java) {
    override fun inject(app: BaseApplication) {
        app.getComponent<MusicComponent>().inject(this)
    }

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    )= FragmentListBinding.inflate(inflater, container, false)

    override fun setListeners() {

        super.setListeners()
    }

    private fun recyclerViewAdapter() = RecyclerViewAdapter.Builder(this, viewModel.items)
        .addProducer(MusicFoldersViewHolder())
        .build { it }

    override fun initVars() {
        binding.folderWithMusicRv.adapter = recyclerViewAdapter()
    }
}