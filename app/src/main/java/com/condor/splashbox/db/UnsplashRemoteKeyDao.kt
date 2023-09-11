package com.condor.splashbox.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UnsplashRemoteKeyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllRemoteKeys(remoteKeys: List<UnsplashRemoteKeys>)

    @Query("Select * From ${PhotosContract.REMOTE_KEYS_TABLE_NAME} Where photoId = :id")
    suspend fun getRemoteKeyByPhotoID(id: String): UnsplashRemoteKeys?

    @Query("DELETE FROM ${PhotosContract.REMOTE_KEYS_TABLE_NAME}")
    suspend fun clearRemoteKeys()

}