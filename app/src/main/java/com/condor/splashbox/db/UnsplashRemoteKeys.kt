package com.condor.splashbox.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = PhotosContract.REMOTE_KEYS_TABLE_NAME)
data class UnsplashRemoteKeys(
    @PrimaryKey
    val photoId: String,
    val prevKey: Int?,
    val nextKey: Int?
)