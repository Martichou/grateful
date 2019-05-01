package me.martichou.be.grateful.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import me.martichou.be.grateful.ui.add.AddViewModel
import me.martichou.be.grateful.ui.details.ShowViewModel
import me.martichou.be.grateful.ui.edit.EditViewModel

import me.martichou.be.grateful.ui.home.MainViewModel
import me.martichou.be.grateful.viewmodel.GratefulViewModelFactory

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(mainViewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ShowViewModel::class)
    abstract fun bindShowViewModel(showViewModel: ShowViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(EditViewModel::class)
    abstract fun bindEditViewModel(editViewModel: EditViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(AddViewModel::class)
    abstract fun bindAddViewModel(addViewModel: AddViewModel): ViewModel

    @Binds
    abstract fun bindGratefulViewModelFactory(factory: GratefulViewModelFactory): ViewModelProvider.Factory
}