/// Error object from VK.
///
/// See https://vk.com/dev/ios_sdk?f=5.3.%20%D0%9E%D0%B1%D1%80%D0%B0%D0%B1%D0%BE%D1%82%D0%BA%D0%B0%20%D0%BE%D1%88%D0%B8%D0%B1%D0%BE%D0%BA
class VKError {
  final String code;
  final int apiCode;
  final String message;

  VKError({this.code, this.apiCode, this.message});

  factory VKError.fromMap(Map<String, dynamic> map) => VKError(
      code: map['code'], apiCode: map['apiCode'], message: map['message']);

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'code': code,
      'apiCode': apiCode,
      'message': message,
    };
  }

  @override
  bool operator ==(Object o) {
    if (identical(this, o)) return true;

    return o is VKError &&
        o.code == code &&
        o.apiCode == apiCode &&
        o.message == message;
  }

  @override
  int get hashCode => code.hashCode ^ apiCode.hashCode ^ message.hashCode;

  @override
  String toString() => 'VKError(code: $code, apiCode: $apiCode, '
      'message: $message)';
}
