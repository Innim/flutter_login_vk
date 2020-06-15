package ru.innim.flutter_login_vk;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.model.VKApiUser;

import java.util.HashMap;

public class Results {
    private enum LoginStatus {
        Success,
        Cancel,
        Error
    }

    public static HashMap<String, Object> loginCancelled() {
        return new HashMap<String, Object>() {{
            put("isCanceled", true);
        }};
    }

    public static HashMap<String, Object> loginSuccess(final VKAccessToken accessToken) {
        return new HashMap<String, Object>() {{
            put("accessToken", accessToken(accessToken));
        }};
    }

    public static HashMap<String, Object> error(final VKError error) {

        return new HashMap<String, Object>() {{
            put("apiCode", error.errorCode);
            put("message", error.errorReason);
        }};
    }

    public static HashMap<String, Object> accessToken(final VKAccessToken accessToken) {
        if (accessToken == null)
            return null;

        return new HashMap<String, Object>() {{
            put("token", accessToken.accessToken);
            put("userId", accessToken.userId);
            put("created", accessToken.created);
            put("expiresIn", accessToken.expiresIn);
            put("email", accessToken.email);
            put("httpsRequired", accessToken.httpsRequired);
            put("secret", accessToken.secret);
        }};
    }

    public static HashMap<String, Object> userProfile(final VKApiUser user) {
        if (user == null)
            return null;

        return new HashMap<String, Object>() {{
            put("userId", user.getId());
            put("firstName", user.first_name);
            put("lastName", user.last_name);
            put("online", user.online);
            put("onlineMobile", user.online_mobile);
            put("photo50", user.photo_50);
            put("photo100", user.photo_100);
            put("photo200", user.photo_200);
        }};
    }
}
