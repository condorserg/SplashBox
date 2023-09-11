package com.condor.splashbox.data.photo

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.condor.splashbox.App
import com.condor.splashbox.R
import com.condor.splashbox.data.user.CurrentUser
import com.condor.splashbox.db.UnsplashDatabase
import com.condor.splashbox.utils.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class PhotosListViewModel @Inject constructor(
    private val repository: PhotosRepository,
    val db: UnsplashDatabase
) : ViewModel() {

    private var currentQueryValue: String? = null
    private var currentSearchResult: Flow<PagingData<Photo>>? = null
    private val showToastLiveData = SingleLiveEvent<String>()
    val showToast: LiveData<String>
        get() = showToastLiveData
    private val photosStateFlow = MutableStateFlow<PagingData<Photo>>(PagingData.empty())
    val photos: StateFlow<PagingData<Photo>>
        get() = photosStateFlow.asStateFlow()

    init {
        Log.d("ViewModelInit", "init $this")
        getLatestPhotos()
    }

    @OptIn(FlowPreview::class)
    suspend fun searchPhotos(queryString: String): Flow<PagingData<Photo>> {
        val lastResult = currentSearchResult
        if (queryString == currentQueryValue && lastResult != null) {
            return lastResult
        }
        currentQueryValue = queryString

        val newResult: Flow<PagingData<Photo>> =
            repository.searchPhotos(queryString, ITEMS_PER_PAGE)
                .cachedIn(viewModelScope)
//                .catch {
//                    showToastLiveData.postValue(App.appContext.getString(R.string.searchPhotoError))
//                    Log.d("PhotoSearch", "Searching Photo Error: ${it.message}")
//                    emitAll(emptyFlow())
//                }
                .stateIn(viewModelScope)
                .debounce(700)
                .distinctUntilChanged()
                .flowOn(Dispatchers.IO)
        currentSearchResult = newResult
        return newResult
    }
    private fun getLatestPhotos() {
        viewModelScope.launch {
            repository.getLatestPhotos()
                .cachedIn(viewModelScope)
                .flowOn(Dispatchers.IO)
//                .catch {
//                    showToastLiveData.postValue(App.appContext.getString(R.string.photos_load_error_toast))
//                    Log.d("PhotosListViewModel", "getLatestPhotos Error = ${it.message}")
//                    emitAll(emptyFlow())
//                }
                .stateIn(viewModelScope)
                .collectLatest { photos ->
                    photosStateFlow.value = photos
                }
        }
    }
}
