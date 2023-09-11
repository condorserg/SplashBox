package com.condor.splashbox.data.user

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.condor.splashbox.data.photo.Photo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {
    private val currentUserStateFlow = MutableStateFlow<CurrentUser?>(null)
    val currentUser: StateFlow<CurrentUser?>
        get() = currentUserStateFlow.asStateFlow()

    init {
        getCurrentUser()
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            try {
                repository.getCurrentUser()
                    .collect { user ->
                        currentUserStateFlow.value = user
                    }
            } catch (e: Exception) {
                Log.d("UserViewModel", "Get Current User Error = ${e.message}")
            }
        }

    }

    fun getUserLikedPhotos(userName: String): Flow<PagingData<Photo>> {
        return try {
            repository.getUserLikedPhotos(userName)
                .onEach { }
                .cachedIn(viewModelScope).stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(),
                    initialValue = PagingData.empty()
                )
        } catch (e: Exception) {
            emptyFlow()
        }
    }
}