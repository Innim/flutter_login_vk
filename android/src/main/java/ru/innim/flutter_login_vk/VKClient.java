package ru.innim.flutter_login_vk;

import com.vk.api.sdk.VK;
import com.vk.api.sdk.VKKeyValueStorage;
import com.vk.api.sdk.auth.VKAccessToken;
import com.vk.api.sdk.auth.VKScope;
import com.vk.sdk.api.users.dto.UsersFields;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class VKClient {

    static final List<UsersFields> FIELDS_DEFAULT = Arrays.asList(
            UsersFields.ONLINE,
            UsersFields.PHOTO_50,
            UsersFields.PHOTO_100,
            UsersFields.PHOTO_200
    );

    static final Map<VKScope, Integer> SCOPE_BYTES =
            new HashMap<VKScope, Integer>() {{
                put(VKScope.NOTIFY, 1);
                put(VKScope.FRIENDS, 2);
                put(VKScope.PHOTOS, 4);
                put(VKScope.AUDIO, 8);
                put(VKScope.VIDEO, 16);
                put(VKScope.STORIES, 64);
                put(VKScope.PAGES, 128);
                put(VKScope.STATUS, 1024);
                put(VKScope.NOTES, 2048);
                put(VKScope.MESSAGES, 4096);
                put(VKScope.WALL, 8192);
                put(VKScope.ADS, 32768);
                put(VKScope.OFFLINE, 65536);
                put(VKScope.DOCS, 131072);
                put(VKScope.GROUPS, 262144);
                put(VKScope.NOTIFICATIONS, 524288);
                put(VKScope.STATS, 1048576);
                put(VKScope.EMAIL, 4194304);
                put(VKScope.MARKET, 134217728);
            }};

    static boolean hasScopes(List<VKScope> scopes, int permission) {
        if (scopes != null && scopes.size() != 0) {
            int scopesMask = 0;
            for (VKScope scope : scopes) {
                final int value = SCOPE_BYTES.get(scope);
                scopesMask |= value;
            }

            return (scopesMask & permission) == scopesMask;
        }
        return true;
    }

    static VKAccessToken getCurrentAccessToken() {
        VKKeyValueStorage storage = VK.apiManager.getConfig().getKeyValueStorage();
        return VKAccessToken.Companion.restore(storage);
    }
}