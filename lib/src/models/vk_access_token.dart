/// VK access token.
class VKAccessToken {
  final String token;
  final int expiresIn;
  final String userId;
  final String secret;
  final bool httpsRequired;
  final DateTime created;
  final String email;

  VKAccessToken.fromMap(Map<String, dynamic> map)
      : token = map['token'],
        userId = map['userId'],
        expiresIn = map['expiresIn'],
        created =
            DateTime.fromMillisecondsSinceEpoch(map['created'], isUtc: true),
        secret = map['secret'],
        email = map['email'],
        httpsRequired = map['httpsRequired'];

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'token': token,
      'userId': userId,
      'expiresIn': expiresIn,
      'created': created.millisecondsSinceEpoch,
      'secret': secret,
      'email': email,
      'httpsRequired': httpsRequired,
    };
  }

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is VKAccessToken &&
          token == other.token &&
          userId == other.userId &&
          expiresIn == other.expiresIn &&
          created == other.created &&
          secret == other.secret &&
          email == other.email &&
          httpsRequired == other.httpsRequired;

  @override
  int get hashCode =>
      token.hashCode ^
      userId.hashCode ^
      expiresIn.hashCode ^
      created.hashCode ^
      secret.hashCode ^
      email.hashCode ^
      httpsRequired.hashCode;

  @override
  String toString() {
    return 'VKAccessToken(token: $token, userId: $userId, '
        'expiresIn: $expiresIn, created: $created, '
        'secret: $secret, email: $email, httpsRequired: $httpsRequired)';
  }
}
