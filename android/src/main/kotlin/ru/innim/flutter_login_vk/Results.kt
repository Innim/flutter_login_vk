package ru.innim.flutter_login_vk

import com.vk.api.sdk.auth.VKAccessToken
import com.vk.sdk.api.base.dto.BaseBoolInt
import com.vk.sdk.api.users.dto.UsersUserFull
import java.util.HashMap

object Results {
    fun loginCancelled(): HashMap<String, Any> {
        return hashMapOf("isCanceled" to true)
    }

    fun loginSuccess(accessToken: VKAccessToken): HashMap<String, Any> {
        return hashMapOf("accessToken" to accessToken(accessToken))
    }

    fun error(error: VKError): HashMap<String, Any?> {
        return hashMapOf(
                "apiCode" to error.errorCode,
                "message" to error.errorMsg
        )
    }

    fun accessToken(accessToken: VKAccessToken): HashMap<String, Any?> {
        return hashMapOf(
                "token" to accessToken.accessToken,
                "userId" to accessToken.userId.toString(),
                "created" to accessToken.created,
                "email" to accessToken.email,
                "isValid" to accessToken.isValid,
                "secret" to accessToken.secret
        )
    }

    fun userProfile(user: UsersUserFull): HashMap<String, Any?> {
        return hashMapOf(
                "userId" to user.id.value,
                "firstName" to user.firstName,
                "lastName" to user.lastName,
                "online" to (user.online == BaseBoolInt.YES),
                "onlineMobile" to (user.onlineMobile == BaseBoolInt.YES),
                "photo50" to user.photo50,
                "photo100" to user.photo100,
                "photo200" to user.photo200
        )
    }
}
