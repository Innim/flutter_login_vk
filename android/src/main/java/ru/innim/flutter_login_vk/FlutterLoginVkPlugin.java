package ru.innim.flutter_login_vk;

import android.content.Context;

import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;

import io.flutter.Log;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * FlutterLoginVkPlugin
 */
public class FlutterLoginVkPlugin implements FlutterPlugin, ActivityAware {
    private static final String _CHANNEL_NAME = "flutter_login_vk";

    private MethodChannel _dartChannel;
    private LoginCallback _loginCallback;
    private MethodCallHandler _methodCallHandler;
    private ActivityListener _activityListener;
    private ActivityPluginBinding _activityPluginBinding;

    // This static function is optional and equivalent to onAttachedToEngine. It supports the old
    // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
    // plugin registration via this function while apps migrate to use the new Android APIs
    // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
    public static void registerWith(Registrar registrar) {
        // TODO: Initialization SDK.
        VKSdk.customInitialize(registrar.context(), 7428672, "");
        final MethodChannel channel = new MethodChannel(registrar.messenger(), _CHANNEL_NAME);
        final LoginCallback loginCallback = new LoginCallback();
        final MethodCallHandler handler = new MethodCallHandler(loginCallback);
        registrar.addActivityResultListener(new ActivityListener(loginCallback));
        handler.updateActivity(registrar.activity());
        channel.setMethodCallHandler(handler);
    }

    @Override
    public void onAttachedToEngine(FlutterPluginBinding flutterPluginBinding) {
        // TODO: Initialization SDK.
        VKSdk.customInitialize(flutterPluginBinding.getApplicationContext(), 7428672, "");
        final BinaryMessenger messenger = flutterPluginBinding.getBinaryMessenger();
        _dartChannel = new MethodChannel(messenger, _CHANNEL_NAME);
        _loginCallback = new LoginCallback();
        _methodCallHandler = new MethodCallHandler(_loginCallback);
        _activityListener = new ActivityListener(_loginCallback);
        _dartChannel.setMethodCallHandler(_methodCallHandler);
    }

    @Override
    public void onAttachedToActivity(ActivityPluginBinding activityPluginBinding) {
        _setActivity(activityPluginBinding);
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        _resetActivity();
    }

    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding activityPluginBinding) {
        _setActivity(activityPluginBinding);
    }

    @Override
    public void onDetachedFromActivity() {
        _resetActivity();
    }


    @Override
    public void onDetachedFromEngine(FlutterPluginBinding binding) {
        _methodCallHandler = null;
        _loginCallback = null;
        _dartChannel.setMethodCallHandler(null);
        _activityPluginBinding = null;
        _activityListener = null;
    }

    private void _setActivity(ActivityPluginBinding activityPluginBinding) {
        _methodCallHandler.updateActivity(activityPluginBinding.getActivity());
        activityPluginBinding.addActivityResultListener(_activityListener);
        _activityPluginBinding = activityPluginBinding;
    }

    private void _resetActivity() {
        if (_activityPluginBinding != null) {
            _activityPluginBinding.removeActivityResultListener(_activityListener);
            _activityPluginBinding = null;
            _methodCallHandler.updateActivity(null);
        }
    }
}
