/// VK user profile.
class VKUserProfile {
  final int userId;
  final String firstName;
  final String lastName;
  final bool online;
  final bool onlineMobile;
  final String photo50;
  final String photo100;
  final String photo200;

  VKUserProfile.fromMap(Map<String, dynamic> map)
      : userId = map['userId'],
        firstName = map['firstName'],
        lastName = map['lastName'],
        online = map['online'],
        onlineMobile = map['onlineMobile'],
        photo50 = map['photo50'],
        photo100 = map['photo100'],
        photo200 = map['photo200'];

  Map<String, dynamic> toMap() {
    return <String, dynamic>{
      'userId': userId,
      'firstName': firstName,
      'lastName': lastName,
      'online': online,
      'onlineMobile': onlineMobile,
      'photo50': photo50,
      'photo100': photo100,
      'photo200': photo200,
    };
  }

  @override
  bool operator ==(Object other) =>
      identical(this, other) ||
      other is VKUserProfile &&
          runtimeType == other.runtimeType &&
          userId == other.userId &&
          firstName == other.firstName &&
          lastName == other.lastName &&
          online == other.online &&
          onlineMobile == other.onlineMobile &&
          photo50 == other.photo50 &&
          photo100 == other.photo100 &&
          photo200 == other.photo200;

  @override
  int get hashCode =>
      userId.hashCode ^
      firstName.hashCode ^
      lastName.hashCode ^
      online.hashCode ^
      onlineMobile.hashCode ^
      photo50.hashCode ^
      photo100.hashCode ^
      photo200.hashCode;

  @override
  String toString() {
    return 'VKUserProfile(userId: $userId, firstName: $firstName, '
        'lastName: $lastName, online: $online, onlineMobile: $onlineMobile, '
        'photo50: $photo50, photo100: $photo100, photo200: $photo200)';
  }
}
