## Next release

## 2.0.0 - 2021-09-09

* **BREAKING** Method `initSdk()` does not accept `appId` and `apiVersion`, you need to define it in `strings.xml`/`Info.plist`, see [README](./README.md).
* **BREAKING** Removed fields `httpsRequired ` and `expiresIn` from `VKAccessToken`.
* Format of the `appID` task has changed, see suggestion for the first item.
* [Android] Update vk-android-sdk to 3.2.2.
* [Android] Up your `minSdkVersion` to 21.
* Change version async to 2.5.0.

## 1.0.1 - 2021-08-03

* [iOS] Update VK-ios-sdk to 1.6.2 
(you may need to run `cd ios && pod update VK-ios-sdk` in your project's folder, to update dependencies).
* Update async to 2.8.1.
* Update innim_lint to 0.2.0.

Thanks to [@otopba](https://github.com/otopba).

## 1.0.0 - 2021-03-10

* Migrate to null safety.
* Flutter 2.

## 0.2.1 - 2021-02-12

* Added `innim_lint`. Refactoring.

## 0.2.0 - 2020-11-17

* Flutter 1.22.
* iOS VK SDK upgraded to the ^1.6.
* **Fixed**: Build failed on iOS with VK SDK 1.6 (Issue [#2](https://github.com/Innim/flutter_login_vk/issues/2)).

## 0.1.1 - 2020-08-06

* **Fixed**: Crash on iOS 10.

## 0.1.0+3 - 2020-07-04

* Readme: Upgrade to the Android embedding v2.
* Readme: Typos and fixes.
* Readme: Link to instruction in Russian.

## 0.1.0+1 - 2020-06-15

* Initial release: log in, get profile information, get email, log out.
