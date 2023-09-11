package com.condor.splashbox.datasource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.condor.splashbox.data.collection.Collection
import com.condor.splashbox.network.UnsplashApi
import retrofit2.HttpException
import java.io.IOException

class UnsplashCollectionsPagingSource(
    private val unsplashApi: UnsplashApi
) : PagingSource<Int, Collection>() {

    private val initialPage = 1

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Collection> {

        val position = params.key ?: initialPage

        return try {
            val response = unsplashApi.getCollections(position, params.loadSize)
            if (response.code() == 403) {
                throw (HttpException(response))
            }
            val collections: List<Collection>? = response.body()
            Log.d("CollectionsPagingSource", "Collections = $collections")
            LoadResult.Page(
                data = collections ?: emptyList(),
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (collections?.isEmpty() == true) null else position + 1
            )
        } catch (e: Exception) {
            Log.e("CollectionsPagingSource", "Exception = ${e.message}", e)
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Collection>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}