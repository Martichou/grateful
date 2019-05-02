package me.martichou.be.grateful.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import me.martichou.be.grateful.util.service.UploadPhotoService

@Suppress("unused")
@Module
abstract class ServiceBuilderModule {
    @ContributesAndroidInjector
    internal abstract fun contributeUploadPhotoService(): UploadPhotoService
}