package ru.innim.flutter_login_vk

import android.content.Intent
import com.vk.api.sdk.VK
import io.flutter.plugin.common.PluginRegistry

class ActivityListener(private val loginCallback: LoginCallback) : PluginRegistry.ActivityResultListener {

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        return VK.onActivityResult(requestCode, resultCode, data, loginCallback)
    }
}