import Flutter
import UIKit
import VK_ios_sdk

/// Plugin methods.
enum PluginMethod: String {
    case initSdk, logIn, logOut, getAccessToken, getUserProfile, getSdkVersion
}

/// Arguments for method `PluginMethod.initSdk`
enum InitSdkArg: String {
    case appId
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
            
            initSdk(result: result, appId: appIdArg)
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
    
    private func initSdk(result: @escaping FlutterResult, appId: String) {
        if _sdk != nil {
            if _sdk!.currentAppId == appId {
                result(true)
                return
            }
        
            // TODO: logout? dispose? remove delegates? error?
        }
        
        let sdk = VKSdk.initialize(withAppId: appId)!
        _sdk = sdk
    
        sdk.uiDelegate = _uiDelegate
        sdk.register(_loginDelegate)
        
        // TODO: wakeUpSession
        
        result(true)
    }
    
    private func logIn(result: @escaping FlutterResult, permissions: [String]) {
    }
    
    private func logOut(result: @escaping FlutterResult) {
        VKSdk.forceLogout()
        _sdk = nil
    }
    
    private func getAccessToken(result: @escaping FlutterResult) {
        let token = VKSdk.accessToken()
        result([
            "accessToken": token?.toMap(),
        ])
    }
    
    private func getUserProfile(result: @escaping FlutterResult) {
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
    private var _pendingResult: FlutterResult?
    
    func startLogin(result: @escaping FlutterResult) {
        // TODO: check if _pendingResult is not null
        _pendingResult = result
    }
    
    func vkSdkUserAuthorizationFailed() {
        // TODO: should notify application
        print("vkSdkUserAuthorizationFailed")
    }
    
    // VKSdkDelegate
    
    func vkSdkAccessAuthorizationFinished(with result: VKAuthorizationResult!) {
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
extension FlutterError {
    static func invalidArgs(_ message: String, details: Any? = nil) -> FlutterError {
        return FlutterError(code:  "INVALID_ARGS", message: message, details: details);
    }
    
    static func invalidResult(_ message: String, details: Any? = nil) -> FlutterError {
        return FlutterError(code:  "INVALID_RESULT", message: message, details: details);
    }
}
