package ru.kamaz.music.ui

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentTransaction
import ru.kamaz.music.databinding.FragmentListBinding
import ru.kamaz.music.di.components.MusicComponent
import ru.kamaz.music.ui.category.CategoryFragment
import ru.kamaz.music.view_models.list.ListViewModel
import ru.sir.presentation.base.BaseApplication
import ru.sir.presentation.base.BaseFragment


class ListFragment:BaseFragment<ListViewModel, FragmentListBinding>(ListViewModel::class.java) {
    override fun inject(app: BaseApplication) {
        app.getComponent<MusicComponent>().inject(this)
    }

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    )= FragmentListBinding.inflate(inflater, container, false)

    override fun setListeners() {
      /*  binding.sourceSelection.listMusic.setOnClickListener {

        }
        binding.sourceSelection.folderMusic.setOnClickListener {

        }
        binding.sourceSelection.categoryMusic.setOnClickListener {

        }*/
        super.setListeners()
    }

    override fun initVars() {
        super.initVars()
    }
}