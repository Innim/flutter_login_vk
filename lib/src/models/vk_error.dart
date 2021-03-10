/// Error object from VK.
class VKError {
  /// VK API error code, if presented.
  ///
  /// See https://vk.com/dev/errors
  final int? apiCode;

  // TODO: rename message to description?

  /// Error description, if presented.
  ///
  /// It' message for developer.
  final String? message;

  // TODO: Current Android version don't support it.
  /// Localized error message. Can be shown to user.
  final String? localizedMessage;

  VKError({this.apiCode, this.message, this.localizedMessage});

  factory VKError.fromMap(Map<String, dynamic> map) => VKError(
        apiCode: map['apiCode'] as int?,
        message: map['message'] as String?,
        localizedMessage: map['localizedMessage'] as String?,
      );

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'apiCode': apiCode,
      'message': message,
      'localizedMessage': localizedMessage,
    };
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;

    return other is VKError &&
        other.apiCode == apiCode &&
        other.message == message &&
        other.localizedMessage == localizedMessage;
  }

  @override
  int get hashCode =>
      apiCode.hashCode ^ message.hashCode ^ localizedMessage.hashCode;

  @override
  String toString() => 'VKError(apiCode: $apiCode, '
      'message: $message, localizedMessage: $localizedMessage)';
}
