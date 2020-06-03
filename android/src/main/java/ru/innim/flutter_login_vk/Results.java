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

    public static HashMap<String, Object> loginResult(final VKAccessToken result, final Error error) {
        return new HashMap<String, Object>() {{
            put("login", login(result, error));
            put("error", error(error));
        }};
    }

    public static HashMap<String, Object> accessTokenResult(final VKAccessToken accessToken, final Error error) {
        return new HashMap<String, Object>() {{
            put("accessToken", accessToken(accessToken));
            put("error", error(error));
        }};
    }

    public static HashMap<String, Object> userProfileResult(final VKApiUser user, final Error error) {

        return new HashMap<String, Object>() {{
            put("profile", userProfile(user));
            put("error", error(error));
        }};
    }

    public static HashMap<String, Object> login(final VKAccessToken result, final Error error) {
        final LoginStatus status = error == null ? LoginStatus.Success :
                (error.apiCode == VKError.VK_CANCELED) ? LoginStatus.Cancel : LoginStatus.Error;
        return new HashMap<String, Object>() {{
            put("status", status.name());
            put("accessToken", accessToken(result));
        }};
    }

    private static HashMap<String, Object> accessToken(final VKAccessToken accessToken) {
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

    private static HashMap<String, Object> userProfile(final VKApiUser user) {
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

    private static HashMap<String, Object> error(final Error error) {
        if (error == null)
            return null;

        return new HashMap<String, Object>() {{
            put("code", error.code);
            put("apiCode", error.apiCode);
            put("message", error.message);
        }};
    }
}
