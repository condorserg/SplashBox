package com.condor.splashbox.data.photo

import android.app.DownloadManager
import android.content.Context.DOWNLOAD_SERVICE
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import androidx.paging.*
import com.condor.splashbox.App
import com.condor.splashbox.BuildConfig
import com.condor.splashbox.datasource.UnsplashSearchPhotoPagingSource
import com.condor.splashbox.db.UnsplashDatabase
import com.condor.splashbox.db.UnsplashRemoteMediator
import com.condor.splashbox.network.UnsplashApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import javax.inject.Inject

//@Singleton
const val ITEMS_PER_PAGE = 20

class PhotosRepository @Inject constructor(
    val db: UnsplashDatabase,
    private val unsplashApi: UnsplashApi,

    ) {

    @OptIn(ExperimentalPagingApi::class)
    fun getLatestPhotos(): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(
                pageSize = ITEMS_PER_PAGE,
                enablePlaceholders = false,
            ),
            remoteMediator = UnsplashRemoteMediator(unsplashApi, db),
            pagingSourceFactory = { db.photosDao().getPhotos() }
        ).flow.flowOn(Dispatchers.IO)
    }

    fun searchPhotos(query: String, itemsPerPage: Int): Flow<PagingData<Photo>> =
        Pager(
            config = PagingConfig(
                pageSize = itemsPerPage,
                enablePlaceholders = false
            )
        ) {
            UnsplashSearchPhotoPagingSource(query = query, unsplashApi = unsplashApi)
        }.flow.flowOn(Dispatchers.IO)

    fun getPhotoDetails(
        id: String,
        onComplete: (Photo) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        unsplashApi.getPhotoById(id).enqueue(

            object : Callback<Photo> {
                override fun onResponse(
                    call: Call<Photo>,
                    response: Response<Photo>
                ) {
                    if (response.isSuccessful) {
                        onComplete(response.body()!!)
                    } else {
                        Log.d("Server", "Incorrect Status Code")
                        onError(RuntimeException("Incorrect Status Code"))

                    }
                }

                override fun onFailure(call: Call<Photo>, t: Throwable) {
                    Log.e("Server", "Response parsing error = ${t.message}", t)
                    onError(t)

                }

            }
        )
    }

    suspend fun likePhoto(id: String) = unsplashApi.likePhoto(id)
    suspend fun unlikePhoto(id: String) = unsplashApi.unlikePhoto(id)

    fun downloadPhotoDM(url: String, fileName: String) {
        val fullFileName = "$fileName.jpg"
        Log.d("DownloadButton", "FileName = $fullFileName, URL=$url")

        val request = DownloadManager.Request(Uri.parse(url))
            .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            .setTitle("SplashBox")
            .setDescription("Downloading file...")
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "$fileName.jpg")

        val downloadManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            App.appContext.getSystemService(DownloadManager::class.java)
        } else {
            App.appContext.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        }
        val downloadId = downloadManager.enqueue(request)
        while (true) {
            val query = DownloadManager.Query().setFilterById(downloadId)
            val cursor = downloadManager.query(query)
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                val reason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON)
                if (columnIndex != -1) {
                    val status = cursor.getInt(columnIndex)
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        break
                    } else if (status == DownloadManager.STATUS_FAILED) {
                        Log.d(
                            "DownloadError",
                            "Download file failed with error: $reason, URL: $url"
                        )
                        break
                    }
                }
            }
            cursor.close()
        }
    }

    suspend fun downloadPhoto(url: String, fileName: String): Uri {
        val request = Request.Builder()
            .url(url)
            .build()

        val client = OkHttpClient.Builder()
            .build()

        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            "$fileName.jpg"
        )

        withContext(Dispatchers.IO) {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    throw IOException("Failed to download image: $response")
                }
                val inputStream = response.body.byteStream()
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                MediaScannerConnection.scanFile(
                    App.appContext,
                    arrayOf(file.absolutePath),
                    null
                ) { _, _ -> }
            }
        }
        return FileProvider.getUriForFile(
            App.appContext,
            BuildConfig.APPLICATION_ID + ".provider",
            file
        )
    }
}

