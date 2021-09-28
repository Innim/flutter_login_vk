package ru.innim.flutter_login_vk

import com.vk.api.sdk.auth.VKScope
import com.vk.sdk.api.users.dto.UsersFields


class VKClient {
    companion object {
        val fieldsDefault = listOf(
                UsersFields.ONLINE,
                UsersFields.PHOTO_50,
                UsersFields.PHOTO_100,
                UsersFields.PHOTO_200
        )

        private val scopeBytes = hashMapOf(
                VKScope.NOTIFY to 1,
                VKScope.FRIENDS to 2,
                VKScope.PHOTOS to 4,
                VKScope.AUDIO to 8,
                VKScope.VIDEO to 16,
                VKScope.STORIES to 64,
                VKScope.PAGES to 128,
                VKScope.STATUS to 1024,
                VKScope.NOTES to 2048,
                VKScope.MESSAGES to 4096,
                VKScope.WALL to 8192,
                VKScope.ADS to 32768,
                VKScope.OFFLINE to 65536,
                VKScope.DOCS to 131072,
                VKScope.GROUPS to 262144,
                VKScope.NOTIFICATIONS to 524288,
                VKScope.STATS to 1048576,
                VKScope.EMAIL to 4194304,
                VKScope.MARKET to 134217728
        )

        fun hasScopes(scopes: List<VKScope>, permission: Int): Boolean {
            if (scopes.isNotEmpty()) {
                var scopesMask = 0
                for (scope in scopes) {
                    val value: Int? = scopeBytes.get(key = scope)

                    if (value != null) {
                        scopesMask = scopesMask or value
                    }
                }
                return scopesMask and permission == scopesMask
            }
            return true
        }
    }
}