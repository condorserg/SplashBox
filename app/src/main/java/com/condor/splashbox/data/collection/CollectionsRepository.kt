package com.condor.splashbox.data.collection

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.condor.splashbox.data.photo.Photo
import com.condor.splashbox.datasource.UnsplashCollectionPhotosPagingSource
import com.condor.splashbox.datasource.UnsplashCollectionsPagingSource
import com.condor.splashbox.db.UnsplashDatabase
import com.condor.splashbox.network.UnsplashApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


const val ITEMS_PER_PAGE = 20

class CollectionsRepository @Inject constructor(
    val db: UnsplashDatabase,
    private val unsplashApi: UnsplashApi
) {

    fun getCollections(): Flow<PagingData<Collection>> {
        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                enablePlaceholders = false,
            )
        ) {
            UnsplashCollectionsPagingSource(unsplashApi)
        }.flow.flowOn(Dispatchers.IO)
    }

    fun getCollectionPhotos(collectionId: String): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                enablePlaceholders = false,
            )
        ) {
            UnsplashCollectionPhotosPagingSource(collectionId, unsplashApi)
        }.flow.flowOn(Dispatchers.IO)
    }

}