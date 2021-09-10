package ru.innim.flutter_login_vk;

import com.vk.api.sdk.VK;
import com.vk.api.sdk.VKKeyValueStorage;
import com.vk.api.sdk.auth.VKAccessToken;
import com.vk.api.sdk.auth.VKScope;
import com.vk.sdk.api.users.dto.UsersFields;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VKClient {

    static final String SDK_VERSION = "3.2.2";

    static final List<UsersFields> FIELDS_DEFAULT = Arrays.asList(
            UsersFields.ONLINE,
            UsersFields.PHOTO_50,
            UsersFields.PHOTO_100,
            UsersFields.PHOTO_200
    );

    /**
     * Converts integer value of permissions into arraylist of constants
     */
    public static ArrayList<VKScope> parseVkPermissionsFromInteger(int permissionsValue) {
        ArrayList<VKScope> res = new ArrayList<>();
        if ((permissionsValue & 1) > 0) res.add(VKScope.NOTIFY);
        if ((permissionsValue & 2) > 0) res.add(VKScope.FRIENDS);
        if ((permissionsValue & 4) > 0) res.add(VKScope.PHOTOS);
        if ((permissionsValue & 8) > 0) res.add(VKScope.AUDIO);
        if ((permissionsValue & 16) > 0) res.add(VKScope.VIDEO);
        if ((permissionsValue & 64) > 0) res.add(VKScope.STORIES);
        if ((permissionsValue & 128) > 0) res.add(VKScope.PAGES);
        if ((permissionsValue & 1024) > 0) res.add(VKScope.STATUS);
        if ((permissionsValue & 2048) > 0) res.add(VKScope.NOTES);
        if ((permissionsValue & 4096) > 0) res.add(VKScope.MESSAGES);
        if ((permissionsValue & 8192) > 0) res.add(VKScope.WALL);
        if ((permissionsValue & 32768) > 0) res.add(VKScope.ADS);
        if ((permissionsValue & 65536) > 0) res.add(VKScope.OFFLINE);
        if ((permissionsValue & 131072) > 0) res.add(VKScope.DOCS);
        if ((permissionsValue & 262144) > 0) res.add(VKScope.GROUPS);
        if ((permissionsValue & 524288) > 0) res.add(VKScope.NOTIFICATIONS);
        if ((permissionsValue & 1048576) > 0) res.add(VKScope.STATS);
        if ((permissionsValue & 4194304) > 0) res.add(VKScope.EMAIL);
        if ((permissionsValue & 134217728) > 0) res.add(VKScope.MARKET);
        return res;
    }

    static boolean hasScopes(List<VKScope> scopes, int permission) {
        boolean allScope = true;
        final ArrayList<VKScope> permissionScopes = parseVkPermissionsFromInteger(permission);
        for (VKScope scope : scopes) {
            if (!permissionScopes.contains(scope)) {
                allScope = false;
                break;
            }
        }
        return allScope;
    }

    static VKAccessToken getCurrentAccessToken() {
        VKKeyValueStorage storage = VK.apiManager.getConfig().getKeyValueStorage();
        return VKAccessToken.Companion.restore(storage);
    }
}