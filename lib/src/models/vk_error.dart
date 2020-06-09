/// Error object from VK.
class VKError {
  /// VK API error code, if presented.
  ///
  /// See https://vk.com/dev/errors
  final int apiCode;

  // TODO: rename message to description?

  /// Error description, if presented.
  ///
  /// It' message for developer.
  final String message;

  // TODO @ivan: add in android
  /// Localized error message. Can be shown to user.
  final String localizedMessage;

  VKError({this.apiCode, this.message, this.localizedMessage});

  factory VKError.fromMap(Map<String, dynamic> map) => VKError(
        apiCode: map['apiCode'],
        message: map['message'],
        localizedMessage: map['localizedMessage'],
      );

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'apiCode': apiCode,
      'message': message,
      'localizedMessage': localizedMessage,
    };
  }

  @override
  bool operator ==(Object o) {
    if (identical(this, o)) return true;

    return o is VKError &&
        o.apiCode == apiCode &&
        o.message == message &&
        o.localizedMessage == localizedMessage;
  }

  @override
  int get hashCode =>
      apiCode.hashCode ^ message.hashCode ^ localizedMessage.hashCode;

  @override
  String toString() => 'VKError(apiCode: $apiCode, '
      'message: $message, localizedMessage: $localizedMessage)';
}
