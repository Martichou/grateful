package me.martichou.be.grateful.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

import me.martichou.be.grateful.ui.home.MainViewModel
import me.martichou.be.grateful.viewmodel.GratefulViewModelFactory

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    abstract fun bindGratefulViewModelFactory(factory: GratefulViewModelFactory): ViewModelProvider.Factory
}