package com.condor.splashbox.data.user

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.condor.splashbox.data.photo.Photo
import com.condor.splashbox.datasource.UnsplashUserLikedPhotosPagingSource
import com.condor.splashbox.db.UnsplashDatabase
import com.condor.splashbox.network.UnsplashApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

const val ITEMS_PER_PAGE = 20

class UserRepository @Inject constructor(
    val db: UnsplashDatabase,
    private val unsplashApi: UnsplashApi
) {
    fun getCurrentUser(): Flow<CurrentUser> {
        return flow {
            emit(unsplashApi.getCurrentUser())
        }.map { response ->
            response.body() ?: throw IOException()
        }.catch { e ->
            Log.e("UserRepository", "GetCurrentUserError: ${e.message}")
        }.flowOn(Dispatchers.IO)
    }

    fun getUserLikedPhotos(userName: String): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                enablePlaceholders = false,
            )
        ) {
            UnsplashUserLikedPhotosPagingSource(userName, unsplashApi)
        }.flow.flowOn(Dispatchers.IO)
    }
}