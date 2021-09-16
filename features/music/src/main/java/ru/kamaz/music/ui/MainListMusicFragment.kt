package ru.kamaz.music.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import ru.kamaz.music.R
import ru.kamaz.music.databinding.FragmentMainListMusicBinding
import ru.kamaz.music.di.components.MusicComponent
import ru.kamaz.music.ui.music_сategory.MusicCategoryFragment
import ru.kamaz.music.view_models.MainListMusicViewModel
import ru.sir.presentation.base.BaseApplication
import ru.sir.presentation.base.BaseFragment


class MainListMusicFragment
    :BaseFragment<MainListMusicViewModel, FragmentMainListMusicBinding>(MainListMusicViewModel::class.java) {
    override fun inject(app: BaseApplication) {
        app.getComponent<MusicComponent>().inject(this)
    }

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentMainListMusicBinding.inflate(inflater, container, false)

    override fun initVars() {
        super.initVars()
        binding.pager.adapter = FragmentAdapter(this)
        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
           // tab.text = "OBJECT ${(position + 1)}"
           when(position){
               0->tab.setIcon(R.drawable.ic_play)
               1->tab.setIcon(R.drawable.ic_next)
               2->tab.setIcon(R.drawable.ic_like_false)
           }
        }.attach()
    }


}

class FragmentAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {

        return when(position){
            0 -> MusicCategoryFragment()
            1 -> TrackFragment()
            2 -> TrackFragment()
            else -> TrackFragment()
        }
    }
}

