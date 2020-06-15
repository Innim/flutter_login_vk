package ru.innim.flutter_login_vk;

import android.app.Activity;
import android.content.Context;

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
    private final static String _GET_SDK_VERSION = "getSdkVersion";
    private final static String _INIT_SDK_METHOD = "initSdk";
    private final static String _SCOPE_LOGIN_ARG = "scope";
    private final static String _SCOPE_INIT_ARG = "scope";
    private final static String _APP_ID_INIT_ARG = "appId";
    private final static String _API_VERSION_INIT_ARG = "apiVersion";


    private final LoginCallback _loginCallback;
    private Activity _activity;
    private Context _context;

    public MethodCallHandler(Context context, LoginCallback loginCallback) {
        _loginCallback = loginCallback;
        _context = context;
    }

    public void updateActivity(Activity activity) {
        _activity = activity;
    }

    @Override
    public void onMethodCall(MethodCall call, Result r) {
        if (_activity != null) {
            switch (call.method) {
                case _LOGIN_METHOD:
                    final List<String> scope = call.argument(_SCOPE_LOGIN_ARG);
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
                case _INIT_SDK_METHOD:
                    final String rawAppId = call.argument(_APP_ID_INIT_ARG);
                    int appId = 0;
                    if (rawAppId != null) {
                        appId = Integer.parseInt(rawAppId);
                        if (appId != 0) {
                            String apiVersion = call.argument(_API_VERSION_INIT_ARG);
                            final List<String> initScope = call.argument(_SCOPE_INIT_ARG);
                            if (apiVersion == null)
                                apiVersion = "";
                            result(initSdk(appId, apiVersion, initScope), r);
                        } else {
                            error(FlutterError.invalidArgs("Arguments is invalid", null), r);
                        }
                    } else {
                        error(FlutterError.invalidArgs("Arguments is invalid", null), r);
                    }

                    break;
                default:
                    r.notImplemented();
                    break;
            }
        }
    }

    private boolean initSdk(int appId, String apiVersion, List<String> scope) {
        VKSdk.customInitialize(_context, appId, apiVersion);

        if (scope != null && VKSdk.isLoggedIn()) {
            final VKAccessToken token = VKAccessToken.currentToken();
            if (token != null) {
                if (!token.hasScope(scope.toArray(new String[0])))
                    logOut();
            }
        }

        return true;
    }

    private void logIn(List<String> scope, Result result) {
        _loginCallback.addPending(result);
        VKSdk.login(_activity, scope.toArray(new String[0]));
    }

    private void logOut() {
        VKSdk.logout();
    }

    private HashMap<String, Object> getAccessToken() {
        if (VKSdk.isLoggedIn()) {
            final VKAccessToken token = VKAccessToken.currentToken();

            if (token != null) {
                return Results.accessToken(token);
            }

        }

        return null;
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
                    result(Results.userProfile(users.get(0)), r);
                }

                @Override
                public void onError(VKError error) {
                    error(FlutterError.apiError("Get profile error: " + error.errorMessage, error), r);
                }

                @Override
                public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                    error(FlutterError.invalidResult("Get user profile attempt failed", null), r);
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

    private void error(FlutterError error, Result r) {
        r.error(error.code, error.message, error.details);
    }
}
