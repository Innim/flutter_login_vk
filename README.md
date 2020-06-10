# flutter_login_vk

[![pub package](https://img.shields.io/pub/v/flutter_login_vk)](https://pub.dartlang.org/packages/flutter_login_vk)

Flutter Plugin to login via VK.com.

Easily add VK login feature in your application. User profile information included.

## SDK version 

VKSDK version, used in plugin:

* iOS: **^1.5** ([CocoaPods](https://cocoapods.org/pods/VK-ios-sdk))
* Android: **^2.2** ([Maven](https://search.maven.org/artifact/com.vk/androidsdk))

## Minimum requirements

* iOS **9.0** and higher.
* Android **4.1** and newer (SDK **16**).

## Getting Started

To use this plugin:

 1. add `flutter_login_vk` as a [dependency in your pubspec.yaml file](https://pub.dev/packages/flutter_login_vk#-installing-tab-);
 2. [create an app on VK.com](#app-on-vk.com)
 3. [setup android](#android);
 4. [setup ios](#ios);
 5. [additional VK.com app setup](#additional-vk.com-app-setup);
 8. [use plugin in application](#usage-in-application).

See documentation on VK.com for full information:
* [iOS SDK](https://vk.com/dev/ios_sdk)
* [Android SDK](https://vk.com/dev/android_sdk)

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
with it's own cerificate (it may be auto generated debug cetificate or some another) than you should add it's key too.

4. Click "Save".

#### App settings for iOS 

1. Add your Bundle Identifier - set `App Bundle ID for iOS` (you can find it in Xcode: Runner - Target Runner - General, section `Identity`, field `Bundle Identifier`).
2. Also set `App ID for iOS`, it's you `SKU` (you can find it in [App Store Connect](https://appstoreconnect.apple.com/WebObjects/iTunesConnect.woa/ra/ng/app/1489717129): My Apps - {Your application} - App Store - App Information, section "General Information"). Mostly often is't the same as bundle ID.
3. Click "Save".

### Android

TODO

### iOS

TODO

### Additional VK.com app setup

TODO

### Usage-in-application

TODO


3. Enter "Description".
4. Select a suitable "Type" and "Category".

Application on

Icons 