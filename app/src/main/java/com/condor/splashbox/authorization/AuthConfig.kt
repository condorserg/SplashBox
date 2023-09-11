package com.condor.splashbox.authorization

import net.openid.appauth.ResponseTypeValues

object AuthConfig {

    const val AUTH_URI = "https://unsplash.com/oauth/authorize"
    const val TOKEN_URI = "https://unsplash.com/oauth/token"
    const val RESPONSE_TYPE = ResponseTypeValues.CODE
    const val SCOPE =
       "public read_user write_user read_photos write_photos write_likes write_followers read_collections write_collections"

    const val CLIENT_ID = "jdqJ7XDpP_AF10l5BraSYl-YB-SXHmZPWAlc29DikY8"
    const val CLIENT_SECRET = "m8IDdTT5E6YkeNkIz3x4932R8NaFZnJf6EchjOTf4do"
    const val CALLBACK_URL = "unsplashskillbox://callback"
    const val CODE = "AzCHJgLNrsqEXCDxVd7eQGhG1mSiICvarxxmBt5Qq3s"
    const val GRANT_TYPE = "authorization_code"
}