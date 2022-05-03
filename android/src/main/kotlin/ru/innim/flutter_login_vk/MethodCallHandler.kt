package ru.innim.flutter_login_vk

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import com.vk.api.sdk.*
import com.vk.api.sdk.VK.execute
import com.vk.api.sdk.VK.getUserId
import com.vk.api.sdk.VK.initialize
import com.vk.api.sdk.VK.isLoggedIn
import com.vk.api.sdk.VK.login
import com.vk.api.sdk.VK.logout
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKScope
import com.vk.sdk.api.account.AccountService
import com.vk.sdk.api.users.UsersService
import com.vk.sdk.api.users.dto.UsersUserFull
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.util.*

class MethodCallHandler(private val context: Context, private val loginCallback: LoginCallback)
    : MethodChannel.MethodCallHandler {
    companion object {
        private const val logIn = "logIn"
        private const val logOut = "logOut"
        private const val getAccessToken = "getAccessToken"
        private const val getUserProfile = "getUserProfile"
        private const val getSdkVersion = "getSdkVersion"
        private const val initSdk = "initSdk"
        private const val scopeLoginArg = "scope"
        private const val scopeInitArg = "scope"
    }

    private var activity: Activity? = null

    fun updateActivity(activity: Activity?) {
        this.activity = activity
    }

    override fun onMethodCall(call: MethodCall, r: MethodChannel.Result) {
        activity?.let {
            when (call.method) {
                logIn -> {
                    val scope = call.argument<List<String>>(scopeLoginArg) ?: listOf()
                    logIn(scope, r)
                }
                logOut -> {
                    logOut()
                    sendResult(null, r)
                }
                getAccessToken -> sendResult(getAccessToken(), r)
                getUserProfile -> getUserProfile(r)
                getSdkVersion -> sendResult(getSdkVersion(), r)
                initSdk -> {
                    val initScope = call.argument<List<String>?>(scopeInitArg)
                    initSdk(initScope, r)
                }
                else -> r.notImplemented()
            }
        }
    }

    private fun initSdk(scope: List<String>?, channelResult: MethodChannel.Result) {
        initialize(context)

        if (scope != null && isLoggedIn()) {
            val userId = getUserId()
            execute(AccountService().accountGetAppPermissions(userId), object : VKApiCallback<Int> {
                override fun success(result: Int) {
                    val list = listOf(*scope.toTypedArray())
                    val vkScopes: List<VKScope> = getScopes(list)
                    if (!VKClient.hasScopes(vkScopes, result)) {
                        logout()
                    }
                    sendResult(true, channelResult)
                }

                override fun fail(error: Exception) {
                    sendError(FlutterError.apiError("Get profile permissions error: " + error.message,
                            VKError(0, error.message.toString())), channelResult)
                }
            })
        } else {
            sendResult(true, channelResult)
        }
    }

    private fun logIn(scopes: List<String>, result: MethodChannel.Result) {
        loginCallback.addPending(result)
        val list = listOf(*scopes.toTypedArray())
        val vkScopes: List<VKScope> = getScopes(list)
        // TODO: use ActivityResultLauncher
        // There are multiple problems with this new login approach:
        // 1. It's requires ComponentActivity, so FlutterActivity is not applicable
        // 2. We need register login activity at start of the app.
        // Which is not crucial but looks pretty bad.
        // 3. Even using FlutterFragmentActivity, we get error
        // "It's requires ComponentActivity, so FlutterActivity is not applicable" on launch
        @Suppress("DEPRECATION")
        login(activity!!, vkScopes)
    }

    private fun getScopes(list: List<String>): List<VKScope> {
        val vkScopes: MutableList<VKScope> = ArrayList()
        val count = list.size
        for (i in 0 until count) {
            val item = list[i]
            val scope = VKScope.valueOf(item.uppercase())
            vkScopes.add(scope)
        }
        return vkScopes
    }

    private fun logOut() {
        logout()
    }

    private fun getAccessToken(): HashMap<String, Any?>? {
        if (isLoggedIn()) {
            val storage = VKPreferencesKeyValueStorage(context)
            val token = VKAccessToken.restore(storage)

            if (token != null) {
                return Results.accessToken(token)
            }
        }

        return null
    }

    private fun getUserProfile(r: MethodChannel.Result) {
        val fields = VKClient.fieldsDefault

        execute(UsersService().usersGet(fields = fields),
                object : VKApiCallback<List<UsersUserFull?>?> {
                    override fun success(result: List<UsersUserFull?>?) {
                        if (result != null && result.isNotEmpty() && result[0] != null) {
                            sendResult(Results.userProfile(result[0]!!), r)
                        } else {
                            sendError(FlutterError.invalidResult("Get profile error: the result is null"), r)
                        }
                    }

                    override fun fail(error: Exception) {
                        sendError(FlutterError.apiError("Get profile error: " + error.message,
                                VKError(0, error.message.toString())), r)
                    }
                })
    }

    private fun getSdkVersion(): String {
        val metaData = context.packageManager.getApplicationInfo(
                context.packageName, PackageManager.GET_META_DATA)
        return metaData.metaData["VKSdkVersion"].toString()
    }

    private fun sendResult(data: Any?, r: MethodChannel.Result) {
        r.success(data)
    }

    private fun sendError(error: FlutterError, r: MethodChannel.Result) {
        r.error(error.code, error.message, error.details)
    }
}
