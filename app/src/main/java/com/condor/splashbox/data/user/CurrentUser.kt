package com.condor.splashbox.data.user

import com.squareup.moshi.JsonClass
import com.condor.splashbox.data.photo.Photo


@JsonClass(generateAdapter = true)
data class CurrentUser(

    val id: String,
    val name: String?,
    val updated_at: String,
    val username: String?,
    val first_name: String?,
    val last_name: String?,
    val twitter_username: String?,
    val portfolio_url: String?,
    val bio: String?,
    val location: String?,
    val links: Links?,
    val profile_image: ProfileImage?,
    val instagram_username: String?,
    val total_likes: Int?,
    val total_photos: Int?,
    val total_collections: Int?,
    val photos: List<Photo>?,
    val followed_by_user: Boolean?,
    val followers_count: Int?,
    val following_count: Int?,
    val downloads: Int?,
    val uploads_remaining: Int?,
    val email: String?
)
