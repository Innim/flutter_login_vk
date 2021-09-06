/// VK access token.
class VKAccessToken {
  final String token;
  final int userId;
  final String? secret;
  final bool isValid;
  final DateTime created;
  final String? email;

  VKAccessToken.fromMap(Map<String, dynamic> map)
      : token = map['token'] as String,
        userId = map['userId'] as int,
        created = DateTime.fromMillisecondsSinceEpoch(map['created'] as int,
            isUtc: true),
        secret = map['secret'] as String?,
        email = map['email'] as String?,
        isValid = map['isValid'] as bool? ?? false;

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'token': token,
      'userId': userId,
      'created': created.millisecondsSinceEpoch,
      'secret': secret,
      'email': email,
      'isValid': isValid,
    };
  }

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is VKAccessToken &&
          token == other.token &&
          userId == other.userId &&
          created == other.created &&
          secret == other.secret &&
          email == other.email &&
          isValid == other.isValid;

  @override
  int get hashCode =>
      token.hashCode ^
      userId.hashCode ^
      created.hashCode ^
      secret.hashCode ^
      isValid.hashCode ^
      email.hashCode;

  @override
  String toString() {
    return 'VKAccessToken(token: $token, userId: $userId, '
        'created: $created, secret: $secret, email: $email, '
        'isValid: $isValid)';
  }
}
