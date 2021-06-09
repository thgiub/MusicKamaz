package ru.kamaz.music.di.components

import dagger.Subcomponent
import ru.kamaz.music.di.modules.*
import ru.kamaz.music.services.MusicService
import ru.kamaz.music.ui.TrackFragment
import ru.kamaz.music.ui.MusicFragment
import ru.kamaz.music.ui.bt.BtFragment
import ru.sir.presentation.base.BaseDaggerComponent
import javax.inject.Singleton

@Singleton
@Subcomponent(modules = [ViewModelModel::class, CacheModule::class,DataModule::class,DomainModule::class,RoomModule::class])
interface MusicComponent : BaseDaggerComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(cacheModule: CacheModule): MusicComponent
    }

    fun inject(fragment: MusicFragment)
    fun inject(fragment: TrackFragment)
    fun inject(service: MusicService)
    fun inject(fragment: BtFragment)


}