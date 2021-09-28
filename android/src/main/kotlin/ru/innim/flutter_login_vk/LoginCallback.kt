package ru.innim.flutter_login_vk

import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import io.flutter.plugin.common.MethodChannel
import java.util.HashMap

class LoginCallback : VKAuthCallback {
    private var pendingResult: MethodChannel.Result? = null

    fun addPending(result: MethodChannel.Result) {
        if (pendingResult != null) {
            callError(FlutterError.interrupted("Interrupted by another login call"))
        }
        pendingResult = result
    }

    override fun onLogin(token: VKAccessToken) {
        callResult(Results.loginSuccess(token))
    }

    override fun onLoginFailed(errorCode: Int) {
        if (errorCode == VKAuthCallback.AUTH_CANCELED) {
            callResult(Results.loginCancelled())
        } else {
            callError(FlutterError.apiError("Login failed: $errorCode",
                    VKError(errorCode)))
        }
    }

    private fun callResult(data: HashMap<String, Any>) {
        pendingResult?.success(data)
        pendingResult = null
    }

    private fun callError(error: FlutterError) {
        pendingResult?.error(error.code, error.message, error.details)
        pendingResult = null
    }
}