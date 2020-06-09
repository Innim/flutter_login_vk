package ru.innim.flutter_login_vk;

import android.app.Activity;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKSdk;
import com.vk.sdk.VKSdkVersion;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKApiUserFull;

import java.util.HashMap;
import java.util.List;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.Result;

public class MethodCallHandler implements MethodChannel.MethodCallHandler {
    private final static String _LOGIN_METHOD = "logIn";
    private final static String _LOGOUT_METHOD = "logOut";
    private final static String _GET_ACCESS_TOKEN = "getAccessToken";
    private final static String _GET_USER_PROFILE = "getUserProfile";
    private final static String _SCOPE_ARG = "scope";
    private final static String _GET_SDK_VERSION = "getSdkVersion";

    private final LoginCallback _loginCallback;
    private Activity _activity;

    public MethodCallHandler(LoginCallback loginCallback) {
        _loginCallback = loginCallback;
    }

    public void updateActivity(Activity activity) {
        _activity = activity;
    }

    @Override
    public void onMethodCall(MethodCall call, Result r) {
        if (_activity != null) {
            switch (call.method) {
                case _LOGIN_METHOD:
                    final List<String> scope = call.argument(_SCOPE_ARG);
                    logIn(scope, r);
                    break;
                case _LOGOUT_METHOD:
                    logOut();
                    result(null, r);
                    break;
                case _GET_ACCESS_TOKEN:
                    result(getAccessToken(), r);
                    break;
                case _GET_USER_PROFILE:
                    getUserProfile(r);
                    break;
                case _GET_SDK_VERSION:
                    result(getSdkVersion(), r);
                    break;
                default:
                    r.notImplemented();
                    break;
            }
        }
    }

    private void logIn(List<String> scope, Result result) {
        _loginCallback.addPending(result);
        VKSdk.login(_activity, scope.toArray(new String[0]));
    }

    private void logOut() {
        VKSdk.logout();
    }

    private HashMap<String, Object> getAccessToken() {
        // TODO @ivan: remove all errors - it's not a error
        // method should just return null if there is no accessToken
        if (VKSdk.isLoggedIn()) {
            final VKAccessToken token = VKAccessToken.currentToken();

            if (token != null) {
                return Results.accessTokenResult(token, null);
            }

            return Results.accessTokenResult(null, Error.unknown("User token is null"));
        }

        return Results.accessTokenResult(null, Error.failed("User is not logged in"));
    }

    private void getUserProfile(final Result r) {
        final VKAccessToken token = VKAccessToken.currentToken();
        if (token != null) {
            VKRequest request = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS,
                    VKApiUser.FIELDS_DEFAULT));
            request.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    @SuppressWarnings("unchecked")
                    final List<VKApiUserFull> users = (List<VKApiUserFull>) response.parsedModel;
                    result(Results.userProfileResult(users.get(0), null), r);
                }

                @Override
                public void onError(VKError error) {
                    result(Results.userProfileResult(null, Error.vk(error)), r);
                }

                @Override
                public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                    result(Results.userProfileResult(null, Error.failed(
                            "Attempt " + attemptNumber + " failed.")), r);
                }
            });
        }
    }

    private String getSdkVersion() {
        return VKSdkVersion.SDK_VERSION;
    }

    private void result(Object data, Result r) {
        r.success(data);
    }
}
