package ru.innim.flutter_login_vk;

import com.vk.api.sdk.auth.VKAccessToken;
import com.vk.api.sdk.auth.VKScope;
import com.vk.sdk.api.users.dto.UsersFields;

import java.util.Arrays;
import java.util.List;

public class VKClient {

    static VKAccessToken TOKEN;
    static String SDK_VERSION = "3.2.2";

    static List<UsersFields> FIELDS_DEFAULT = Arrays.asList(
            UsersFields.ONLINE,
            UsersFields.PHOTO_50,
            UsersFields.PHOTO_100,
            UsersFields.PHOTO_200
    );
    static List<Integer> SCOPE_BYTES =
            Arrays.asList(
                    1,
                    2,
                    4,
                    8,
                    16,
                    64,
                    128,
                    1024,
                    2048,
                    4096,
                    8192,
                    32768,
                    65536,
                    131072,
                    262144,
                    524288,
                    1048576,
                    4194304,
                    134217728);

    static boolean hasScope(List<VKScope> scopes, int permission) {
        if (scopes != null && scopes.size() != 0) {
            int bytes = 0;

            for (int i = 0; i < scopes.size(); ++i) {
                VKScope scope = scopes.get(i);
                List<VKScope> arr = Arrays.asList(VKScope.values().clone());
                int index = arr.indexOf(scope);
                bytes += SCOPE_BYTES.get(index);

            }

            return permission == bytes;
        }

        return false;
    }
}

