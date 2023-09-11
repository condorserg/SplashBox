package com.condor.splashbox.di

import android.app.Application
import androidx.room.Room
import com.condor.splashbox.db.UnsplashDatabase
import com.condor.splashbox.db.UnsplashRemoteKeyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import om.condorserg.materialdesign.db.PhotosDao

@Module
@InstallIn(ViewModelComponent::class)
class DatabaseModule {

    @Provides
    fun providesDatabase(application: Application): UnsplashDatabase {
        return Room.databaseBuilder(
            application,
            UnsplashDatabase::class.java,
            UnsplashDatabase.DB_NAME
        )
            .build()
    }

    @Provides
    fun providesPhotosDao(db: UnsplashDatabase): PhotosDao {
        return db.photosDao()
    }

    @Provides
    fun providesRemoteKeysDao(db: UnsplashDatabase): UnsplashRemoteKeyDao {
        return db.remoteKeysDao()
    }
}