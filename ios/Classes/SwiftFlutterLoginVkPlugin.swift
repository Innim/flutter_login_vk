import Flutter
import UIKit
import VK_ios_sdk

/// Plugin methods.
enum PluginMethod: String {
    case initSdk, logIn, logOut, getAccessToken, getUserProfile, getSdkVersion
}

/// Arguments for method `PluginMethod.initSdk`
enum InitSdkArg: String {
    case appId, apiVersion, scope
}

/// Arguments for method `PluginMethod.logIn`
enum LogInArg: String {
    case scope
}

public class SwiftFlutterLoginVkPlugin: NSObject, FlutterPlugin {
    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "flutter_login_vk", binaryMessenger: registrar.messenger())
        let instance = SwiftFlutterLoginVkPlugin()
        
        registrar.addMethodCallDelegate(instance, channel: channel)
        registrar.addApplicationDelegate(instance)
    }
    
    private lazy var _uiDelegate = VkUIDelegate()
    private lazy var _loginDelegate = VkLogInDelegate()
    private var _sdk: VKSdk?
    
    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        guard let method = PluginMethod(rawValue: call.method) else {
            result(FlutterMethodNotImplemented)
            return
        }
        
        switch method {
        case .initSdk:
            guard
                let args = call.arguments as? [String: Any],
                let appIdArg = args[InitSdkArg.appId.rawValue] as? String
                else {
                    result(FlutterError.invalidArgs("Arguments is invalid"))
                    return
            }

            let apiVersionArg = args[InitSdkArg.apiVersion.rawValue] as? String
            let permissionsArg = args[InitSdkArg.scope.rawValue] as? [String]
            
            initSdk(result: result, appId: appIdArg, apiVersion: apiVersionArg,
                permissions: permissionsArg)
        case .logIn:
            guard
                let args = call.arguments as? [String: Any],
                let permissionsArg = args[LogInArg.scope.rawValue] as? [String]
                else {
                    result(FlutterError.invalidArgs("Arguments is invalid"))
                    return
            }
            
            logIn(result: result, permissions: permissionsArg)
        case .logOut:
            logOut(result: result)
        case .getAccessToken:
            getAccessToken(result: result)
        case .getUserProfile:
            getUserProfile(result: result)
        case .getSdkVersion:
            getSdkVersion(result: result)
        }
    }
    
    // Application delegate
    
    public func application(_ application: UIApplication, open url: URL,
                            options: [UIApplication.OpenURLOptionsKey : Any] = [:]) -> Bool {
        VKSdk.processOpen(
            url,
            fromApplication: options[UIApplication.OpenURLOptionsKey.sourceApplication] as? String)
        
        return true;
    }
    
    // Plugin methods impl
    
    private func initSdk(result: @escaping FlutterResult, appId: String, apiVersion: String?,
                         permissions: [String]?) {
        if let prevSdk = _sdk {
            if prevSdk.currentAppId == appId {
                result(true)
                return
            }
            
            prevSdk.unregisterDelegate(_loginDelegate)
            prevSdk.uiDelegate = nil
        }
        
        let sdk = apiVersion == nil
            ? VKSdk.initialize(withAppId: appId)!
            : VKSdk.initialize(withAppId: appId, apiVersion: apiVersion)!
        _sdk = sdk
        
        sdk.uiDelegate = _uiDelegate
        sdk.register(_loginDelegate)
        
        VKSdk.wakeUpSession(permissions) { state, error in
            switch state {
            case .initialized, .authorized:
                result(true)
            case .pending, .external, .safariInApp, .webview:
                // Initialization complete, but log in still in progress
                self._loginDelegate.waitForInit(result: result)
            case .unknown, .error:
                result(FlutterError.byError(
                    message: "Init failed. State: \(state)", error: error))
            @unknown default:
                result(FlutterError.byError(
                    message: "Init failed with unhandled state: \(state)", error: error))
            }
        }
        
        result(true)
    }
    
    private func logIn(result: @escaping FlutterResult, permissions: [String]) {
        _loginDelegate.startLogin(result: result)
        VKSdk.authorize(permissions)
    }
    
    private func logOut(result: @escaping FlutterResult) {
        VKSdk.forceLogout()
        _sdk = nil
        result(nil)
    }
    
    private func getAccessToken(result: @escaping FlutterResult) {
        let token = VKSdk.accessToken()
        result(token?.toMap())
    }
    
    private func getUserProfile(result: @escaping FlutterResult) {
        let params: [String: Any] = [
            VK_API_FIELDS: ["first_name", "last_name", "online",
                            "photo_50", "photo_100", "photo_200"],
        ]
        
        guard let request = VKApi.users()?.get(params) else {
            result(FlutterError.apiUnavailable("Can't get user data"))
            return
        }
        
        request.execute(resultBlock: { response in
            guard
                let list = response?.parsedModel as? VKUsersArray,
                let user = list[0]
                else {
                    result(FlutterError.invalidResult("Can't parse get users response"))
                    return
            }
            
            let data: [String: Any?] = [
                "userId": user.id,
                "firstName": user.first_name,
                "lastName": user.last_name,
                "online": user.online?.toBool(),
                "onlineMobile": user.online_mobile?.toBool(),
                "photo50": user.photo_50,
                "photo100": user.photo_100,
                "photo200": user.photo_200,
            ]
            
            result(data)
        }, errorBlock: { error in
            // TODO: pass error data as details?
            result(FlutterError.invalidResult(
                "Get profile error: \(String(describing: error))"))
        })
    }
    
    private func getSdkVersion(result: @escaping FlutterResult) {
        result(VK_SDK_VERSION)
    }
}

class VkUIDelegate : NSObject, VKSdkUIDelegate {
    private var rootViewController: UIViewController? {
        get {
            let app = UIApplication.shared
            return app.delegate?.window??.rootViewController
        }
    }
    
    func vkSdkShouldPresent(_ controller: UIViewController!) {
        guard let vc = rootViewController else {
            // TODO: log error
            return
        }
        
        vc.present(controller, animated: true)
    }
    
    func vkSdkNeedCaptchaEnter(_ captchaError: VKError!) {
        guard let vc = rootViewController else {
            // TODO: log error
            return
        }
        
        let controller = VKCaptchaViewController.captchaControllerWithError(captchaError)!
        controller.present(in: vc)
    }
}

class VkLogInDelegate : NSObject, VKSdkDelegate {
    private var _pendingLoginResult: FlutterResult?
    private var _pendingInitResult: FlutterResult?
    
    func startLogin(result: @escaping FlutterResult) {
        if let prevResult = _pendingLoginResult {
            prevResult(FlutterError.interrupted("Interrupted by another login call"))
        }
        
        _pendingLoginResult = result
    }
    
    func waitForInit(result: @escaping FlutterResult) {
        if let prevResult = _pendingInitResult {
            prevResult(FlutterError.interrupted("Interrupted by another init call"))
        }
        
        _pendingInitResult = result
    }
    
    func vkSdkUserAuthorizationFailed() {
        // TODO: should notify application
        print("vkSdkUserAuthorizationFailed")
    }
    
    // VKSdkDelegate
    
    func vkSdkAccessAuthorizationFinished(with result: VKAuthorizationResult!) {
        if let pendingResult = _pendingLoginResult {
            _pendingLoginResult = nil
            let data: [String: Any];
            if let token = result.token {
                data = [
                    "accessToken": token.toMap()
                ]
            } else if let error = result.error {
                let nsError = error as NSError
                if nsError.domain == VKSdkErrorDomain, let vkError = nsError.vkError {
                    if vkError.isCanceled() {
                        data = [
                            "isCanceled": true
                        ]
                    } else {
                        pendingResult(FlutterError.apiError(
                            "Login failed: \(vkError.errorMessage ?? "nil")",
                            error: vkError))
                        return
                    }
                    
                    //                } else if nsError.domain == NSURLErrorDomain && nsError.code == NSURLErrorNotConnectedToInternet {
                    //                    // TODO: handle NSURLErrorNotConnectedToInternet
                } else {
                    pendingResult(FlutterError.invalidResult(
                        "Invalid login error: \(String(describing: error))"))
                    return
                }
            } else {
                pendingResult(FlutterError.invalidResult(
                    "Invalid login result: \(String(describing: result))"))
                return;
            }
            
            pendingResult(data)
        } else if let pendingResult = _pendingInitResult {
            // it's auto auth from wakeUpSession(), without authorize() call
            
            // We don't need result here, just a fact that it's done
            _pendingInitResult = nil
            pendingResult(true)
        }
    }
}

extension VKAccessToken {
    func toMap() -> [String: Any?] {
        return [
            "token": accessToken,
            "userId": userId,
            "expiresIn": expiresIn,
            "created": Int((created * 1000.0).rounded()),
            "secret": secret,
            "email": email,
            "httpsRequired": httpsRequired,
            "permissions": permissions,
        ]
    }
}

extension VKError {
    func toMap() -> [String: Any?] {
        let apiCode: Int?
        let message: String?
        let localizedMessage: String?
        
        if errorCode == VK_API_ERROR, let apiError = self.apiError {
            apiCode = apiError.errorCode
            message = apiError.errorMessage
            localizedMessage = apiError.errorText
        } else {
            apiCode = nil
            message = errorMessage
            localizedMessage = errorText
        }
        
        return [
            "apiCode": apiCode,
            "message": message,
            "localizedMessage": localizedMessage,
        ]
    }
    
    func isCanceled() -> Bool {
        return errorCode == VK_API_CANCELED ||
            errorCode == VK_API_ERROR && errorReason == "user_denied"
    }
}

extension FlutterError {
    static func invalidArgs(_ message: String, details: Any? = nil) -> FlutterError {
        return FlutterError(code: "INVALID_ARGS", message: message, details: details);
    }
    
    static func invalidResult(_ message: String, details: Any? = nil) -> FlutterError {
        return FlutterError(code: "INVALID_RESULT", message: message, details: details);
    }
    
    static func apiUnavailable(_ message: String, details: Any? = nil) -> FlutterError {
        return FlutterError(code: "API_UNAVAILABLE", message: message, details: details);
    }
    
    /// Interrupted. For example by another call.
    static func interrupted(_ message: String, details: Any? = nil) -> FlutterError {
        return FlutterError(code: "INTERRUPTED", message: message, details: details);
    }
    
    /// Error as result of SDK API call.
    static func apiError(_ message: String, error: VKError) -> FlutterError {
        return FlutterError(code: "API_ERROR", message: message, details: error.toMap());
    }
    
    static func byError(message: String, error: Error?) -> FlutterError {
        if error != nil {
            let nsError = error! as NSError
            if nsError.domain == VKSdkErrorDomain, let vkError = nsError.vkError {
                return FlutterError.apiError(message, error: vkError)
            } else {
                return FlutterError.invalidResult(
                    "\(message). Error: \(nsError)")
            }
        } else {
            return FlutterError.invalidResult("\(message). No error provided.")
        }
    }
}

extension NSNumber {
    func toBool() -> Bool {
        return self == 1
    }
}
