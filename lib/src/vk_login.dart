import 'package:async/async.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_login_vk/flutter_login_vk.dart';

/// Class for implementing login via VK.
class VKLogin {
  // Methods
  static const _methodInitSdk = "initSdk";
  static const _methodLogIn = "logIn";
  static const _methodLogOut = "logOut";
  static const _methodGetAccessToken = "getAccessToken";
  static const _methodGetUserProfile = "getUserProfile";
  static const _methodGetSdkVersion = "getSdkVersion";

  // TODO: rename to `permissions`?
  static const _argLogInScope = "scope";

  static const _argInitSdkAppId = "appId";
  static const _argInitSdkApiVersion = "apiVersion";
  // TODO: rename to `permissions`?
  static const _argInitSdkScope = "scope";

  static const MethodChannel _channel = const MethodChannel('flutter_login_vk');

  /// If `true` all requests and results will be printed in console.
  final bool debug;

  bool _initialized = false;

  VKLogin({this.debug = false}) : assert(debug != null) {
    if (debug) sdkVersion.then((v) => _log('SDK version: $v'));
  }

  /// Return `true` if SDK initialized.
  bool get isInitialized => _initialized;

  /// Returns access token if user logged in.
  ///
  /// If user is now logged in, than returns `null`.
  Future<VKAccessToken> get accessToken async {
    assert(_initialized,
        'SDK is not initialized. You should call initSdk() first');
    if (!_initialized) return null;

    final Map<dynamic, dynamic> tokenResult =
        await _channel.invokeMethod(_methodGetAccessToken);

    return tokenResult != null
        ? VKAccessToken.fromMap(tokenResult.cast<String, dynamic>())
        : null;
  }

  /// Returns currently used VK SDK.
  Future<String> get sdkVersion async {
    final String res = await _channel.invokeMethod(_methodGetSdkVersion);
    return res;
  }

  // TODO: add get apiVersion

  Future<bool> get isLoggedIn async {
    final token = await accessToken;
    return _isLoggedIn(token);
  }

  /// Initialize SDK.
  ///
  /// Should call before any other method calls.
  ///
  /// You can pass [scope] (and/or [customScope], see [logIn])
  /// to require listed permissions. If user logged in,
  /// but doesn't have all of this permissions - he will be logged out.
  Future<Result> initSdk(String appId,
      {String apiVersion,
      List<VKScope> scope,
      List<String> customScope}) async {
    assert(appId != null);

    final scopeArg = _getScope(scope: scope, customScope: customScope);

    if (debug) {
      _log('initSdk with $appId' +
          (apiVersion != null ? ' (api vestion: $apiVersion)' : '') +
          (scopeArg != null ? '. Permissions: $scopeArg' : ''));
    }

    try {
      final bool result = await _channel.invokeMethod(_methodInitSdk, {
        _argInitSdkAppId: appId,
        _argInitSdkApiVersion: apiVersion,
        _argInitSdkScope: scopeArg,
      });

      if (result) {
        _initialized = true;
        return Result.value(true);
      } else {
        _initialized = false;
        return Result.error("Init SDK failed");
      }
    } on PlatformException catch (e) {
      if (debug) _log('Init SDK error: $e');
      return Result.error(e);
    }
  }

  /// Get user profile information.
  ///
  /// If not logged in, than return `null` as a result value.
  ///
  /// If error occure during request than method
  /// will return error result.
  Future<Result<VKUserProfile>> getUserProfile() async {
    if (await isLoggedIn == false) {
      if (debug) _log('Not logged in. User profile is null');
      return Result.value(null);
    }

    try {
      final Map<dynamic, dynamic> result =
          await _channel.invokeMethod(_methodGetUserProfile);

      if (debug) _log('User profile: $result');

      return Result.value(result != null
          ? VKUserProfile.fromMap(result.cast<String, dynamic>())
          : null);
    } on PlatformException catch (e) {
      if (debug) _log('Get profile error: $e');
      return Result.error(e);
    }
  }

  /// Get user email.
  ///
  /// Attention! User need to be logged in with
  /// accepted [VKScope.email] scope.
  ///
  /// If not logged in, decline [VKScope.email] scope than returns `null`.
  Future<String> getUserEmail() async {
    final token = await accessToken;
    if (!_isLoggedIn(token)) {
      if (debug) _log('Not logged in. Email is null');
      return null;
    }

    return token.email;
  }

  /// Start log in VK process.
  ///
  /// [scope] Array of scope.
  /// If required scope is not in enum [VKScope], than use [customScope].
  ///
  /// Value in not error result can't be null.
  ///
  /// If user cancel log in process, than value result will be returned,
  /// but value property [VKLoginResult.isCanceled] will be `true`
  /// and [VKLoginResult.accessToken] will be `null`.
  ///
  /// If error occure during log in process, than error result
  /// will be returned. And [Result.error] may
  Future<Result<VKLoginResult>> logIn(
      {List<VKScope> scope = const [], List<String> customScope}) async {
    assert(scope != null);

    assert(_initialized,
        'SDK is not initialized. You should call initSdk() first');
    if (!_initialized) throw Exception("SDK is not initialized.");

    final scopeArg = _getScope(scope: scope, customScope: customScope);

    if (debug) _log('Log In with scope $scopeArg');

    // TODO @ivan: change in Android implementation

    try {
      final Map<dynamic, dynamic> res =
          await _channel.invokeMethod(_methodLogIn, {_argLogInScope: scopeArg});

      if (res == null) {
        return Result.error("Invalid null result");
      } else {
        return Result.value(VKLoginResult.fromMap(res.cast<String, dynamic>()));
      }
    } on PlatformException catch (e) {
      if (debug) _log('Log In error: $e');
      return Result.error(e);
    }
  }

  Future<void> logOut() async {
    assert(_initialized,
        'SDK is not initialized. You should call initSdk() first');
    if (!_initialized) return;

    if (debug) _log('Log Out');
    await _channel.invokeMethod(_methodLogOut);
  }

  bool _isLoggedIn(VKAccessToken token) => token != null;

  List<String> _getScope({List<VKScope> scope, List<String> customScope}) {
    if (scope != null) {
      final scopeArg = scope.map((e) => e.name).toList();
      if (customScope != null) scopeArg.addAll(customScope);
      return scopeArg;
    } else if (customScope != null) {
      return List.from(customScope);
    } else {
      return null;
    }
  }

  void _log(String message) {
    if (debug) debugPrint('[VK] $message');
  }
}
