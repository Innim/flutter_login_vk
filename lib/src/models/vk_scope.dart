import 'package:flutter/foundation.dart';

enum VKScope {
  friends,
  photos,
  audio,
  video,
  stories,
  pages,
  status,
  notes,
  messages,
  wall,
  ads,
  offline,
  docs,
  groups,
  notifications,
  stats,
  email,
  market
}

extension VKScopeExtension on VKScope {
  /// Name of scope.
  String get name => describeEnum(this);
}
