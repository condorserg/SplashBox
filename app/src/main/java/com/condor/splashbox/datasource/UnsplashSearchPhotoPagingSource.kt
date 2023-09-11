package com.condor.splashbox.datasource

import android.util.Log
import androidx.paging.LoadState
import androidx.paging.LoadType
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.condor.splashbox.authorization.AuthConfig
import com.condor.splashbox.data.photo.Photo
import com.condor.splashbox.network.UnsplashApi
import retrofit2.HttpException
import java.io.IOException

class UnsplashSearchPhotoPagingSource(
    private val query: String,
    private val unsplashApi: UnsplashApi
) : PagingSource<Int, Photo>() {

    private val initialPage = 1

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {

        val position = params.key ?: initialPage

        return try {
            val response = unsplashApi.searchPhotos(query, position, params.loadSize, AuthConfig.CLIENT_ID)
            if (response.code() == 403) {
                throw (HttpException(response))
            }
            val photos: List<Photo>? = response.body()?.results

            LoadResult.Page(
                data = photos ?: emptyList(),
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (photos?.isEmpty() == true) null else position + 1
            )
        } catch (e: IOException) {
            Log.e("SearchPhotoPagingSource", "IOException = ${e.message}", e)
            LoadResult.Error(e)
        } catch (e: HttpException) {
            Log.e("SearchPhotoPagingSource", "HttpException = ${e.message}", e)
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

}