package com.condor.splashbox.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.condor.splashbox.data.photo.Photo
import om.condorserg.materialdesign.db.PhotosDao


@Database(
    entities = [
        UnsplashRemoteKeys::class,
        Photo::class
    ],
    version = UnsplashDatabase.DB_VERSION
)
abstract class UnsplashDatabase : RoomDatabase() {

    abstract fun photosDao(): PhotosDao
    abstract fun remoteKeysDao(): UnsplashRemoteKeyDao

    companion object {
        const val DB_VERSION = 1
        const val DB_NAME = "unsplash-database"
    }
}