package com.condor.splashbox.data.collection

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.condor.splashbox.data.photo.Photo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class CollectionsViewModel @Inject constructor(
    private val repository: CollectionsRepository
) : ViewModel() {

    private val _collections = MutableStateFlow<PagingData<Collection>>(PagingData.empty())
    val collections = _collections.asStateFlow()

    fun getCollections(): Flow<PagingData<Collection>> {
        return repository.getCollections().cachedIn(viewModelScope)
    }

    fun getCollectionPhotos(collectionId: String): Flow<PagingData<Photo>> {
        return repository.getCollectionPhotos(collectionId).cachedIn(viewModelScope).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = PagingData.empty()
        )
    }
}