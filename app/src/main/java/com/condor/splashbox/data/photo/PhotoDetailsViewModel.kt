package com.condor.splashbox.data.photo

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.condor.splashbox.App
import com.condor.splashbox.R
import com.condor.splashbox.db.UnsplashDatabase
import com.condor.splashbox.utils.NotificationsHelper
import com.condor.splashbox.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PhotoDetailsViewModel @Inject constructor(
    private val repository: PhotosRepository,
    val db: UnsplashDatabase
) : ViewModel() {

    private val unsplashPhotoLiveData = MutableLiveData<Photo>()
    private val isLoadingLiveData = MutableLiveData(false)

    val photo: LiveData<Photo>
        get() = unsplashPhotoLiveData

    private val showToastLiveData = SingleLiveEvent<String>()
    val showToast: LiveData<String>
        get() = showToastLiveData

    val isLoading: LiveData<Boolean>
        get() = isLoadingLiveData

    fun getPhotoDetails(id: String) {
        isLoadingLiveData.postValue(true)
        repository.getPhotoDetails(
            id,
            onComplete = { photo ->
                isLoadingLiveData.postValue(false)
                unsplashPhotoLiveData.postValue(photo)

            }, onError = {
                isLoadingLiveData.postValue(false)
                showToastLiveData.postValue(App.appContext.getString(R.string.getPhotoDetailsError))
            })
    }

    fun likePhoto(id: String) {
        viewModelScope.launch (Dispatchers.IO){
            try {
                repository.likePhoto(id)
            } catch (t: Throwable) {
                Log.e("LikePhoto", "Like Photo Error", t)
                showToastLiveData.postValue(App.appContext.getString(R.string.likePhotoError))
            }
        }
    }

    fun unlikePhoto(id: String) = viewModelScope.launch (Dispatchers.IO) {
        try {
            repository.unlikePhoto(id)
        } catch (t: Throwable) {
            Log.e("LikePhoto", "Like Photo Error", t)
            showToastLiveData.postValue(App.appContext.getString(R.string.likePhotoError))
        }
    }

    suspend fun downloadPhoto(photoUrl: String, fileName: String) {
        viewModelScope.launch (Dispatchers.IO){
            try {
                showToastLiveData.postValue(App.appContext.getString(R.string.downloadStarted))
                val fileUri = repository.downloadPhoto(photoUrl, fileName)
                Log.d("FileUri", "FileUri = $fileUri")
                NotificationsHelper.showDownloadCompleteNotification(
                    "$fileName.jpg",
                    App.appContext.getString(R.string.downloadSuccessNotification),
                    fileUri
                )
                showToastLiveData.postValue(App.appContext.getString(R.string.downloadSuccessNotification))
            } catch (t: Throwable) {
                showToastLiveData.postValue(App.appContext.getString(R.string.downloadErrorNotification))
                Log.d("DownloadError", "Download Photo Error = ${t.message}")
                NotificationsHelper.showDownloadErrorNotification(
                    "$fileName.jpg",
                    App.appContext.getString(R.string.downloadErrorNotification)
                )
            }
        }
    }

    fun downloadPhotoDM(photoUrl: String, fileName: String) {
        repository.downloadPhotoDM(photoUrl, fileName)
    }
}