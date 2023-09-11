package com.condor.splashbox.data.photo

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.condor.splashbox.data.user.User
import com.condor.splashbox.db.PhotosContract

import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Entity(
    tableName = PhotosContract.PHOTOS_TABLE_NAME,
)

@Parcelize
@JsonClass(generateAdapter = true)
data class Photo(
    @PrimaryKey
    @ColumnInfo(name = PhotosContract.Columns.ID)
    val id: String,
    val created_at: String?,
    val updated_at: String?,
    val width: Int?,
    val height: Int?,
    var likes: Int?,
    var liked_by_user: Boolean?,
    val views: Int?,
    val downloads: Int?,
    val description: String?,
    val alt_description: String?,
    @Embedded
    val exif: Exif?,
    @Embedded
    val urls: Urls,
    @Embedded (prefix = "photo_")
    val links: Links?,
    @Embedded (prefix = "user_")
    val user: User?,
    @Embedded
    val location: Location?
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Exif(
    val make: String?,
    val model: String?,
    val exposure_time: String?,
    val aperture: String?,
    val focal_length: String?,
    val iso: Int?
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Location(
    val city: String?,
    val country: String?,
//    val position: Position?
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Position(
    val latitude: Double?,
    val longitude: Double?
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Tag(
    val type: String?,
    val title: String?
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Urls(
    val thumb: String?,
    val small: String,
    @ColumnInfo(name = PhotosContract.Columns.PHOTO_URL)
    val regular: String?,
    val full: String?,
    val raw: String?,
    val small_s3: String?
) : Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class Links(
    val self: String,
    val html: String,
    val photos: String?,
    val likes: String?,
    val portfolio: String?,
    val download: String?,
    val download_location: String?
) : Parcelable