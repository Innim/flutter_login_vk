package ru.innim.flutter_login_vk;

import android.content.Intent;

import com.vk.api.sdk.VK;

import io.flutter.plugin.common.PluginRegistry;

public class ActivityListener implements PluginRegistry.ActivityResultListener {

    private LoginCallback _loginCallback;

    public ActivityListener(LoginCallback loginCallback) {
        _loginCallback = loginCallback;
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        return data == null || !VK.onActivityResult(requestCode, resultCode, data, _loginCallback);
    }
}
