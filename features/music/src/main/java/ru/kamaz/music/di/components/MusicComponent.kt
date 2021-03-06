package ru.kamaz.music.di.components

import dagger.Subcomponent
import ru.kamaz.music.di.modules.CacheModule
import ru.kamaz.music.di.modules.DataModule
import ru.kamaz.music.di.modules.DomainModule
import ru.kamaz.music.di.modules.ViewModelModel
import ru.kamaz.music.ui.TrackFragment
import ru.kamaz.music.ui.MusicFragment
import ru.sir.presentation.base.BaseDaggerComponent
import javax.inject.Singleton

@Singleton
@Subcomponent(modules = [ViewModelModel::class, CacheModule::class,DataModule::class,DomainModule::class])
interface MusicComponent : BaseDaggerComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(cacheModule: CacheModule): MusicComponent
    }

    fun inject(fragment: MusicFragment)
    fun inject(fragment: TrackFragment)
}