import 'package:flutter/cupertino.dart';
import 'package:flutter_login_vk/flutter_login_vk.dart';

/// Result operation.
class VKResult<T> {
  final T data;
  final VKError error;

  const VKResult({@required this.data, this.error});

  VKResult.fromMap(Map<String, dynamic> map)
      : data = map['data'],
        error = map['error'];

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'data': data,
      'error': error,
    };
  }

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is VKResult && data == other.data && error == other.error;

  @override
  int get hashCode => data?.hashCode ?? 0 ^ error?.hashCode ?? 0;

  @override
  String toString() {
    return 'VKResult(data: $data, error: $error)';
  }
}
