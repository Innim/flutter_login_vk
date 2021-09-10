package ru.innim.flutter_login_vk;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.vk.api.sdk.BuildConfig;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.VKApiCallback;
import com.vk.api.sdk.auth.VKAccessToken;
import com.vk.api.sdk.auth.VKScope;
import com.vk.sdk.api.account.AccountService;
import com.vk.sdk.api.users.UsersService;
import com.vk.sdk.api.users.dto.UsersFields;
import com.vk.sdk.api.users.dto.UsersUserXtrCounters;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
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
                    final List<String> initScope = call.argument(_SCOPE_INIT_ARG);
                    initSdk(initScope, r);
                    break;
                default:
                    r.notImplemented();
                    break;
            }
        }
    }

    private void initSdk(final List<String> scope, final Result r) {
        VK.initialize(_context);

        if (scope != null && VK.isLoggedIn()) {
            final int userId = VK.getUserId();
            VK.execute(new AccountService().accountGetAppPermissions(userId), new VKApiCallback<Integer>() {
                @Override
                public void success(Integer o) {
                    List<String> list = Arrays.asList(scope.toArray(new String[0]));
                    List<VKScope> vkScopes = getScopes(list);
                    if (!VKClient.hasScopes(vkScopes, o)) {
                        VK.logout();
                    }

                    result(true, r);
                }

                @Override
                public void fail(@NotNull Exception o) {
                    error(FlutterError.apiError("Get profile permissions error: " + o.getMessage(),
                            new VKError(0, o.getMessage())), r);
                }
            });
        } else {
            result(true, r);
        }
    }

    private void logIn(List<String> scopes, Result result) {
        _loginCallback.addPending(result);

        List<String> list = Arrays.asList(scopes.toArray(new String[0]));
        List<VKScope> vkScopes = getScopes(list);

        VK.login(_activity, vkScopes);
    }

    private List<VKScope> getScopes(List<String> list) {
        List<VKScope> vkScopes = new ArrayList<>();
        int count = list.size();
        for (int i = 0; i < count; ++i) {
            String item = list.get(i);
            VKScope scope = VKScope.valueOf(item.toUpperCase());
            vkScopes.add(scope);
        }

        return vkScopes;
    }

    private void logOut() {
        VK.logout();
    }

    private HashMap<String, Object> getAccessToken() {
        if (VK.isLoggedIn()) {
            final VKAccessToken token = VKClient.getCurrentAccessToken();
            if (token != null) {
                return Results.accessToken(token);
            }
        }

        return null;
    }

    private void getUserProfile(final Result r) {
        List<UsersFields> fields = VKClient.FIELDS_DEFAULT;

        final VKAccessToken token = VKClient.getCurrentAccessToken();
        if (token != null) {
            VK.execute(new UsersService().usersGet(null, fields, null),
                    new VKApiCallback<List<UsersUserXtrCounters>>() {
                        @Override
                        public void success(List<UsersUserXtrCounters> o) {
                            result(Results.userProfile(o.get(0)), r);
                        }

                        @Override
                        public void fail(@NotNull Exception o) {
                            error(FlutterError.apiError("Get profile error: " + o.getMessage(),
                                    new VKError(0, o.getMessage())), r);
                        }
                    });
        }
    }

    private String getSdkVersion() {
        return BuildConfig.VERSION_NAME;
    }

    private void result(Object data, Result r) {
        r.success(data);
    }

    private void error(FlutterError error, Result r) {
        r.error(error.code, error.message, error.details);
    }
}
