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
            callError(ErrorCode.INTERRUPTED, "Waiting login result was been interrupted!");

        _pendingResult = result;
    }

    @Override
    public void onResult(final VKAccessToken loginResult) {
        callResult(Results.loginResult(loginResult, null));
    }

    @Override
    public void onError(VKError error) {
        callResult(Results.loginResult(null, Error.vk(error)));
    }

    private void callResult(HashMap<String, Object> data) {
        if (_pendingResult != null) {
            _pendingResult.success(data);
            _pendingResult = null;
        }
    }

    private void callError(String code, String message) {
        if (_pendingResult != null) {
            _pendingResult.error(code, message, null);
            _pendingResult = null;
        }
    }
}
