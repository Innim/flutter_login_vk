package ru.innim.flutter_login_vk;

import com.vk.sdk.api.VKError;

public class FlutterError {
    String code;
    String message;
    Object details;

    public static FlutterError invalidArgs(String message, Object details) {
        return new FlutterError("INVALID_ARGS", message, details);
    }

    public static FlutterError invalidResult(String message, Object details) {
        return new FlutterError("INVALID_RESULT", message, details);
    }

    public static FlutterError apiUnavailable(String message, Object details) {
        return new FlutterError("API_UNAVAILABLE", message, details);
    }

    public static FlutterError interrupted(String message, Object details) {
        return new FlutterError("INTERRUPTED", message, details);
    }

    public static FlutterError noConnection(String message, Object details) {
        return new FlutterError("NO_CONNECTION", message, details);
    }

    public static FlutterError apiError(String message, VKError error) {
        return new FlutterError("API_ERROR", message, Results.error(error));
    }

    public FlutterError(String code, String message, Object details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }
}
