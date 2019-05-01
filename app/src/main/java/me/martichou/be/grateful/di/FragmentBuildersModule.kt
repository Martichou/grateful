package me.martichou.be.grateful.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.martichou.be.grateful.ui.add.AddMainFragment
import me.martichou.be.grateful.ui.details.ShowMainFragment
import me.martichou.be.grateful.ui.home.HomeMainFragment

@ExperimentalCoroutinesApi
@Suppress("unused")
@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeHomeMainFragment(): HomeMainFragment

    @ContributesAndroidInjector
    abstract fun contributeShowMainFragment(): ShowMainFragment

    @ContributesAndroidInjector
    abstract fun contributeAddMainFragment(): AddMainFragment
}