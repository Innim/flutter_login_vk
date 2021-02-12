import 'package:meta/meta.dart';
import 'package:flutter_login_vk/flutter_login_vk.dart';

/// Result for login request.
class VKLoginResult {
  /// `true` if log in process canceled by user.
  final bool isCanceled;

  /// Access token.
  ///
  /// `null` if user log in failed.
  final VKAccessToken accessToken;

  VKLoginResult(this.accessToken, {@required this.isCanceled})
      : assert(isCanceled != null);

  VKLoginResult.fromMap(Map<String, dynamic> map)
      : isCanceled = map['isCanceled'] as bool ?? false,
        accessToken = map['accessToken'] != null
            ? VKAccessToken.fromMap(
                (map['accessToken'] as Map<dynamic, dynamic>)
                    .cast<String, dynamic>())
            : null;

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'isCanceled': isCanceled,
      'accessToken': accessToken,
    };
  }

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is VKLoginResult &&
          runtimeType == other.runtimeType &&
          isCanceled == other.isCanceled &&
          accessToken == other.accessToken;

  @override
  int get hashCode => isCanceled.hashCode ^ accessToken?.hashCode ?? 0;

  @override
  String toString() =>
      'VKLoginResult(accessToken: $accessToken, isCanceled: $isCanceled)';
}
