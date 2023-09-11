package com.condor.splashbox.data.user

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import com.condor.splashbox.db.PhotosContract
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class User(
    val id: String,
    val username: String,
    val name: String,
    val portfolio_url: String?,
    val bio: String?,
    val location: String?,
    val total_likes: Int,
    val total_photos: Int,
    val total_collections: Int,
    @Embedded
    val profile_image: ProfileImage,
    @Embedded (prefix = "user_")
    val links: Links
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class ProfileImage(
    @ColumnInfo(name = PhotosContract.Columns.USER_AVATAR_URL)
    val small: String,
    val medium: String,
    val large: String
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Badge(
    val title: String?,
    val primary: Boolean?,
    val slug: String?,
    val link: String?
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Links(
    val self: String,
    val html: String,
    val photos: String,
    val likes: String,
    val portfolio: String,
    val following: String,
    val followers: String
) : Parcelable
