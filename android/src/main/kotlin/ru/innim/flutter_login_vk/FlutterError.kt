package ru.innim.flutter_login_vk

class FlutterError(var code: String, var message: String?, var details: Any?) {
    companion object {
        fun invalidArgs(message: String, details: Any?): FlutterError {
            return FlutterError("INVALID_ARGS", message, details)
        }

        fun invalidResult(message: String, details: Any? = null): FlutterError {
            return FlutterError("INVALID_RESULT", message, details)
        }

        fun apiUnavailable(message: String, details: Any? = null): FlutterError {
            return FlutterError("API_UNAVAILABLE", message, details)
        }

        fun interrupted(message: String, details: Any? = null): FlutterError {
            return FlutterError("INTERRUPTED", message, details)
        }

        fun noConnection(message: String, details: Any? = null): FlutterError {
            return FlutterError("NO_CONNECTION", message, details)
        }

        fun apiError(message: String, error: VKError): FlutterError {
            return FlutterError("API_ERROR", message, Results.error(error))
        }
    }
}
