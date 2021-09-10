# flutter_login_vk

[![pub package](https://img.shields.io/pub/v/flutter_login_vk)](https://pub.dartlang.org/packages/flutter_login_vk)

Flutter Plugin to login via VK.com.

Easily add VK login feature in your application. User profile information included.

## SDK version 

VK SDK version, used in plugin:

* iOS: **^1.6** ([CocoaPods](https://cocoapods.org/pods/VK-ios-sdk))
* Android: **1.6.7** ([Maven](https://search.maven.org/artifact/com.vk/androidsdk))

## Minimum requirements

* iOS **9.0** and higher.
* Android **4.1** and newer (SDK **21**).

⚠️  If your project was create with Flutter pre **1.12** you should upgrade it to the Android embedding v2.
See https://flutter.dev/go/android-project-migration

## Getting Started

To use this plugin:

 1. add `flutter_login_vk` as a [dependency in your pubspec.yaml file](https://pub.dev/packages/flutter_login_vk#-installing-tab-);
 2. [create an app on VK.com](#app-on-vkcom)
 3. [setup android](#android);
 4. [setup ios](#ios);
 5. [additional VK.com app setup](#additional-vk.com-app-setup);
 8. [use plugin in application](#usage-in-application).

See documentation on VK.com for full information:
* [iOS SDK](https://vk.com/dev/ios_sdk)
* [Android SDK](https://vk.com/dev/android_sdk)

And here is [instructions in Russian](https://syntaxerror.ru/login-via-vk-in-flutter-app/) if it's your native language ([русская версия](https://syntaxerror.ru/login-via-vk-in-flutter-app/)).

### App on VK.com

Create an app on VK.com https://vk.com/editapp?act=create

1. Enter "Title".
2. Select **Standalone app** as "Platform".
3. Click "Connect app".

An application will be created. Now select tab "Settings" and copy "App ID"
(referenced as `[APP_ID]` in this readme).

#### App settings for Android

1. Set `Package name for Android` - your package name for Android application (attribute `package` in `AndroidManifest.xml`).
2. Set `Main activity for Android` - your main activity class (with package). By default it would be `com.yourcompany.yourapp.MainActivity`.
3. To fill up `Signing certificate fingerprint for Android` you should create SHA1 fingerprint
as described in the [documentation](https://vk.com/dev/android_sdk?f=1.1.%20Certificate%20Fingerprint) 
(without `SHA1: ` prefix).
Add fingerprints for debug and release certificates. *Note:* if your application uses [Google Play App Signing](https://support.google.com/googleplay/android-developer/answer/7384423) than you should get certificate SHA-1 fingerprint from Google Play Console.

    ⚠️ **Important!** You should add fingerprints for every build variants. E.g. if you have CI/CD which build APK for testing
with it's own cerificate (it may be auto generated debug cetificate or some another) than you should add it's fingerprint too.

4. Click "Save".

#### App settings for iOS 

1. Add your Bundle Identifier - set `App Bundle ID for iOS` (you can find it in Xcode: Runner - Target Runner - General, section `Identity`, field `Bundle Identifier`).
2. Also set `App ID for iOS`, it's you `SKU` (you can find it in [App Store Connect](https://appstoreconnect.apple.com/WebObjects/iTunesConnect.woa/ra/ng/app/1489717129): My Apps - {Your application} - App Store - App Information, section "General Information"). Mostly often is't the same as bundle ID.
3. Click "Save".

### Android

Edit `AndroidManifest.xml` (`android/app/src/main/AndroidManifest.xml`):

1. Add the `INTERNET` permission in the root of `<manifest>`, if you haven't (probably you have):
```xml
<uses-permission android:name="android.permission.INTERNET" />
```
2. Add an activity to the section `application`:
```xml
<activity android:name="com.vk.sdk.VKServiceActivity"
    android:label="ServiceActivity"
    android:theme="@style/VK.Transparent" />
```
3. Add your VK application identifier to the resource file (e.g. `strings.xml`),
replacing `[APP_ID]` with your application id:
```xml
<resources>
    <integer name="com_vk_sdk_AppId">[APP_ID]</integer>
</resources>
```

See full `AndroidManifest.xml` in [example](example/android/app/src/main/AndroidManifest.xml).

### iOS

Configure `Info.plist` (`ios/Runner/Info.plist`).
You can edit it as a text file from your IDE,
or you can open project (`ios/Runner.xcworkspace`) in Xcode.

1. In Xcode right-click on `Info.plist`, and choose `Open As Source Code`.
2. Copy and paste the following XML snippet into the body of your file (`<dict>...</dict>`),
replacing `[APP_ID]` with your application id:
```xml
<key>CFBundleURLTypes</key>
<array>
  <dict>
  <key>CFBundleURLSchemes</key>
  <array>
    <string>vk[APP_ID]</string>
  </array>
  </dict>
</array>
```
3. Also add to `Info.plist` body (`<dict>...</dict>`):
```xml
<key>LSApplicationQueriesSchemes</key> 
<array> 
    <string>vk</string> 
    <string>vk-share</string> 
    <string>vkauthorize</string> 
</array>
```
4. Enter your VK application identifier.
```xml
<key>VKAppId</key>
<string>[APP_ID]</string>
```

See full `Info.plist` in [example](example/ios/Runner/Info.plist).

⚠️ **NOTE.** Check if you already have `CFBundleURLTypes` or `LSApplicationQueriesSchemes` keys in your `Info.plist`. If you have, you should merge their values, instead of adding a duplicate key.

If you want to use `scope=nohttps`, which we strongly **do not recommend**, you should also add `NSAppTransportSecurity`,
see the [documentation](https://vk.com/dev/ios_sdk?f=1.2.%20Изменения%20для%20iOS%209).


### Additional VK.com app setup

Go to [My Apps](https://vk.com/apps?act=manage) and click "Manage" on your app.

On tab "Information" you should:
1. Enter "Description".
2. Select a suitable "Category".
3. Upload small icon "32x32 icon".
4. Click "Save".
5. Upload "Square banner" and "A square banner for catalog" - user can see it.

Setup other settings if you need it.

Than go to "Setting" tab and turn on application: 
change "App status" from `Application off` to `Application on and visible to all`.

Click "Save".

### Usage in application

First, you should create an instance of `VKLogin`. 
Than, before any method call or checking `accessToken` you should initialize VK SDK:

```dart
final vk = VKLogin();
await vk.initSdk();
```

Now you can use the plugin.

Features:
* log in via VK.com;
* get access token;
* get user profile;
* get user email;
* check if logged in;
* log out.

Sample code:

```dart
import 'package:flutter_login_vk/flutter_login_vk.dart';

// Create an instance of VKLogin
final vk = VKLogin();

// Initialize
await vk.initSdk();

// Log in
final res = await vk.logIn(permissions: [
  VKScope.email,
  VKScope.friends,
]);

// Check result
if (res.isValue) {
    // There is no error, but we don't know yet
    // if user loggen in or not.
    // You should check isCanceled
    final VKLoginResult data = res.asValue.value;

    if (res.isCanceled) {
        // User cancel log in
    } else {
        // Logged in

        // Send access token to server for validation and auth
        final VKAccessToken accessToken = res.accessToken;
        print('Access token: ${accessToken.token}');
    
        // Get profile data
        final profile = await fb.getUserProfile();
        print('Hello, ${profile.firstName}! You ID: ${profile.userId}');

        // Get email (since we request email permissions)
        final email = await fb.getUserEmail();
        print('And your email is $email');
    }
} else {
    // Log in failed
    final errorRes = res.asError;
    print('Error while log in: ${errorRes.error}');
}
```

#### Initialization notes

When you call `initSdk()`, plugin try to restore previous session.
If token has been expired - it will be refreshed. 

Also, during restoring, log in screen may be shown to user
(only if user was logged in).

In additional, you can pass to `initSdk()` required `scope`,
and if current user session doesn't provide it - user will be
logged out.

Also you can specify API version to use, but you shouldn't.