package ru.innim.flutter_login_vk;

public class VKError {
    final int errorCode;
    final String errorMsg;

    public VKError(int errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }
}
