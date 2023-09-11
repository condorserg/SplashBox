package com.condor.splashbox.db

object PhotosContract {

    const val PHOTOS_TABLE_NAME = "photos"
    const val REMOTE_KEYS_TABLE_NAME = "remote_keys"

    object Columns{
        const val ID = "photo_id"
        const val PHOTO_URL = "photo_url"
        const val USER_AVATAR_URL = "user_avatar_url"
    }
}