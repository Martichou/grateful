package me.martichou.be.grateful.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import kotlinx.coroutines.ExperimentalCoroutinesApi
import me.martichou.be.grateful.ui.home.HomeMainFragment

@ExperimentalCoroutinesApi
@Suppress("unused")
@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeHomeMainFragment(): HomeMainFragment
}