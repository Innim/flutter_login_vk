package ru.innim.flutter_login_vk;

import com.vk.api.sdk.auth.VKAccessToken;
import com.vk.sdk.api.base.dto.BaseBoolInt;
import com.vk.sdk.api.users.dto.UsersUserXtrCounters;

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
            put("message", error.errorMsg);
        }};
    }

    public static HashMap<String, Object> accessToken(final VKAccessToken accessToken) {
        if (accessToken == null)
            return null;
        return new HashMap<String, Object>() {{
            put("token", accessToken.getAccessToken());
            put("userId", String.valueOf(accessToken.getUserId()));
            put("created", accessToken.getCreated());
            put("email", accessToken.getEmail());
            put("isValid", accessToken.isValid());
            put("secret", accessToken.getSecret());
        }};
    }

    public static HashMap<String, Object> userProfile(final UsersUserXtrCounters user) {
        if (user == null)
            return null;

        return new HashMap<String, Object>() {{
            put("userId", user.getId().getValue());
            put("firstName", user.getFirstName());
            put("lastName", user.getLastName());
            put("online", user.getOnline() == BaseBoolInt.YES);
            put("onlineMobile", user.getOnlineMobile() == BaseBoolInt.YES);
            put("photo50", user.getPhoto50());
            put("photo100", user.getPhoto100());
            put("photo200", user.getPhoto200());
        }};
    }
}
