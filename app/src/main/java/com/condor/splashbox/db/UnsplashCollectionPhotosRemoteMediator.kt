package com.condor.splashbox.db

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.condor.splashbox.authorization.AuthConfig
import com.condor.splashbox.data.photo.Photo
import com.condor.splashbox.network.UnsplashApi


@OptIn(ExperimentalPagingApi::class)
class UnsplashCollectionPhotosRemoteMediator(
    private val collectionId: String,
    private val service: UnsplashApi,
    private val unsplashDatabase: UnsplashDatabase
) : RemoteMediator<Int, Photo>() {

    private var pageIndex = 1

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Photo>): MediatorResult {

        pageIndex =
            getPageIndex(loadType) ?: return MediatorResult.Success(endOfPaginationReached = true)

        val limit = state.config.pageSize
        Log.d("RemoteMediator", "limit = $limit")

        return try {
            val response =
                service.getCollectionPhotos(collectionId, pageIndex, limit)
            val photos = response.body()

            unsplashDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    if (photos != null) {
                        unsplashDatabase.photosDao().refresh(photos)
                    }
                } else {
                    if (photos != null) {
                        unsplashDatabase.photosDao().insertAllPhotos(photos)
                    }
                }
                MediatorResult.Success(
                    endOfPaginationReached = photos!!.size < limit
                )
            }

        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    private fun getPageIndex(loadType: LoadType): Int? {
        pageIndex = when (loadType) {
            LoadType.REFRESH -> {
                Log.d("RemoteMediator", "REFRESH, PageIndex = $pageIndex")
                1
            }
            LoadType.PREPEND -> {
                Log.d("RemoteMediator", "PREPEND, PageIndex = $pageIndex")
                return null
            }
            LoadType.APPEND -> {
                Log.d("RemoteMediator", "APPEND, PageIndex = $pageIndex")
                ++pageIndex
            }
        }
        return pageIndex
    }
}