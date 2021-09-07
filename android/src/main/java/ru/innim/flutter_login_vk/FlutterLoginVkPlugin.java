package ru.innim.flutter_login_vk;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodChannel;

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

    @Override
    public void onAttachedToEngine(FlutterPluginBinding flutterPluginBinding) {
        final BinaryMessenger messenger = flutterPluginBinding.getBinaryMessenger();
        _dartChannel = new MethodChannel(messenger, _CHANNEL_NAME);
        _loginCallback = new LoginCallback();
        _methodCallHandler = new MethodCallHandler(flutterPluginBinding.getApplicationContext(),
                _loginCallback);
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
