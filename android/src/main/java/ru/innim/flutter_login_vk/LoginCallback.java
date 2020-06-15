package ru.innim.flutter_login_vk;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.api.VKError;

import java.util.HashMap;

import io.flutter.plugin.common.MethodChannel;

public class LoginCallback implements VKCallback<VKAccessToken> {
    private MethodChannel.Result _pendingResult;

    public void addPending(MethodChannel.Result result) {
        if (_pendingResult != null)
            callError(FlutterError.interrupted("Interrupted by another login call", null));

        _pendingResult = result;
    }

    @Override
    public void onResult(final VKAccessToken accessToken) {
        callResult(Results.loginSuccess(accessToken));
    }

    @Override
    public void onError(VKError error) {
        if (error.errorCode == VKError.VK_CANCELED) {
            callResult(Results.loginCancelled());
        } else {
            callError(FlutterError.apiError("Login failed: " + error.errorMessage, error));
        }
    }

    private void callResult(HashMap<String, Object> data) {
        if (_pendingResult != null) {
            _pendingResult.success(data);
            _pendingResult = null;
        }
    }

    private void callError(FlutterError error) {
        if (_pendingResult != null) {
            _pendingResult.error(error.code, error.message, error.details);
            _pendingResult = null;
        }
    }
}
