import 'package:async/async.dart';
import 'package:flutter/services.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_login_vk/flutter_login_vk.dart';
import 'package:flutter_login_vk/src/models/vk_result.dart';

import 'models/vk_scope.dart';

/// Class for implementing login via VK.
class VKLogin {
  // Methods
  static const _methodLogIn = "logIn";
  static const _methodLogOut = "logOut";
  static const _methodGetAccessToken = "getAccessToken";
  static const _methodGetUserProfile = "getUserProfile";
  static const _methodGetSdkVersion = "getSdkVersion";

  static const _scopeArg = "scope";

  static const MethodChannel _channel = const MethodChannel('flutter_login_vk');

  /// If `true` all requests and results will be printed in console.
  final bool debug;

  VKLogin({this.debug = false}) : assert(debug != null) {
    //if (debug) sdkVersion.then((v) => _log('SDK version: $v'));
  }

  /// Returns access token if user logged in.
  ///
  /// If user is now logged in, than returns `null`.
  Future<VKAccessToken> get accessToken async {
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

  Future<bool> get isLoggedIn async {
    final token = await accessToken;
    return _isLoggedIn(token);
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
  Future<VKResult<VKLoginResult>> logIn(
      {List<VKScope> scope = const [], List<String> customScope}) async {
    assert(scope != null);

    final scopeArg = scope.map((e) => e.name).toList();
    if (customScope != null) scopeArg.addAll(customScope);

    if (debug) _log('Log In with scope $scopeArg');
    final Map<dynamic, dynamic> loginResultData =
        await _channel.invokeMethod(_methodLogIn, {_scopeArg: scopeArg});

    if (debug) _log('Result: $loginResultData');
    final l = loginResultData['login'];
    return result(
        l != null ? VKLoginResult.fromMap(l.cast<String, dynamic>()) : null,
        loginResultData);
  }

  Future<void> logOut() {
    if (debug) _log('Log Out');
    return _channel.invokeMethod(_methodLogOut);
  }

  VKResult<T> result<T>(T data, Map<dynamic, dynamic> dataWithError) {
    final e = dataWithError['error'];
    return VKResult<T>(
        data: data,
        error: e != null ? VKError.fromMap(e.cast<String, dynamic>()) : null);
  }

  bool _isLoggedIn(VKAccessToken token) => token != null;

  void _log(String message) {
    if (debug) debugPrint('[VK] $message');
  }
}
