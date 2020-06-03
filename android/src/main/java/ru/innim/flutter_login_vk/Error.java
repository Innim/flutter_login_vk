package ru.innim.flutter_login_vk;

import com.vk.sdk.api.VKError;

public class Error {
    public String code;
    public int apiCode;
    public String message;

    public static Error vk(VKError error) {
        return new Error(error);
    }

    public static Error failed(String message) {
        return new Error(ErrorCode.FAILED, message);
    }

    public static Error unknown(String message) {
        return new Error(ErrorCode.UNKNOWN, message);
    }

    private Error(VKError error) {
        this.code = ErrorCode.VK_ERROR;
        this.apiCode = error.errorCode;
        this.message = error.errorMessage;
    }

    private Error(String code, String message) {
        this.code = code;
        this.apiCode = 0;
        this.message = message;
    }
}
