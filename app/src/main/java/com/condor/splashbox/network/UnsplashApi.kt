package com.condor.splashbox.network

import com.condor.splashbox.data.photo.Photo
import com.condor.splashbox.data.photo.PhotoSearchResult
import com.condor.splashbox.data.collection.Collection
import com.condor.splashbox.data.user.CurrentUser
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*


interface UnsplashApi {

    @GET("photos")
    suspend fun getLatestPhotos(
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int,
        @Query("client_id") clientId: String
    ): Response<List<Photo>>


    @GET("search/photos")
    suspend fun searchPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") itemsPerPage: Int,
        @Query("client_id") clientId: String
    ): Response<PhotoSearchResult>


    @GET("photos/{id}")
    fun getPhotoById(
        @Path("id") id: String
    ): Call<Photo>

    @POST("photos/{id}/like")
    suspend fun likePhoto(
        @Path("id") id: String
    ): ResponseBody

    @DELETE("photos/{id}/like")
    suspend fun unlikePhoto(
        @Path("id") id: String
    ): Response<Unit>

    @GET("collections")
    suspend fun getCollections(
        @Query("page") page: Int?,
        @Query("per_page") per_page: Int?
    ): Response<List<Collection>>

    @GET("collections/{id}/photos")
    suspend fun getCollectionPhotos(
        @Path("id") id: String,
        @Query("page") page: Int?,
        @Query("per_page") per_page: Int?
    ): Response<List<Photo>>

    @GET("me")
    suspend fun getCurrentUser(): Response<CurrentUser>

    @GET("users/{username}/likes")
    suspend fun getUserLikedPhotos(
        @Path("username") username: String,
        @Query("page") page: Int?,
        @Query("per_page") per_page: Int?
    ): Response<List<Photo>>
}
