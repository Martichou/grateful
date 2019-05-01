package me.martichou.be.grateful.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import me.martichou.be.grateful.db.AppDatabase
import me.martichou.be.grateful.db.NotesDao
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {

    @Singleton
    @Provides
    fun provideDb(app: Application): AppDatabase {
        return Room
            .databaseBuilder(app, AppDatabase::class.java, "notes")
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .build()
    }

    @Singleton
    @Provides
    fun provideNotesDao(db: AppDatabase): NotesDao {
        return db.notesDao()
    }
}