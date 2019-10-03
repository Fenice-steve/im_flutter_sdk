package com.easemob.im_flutter_sdk;

import android.os.Handler;
import android.os.Looper;

import com.hyphenate.EMCallBack;
import com.hyphenate.exceptions.HyphenateException;

import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.common.MethodChannel.Result;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/** ImFlutterSdkPlugin */
public class ImFlutterSdkPlugin {
  private static final String CHANNEL_PREFIX = "com.easemob.im";
  static final Handler handler = new Handler(Looper.getMainLooper());

  private ImFlutterSdkPlugin(){}

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    registerClientWith(registrar);
    registerChatManagerWith(registrar);
    registerContactManagerWith(registrar);
    registerConversationWith(registrar);
  }

  public static void registerClientWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), CHANNEL_PREFIX + "/em_client");
    channel.setMethodCallHandler(new EMClientWrapper(registrar.context()));
  }

  public static void registerChatManagerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), CHANNEL_PREFIX + "/em_chat_manager");
    channel.setMethodCallHandler(new EMChatManagerWrapper());
  }

  public static void registerContactManagerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), CHANNEL_PREFIX + "/em_contact_manager");
    channel.setMethodCallHandler(new EMContactManagerWrapper());
  }

  public static void registerConversationWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), CHANNEL_PREFIX + "/em_conversation");
    channel.setMethodCallHandler(new EMConversationWrapper());
  }
}

interface EMWrapper {
  default void post(Consumer<Void> func) {
    ImFlutterSdkPlugin.handler.post(new Runnable() {
      @Override
      public void run() {
        func.accept(null);
      }
    });
  }

  default void onSuccess(Result result) {
    Map<String, Object> data = new HashMap<String, Object>();
    data.put("success", Boolean.TRUE);
    result.success(data);
  }

  default void onError(Result result, HyphenateException e) {
    Map<String, Object> data = new HashMap<String, Object>();
    data.put("success", Boolean.FALSE);
    data.put("code", e.getErrorCode());
    data.put("desc", e.getDescription());
    result.success(data);
  }
}

class EMWrapperCallBack implements EMCallBack{
  EMWrapperCallBack(Result result) {
    this.result = result;
  }

  private Result result;

  void post(Consumer<Void> func) {
    ImFlutterSdkPlugin.handler.post(new Runnable() {
      @Override
      public void run() {
        func.accept(null);
      }
    });
  }

  @Override
  public void onSuccess() {
    post((Void)->{
      Map<String, Object> data = new HashMap<String, Object>();
      data.put("success", Boolean.TRUE);
      result.success(data);
    });
  }

  @Override
  public void onError(int code, String desc) {
    post((Void)->{
      Map<String, Object> data = new HashMap<String, Object>();
      data.put("success", Boolean.FALSE);
      data.put("code", code);
      data.put("desc", desc);
      result.success(data);
    });
  }

  @Override
  public void onProgress(int progress, String status) {
    // no need
  }
}