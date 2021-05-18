package ru.kamaz.music.di.modules

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import ru.kamaz.music.view_models.TrackViewModel
import ru.kamaz.music.view_models.MusicFragmentViewModel
import ru.kamaz.music.view_models.bt.BtFragmentViewModel
import ru.sir.presentation.annotations.ViewModelKey
import ru.sir.presentation.factories.ViewModelFactory

@Module
abstract class ViewModelModel() {
    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory

    @Binds
    @IntoMap
    @ViewModelKey(MusicFragmentViewModel::class)
    abstract fun bindMusicViewModel(model: MusicFragmentViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(TrackViewModel::class)
    abstract fun bindListMusicViewModel(model: TrackViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(BtFragmentViewModel::class)
    abstract fun bindBtViewModel(model: BtFragmentViewModel): ViewModel
}