import 'package:flutter_login_vk/flutter_login_vk.dart';

/// Login status.
enum VKLoginStatus { Success, Cancel, Error }

/// Result for login request.
class VKLoginResult {
  final VKLoginStatus status;
  final VKAccessToken accessToken;

  VKLoginResult(this.status, this.accessToken)
      : assert(status != null),
        assert(accessToken != null);

  VKLoginResult.fromMap(Map<String, dynamic> map)
      : status = _parseStatus(map['status']),
        accessToken = map['accessToken'] != null
            ? VKAccessToken.fromMap(map['accessToken'].cast<String, dynamic>())
            : null;

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'status': status.toString().split('.').last,
      'accessToken': accessToken,
    };
  }

  static VKLoginStatus _parseStatus(String status) {
    switch (status) {
      case 'Success':
        return VKLoginStatus.Success;
      case 'Cancel':
        return VKLoginStatus.Cancel;
      case 'Error':
        return VKLoginStatus.Error;
    }

    throw StateError('Invalid status: $status');
  }

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is VKLoginResult &&
          runtimeType == other.runtimeType &&
          status == other.status &&
          accessToken == other.accessToken;

  @override
  int get hashCode => status.hashCode ^ accessToken?.hashCode ?? 0;

  @override
  String toString() =>
      'VKLoginResult(status: $status, accessToken: $accessToken)';
}
