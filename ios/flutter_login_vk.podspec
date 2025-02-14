#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html.
# Run `pod lib lint flutter_login_vk.podspec' to validate before publishing.
#
Pod::Spec.new do |s|
  s.name             = 'flutter_login_vk'
  s.version          = '3.0.0'
  s.summary          = 'Flutter login via VK.'
  s.description      = <<-DESC
Login via VK.com for a Flutter app.
                       DESC
  s.homepage         = 'https://github.com/Innim/flutter_login_vk'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Innim' => 'developer@innim.ru' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.dependency 'Flutter'
  s.dependency 'VK-ios-sdk', '~> 1.6.2'
  s.platform = :ios, '9.0'

  # Flutter.framework does not contain a i386 slice. Only x86_64 simulators are supported.
  s.pod_target_xcconfig = { 'DEFINES_MODULE' => 'YES', 'VALID_ARCHS[sdk=iphonesimulator*]' => 'x86_64' }
  s.swift_version = '5.0'
end
