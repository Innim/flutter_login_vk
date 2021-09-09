package ru.innim.flutter_login_vk;

import com.vk.api.sdk.auth.VKAccessToken;
import com.vk.api.sdk.auth.VKAuthCallback;

import java.util.HashMap;

import io.flutter.plugin.common.MethodChannel;

public class LoginCallback implements VKAuthCallback {
    private MethodChannel.Result _pendingResult;

    public void addPending(MethodChannel.Result result) {
        if (_pendingResult != null)
            callError(FlutterError.interrupted("Interrupted by another login call", null));

        _pendingResult = result;
    }

    @Override
    public void onLogin(final VKAccessToken accessToken) {
        VKClient.token = accessToken;
        callResult(Results.loginSuccess(accessToken));
    }

    @Override
    public void onLoginFailed(int errorCode) {
        if (errorCode == VKAuthCallback.AUTH_CANCELED) {
            callResult(Results.loginCancelled());
        } else {
            callError(FlutterError.apiError("Login failed: " + errorCode,
                    new VKError(errorCode, null)));
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
