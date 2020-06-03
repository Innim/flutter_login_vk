#import "FlutterLoginVkPlugin.h"
#if __has_include(<flutter_login_vk/flutter_login_vk-Swift.h>)
#import <flutter_login_vk/flutter_login_vk-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_login_vk-Swift.h"
#endif

@implementation FlutterLoginVkPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterLoginVkPlugin registerWithRegistrar:registrar];
}
@end
