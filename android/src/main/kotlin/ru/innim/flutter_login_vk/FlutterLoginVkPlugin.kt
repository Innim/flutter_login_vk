package ru.innim.flutter_login_vk

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodChannel

class FlutterLoginVkPlugin : FlutterPlugin, ActivityAware {

    companion object {
        private const val channelName = "flutter_login_vk"
    }

    private var dartChannel: MethodChannel? = null
    private var loginCallback: LoginCallback? = null
    private var methodCallHandler: MethodCallHandler? = null
    private var activityListener: ActivityListener? = null
    private var activityPluginBinding: ActivityPluginBinding? = null


    override fun onAttachedToEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        val messenger: BinaryMessenger = binding.binaryMessenger
        val dartChannel = MethodChannel(messenger, channelName)
        val loginCallback = LoginCallback()
        val methodCallHandler = MethodCallHandler(binding.applicationContext, loginCallback)
        val activityListener = ActivityListener(loginCallback)
        dartChannel.setMethodCallHandler(methodCallHandler)

        this.activityListener = activityListener
        this.methodCallHandler = methodCallHandler
        this.dartChannel = dartChannel
        this.loginCallback = loginCallback
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        setActivity(binding)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        resetActivity()
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        setActivity(binding)
    }

    override fun onDetachedFromActivity() {
        resetActivity()
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        dartChannel?.setMethodCallHandler(null)

        methodCallHandler = null
        loginCallback = null
        activityPluginBinding = null
        activityListener = null
    }

    private fun setActivity(activityPluginBinding: ActivityPluginBinding) {
        methodCallHandler?.updateActivity(activityPluginBinding.activity)
        activityPluginBinding.addActivityResultListener(activityListener!!)
        this.activityPluginBinding = activityPluginBinding
    }

    private fun resetActivity() {
        activityPluginBinding?.removeActivityResultListener(activityListener!!)
        methodCallHandler?.updateActivity(null)
        activityPluginBinding = null
    }
}