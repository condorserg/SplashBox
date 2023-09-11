package com.condor.splashbox.authorization

import android.net.Uri
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ClientAuthentication
import net.openid.appauth.ClientSecretPost
import net.openid.appauth.TokenRequest

class AuthRepository {

    fun getAuthRequest(): AuthorizationRequest {
        val serviceConfiguration = AuthorizationServiceConfiguration(
            Uri.parse(AuthConfig.AUTH_URI),
            Uri.parse(AuthConfig.TOKEN_URI),
//            AuthConfig.CODE,
//            AuthConfig.GRANT_TYPE

        )

        val redirectUri = Uri.parse(AuthConfig.CALLBACK_URL)
        val parameters = mapOf(
            "Code" to AuthConfig.CODE,
            "GrantType" to AuthConfig.GRANT_TYPE
        )

        return AuthorizationRequest.Builder(
            serviceConfiguration,
            AuthConfig.CLIENT_ID,
            AuthConfig.RESPONSE_TYPE,
            redirectUri,
        )
            .setScope(AuthConfig.SCOPE)
            .setAdditionalParameters(parameters)
            .build()
    }


    fun performTokenRequest(
        authService: AuthorizationService,
        tokenRequest: TokenRequest,
        onComplete: () -> Unit,
        onError: () -> Unit
    ) {
        authService.performTokenRequest(
            tokenRequest,
            getClientAuthentication()
        ) { response, ex ->
            when {
                response != null -> {
                    val accessToken = response.accessToken.orEmpty()
                    AccessTokenSingleton.accessToken = accessToken
                    onComplete()
                }
                else -> onError()
            }
        }
    }


    private fun getClientAuthentication(): ClientAuthentication {
        return ClientSecretPost(AuthConfig.CLIENT_SECRET)
    }

    object AccessTokenSingleton {
        var accessToken: String = ""
    }

}

