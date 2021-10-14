package com.example.acgame0403;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import com.acgame.sdk.YlCallBack;
import com.acgame.sdk.YlGameSdk;
import com.acgame.sdk.records.YlInitInfo;
import com.acgame.sdk.records.YlPayInfo;
import com.acgame.sdk.view.WVLoginActivity;
import com.google.gson.Gson;


public class AcGame0403 {
    String TAG = "AcGame0403";
    /**
     * unity项目启动时的的上下文
     */
    private Activity _unityActivity;

    String _unityObjName;
    String _unityFuncName;

    public void SetUnityInfo(String unityObjName,String unityFuncName)
    {
        _unityObjName = unityObjName;
        _unityFuncName = unityFuncName;
    }

    public  void SetActivity(Activity activity)
    {
        if (_unityActivity == null)
        {
            _unityActivity = activity;

        }
    }

    /**
     * 获取unity项目的上下文
     * @return
     */
    Activity getActivity(){
        if(null == _unityActivity) {
            try {
                Class<?> classtype = Class.forName("com.unity3d.player.UnityPlayer");
                Activity activity = (Activity) classtype.getDeclaredField("currentActivity").get(classtype);
                _unityActivity = activity;
            } catch (ClassNotFoundException e) {

            } catch (IllegalAccessException e) {

            } catch (NoSuchFieldException e) {

            }
        }
        return _unityActivity;
    }

    /**
     * 调用Unity的方法
     * @param args              参数
     * @return                  调用是否成功
     */
    boolean callUnity( String args){
        try {
            Class<?> classtype = Class.forName("com.unity3d.player.UnityPlayer");
            Method method =classtype.getMethod("UnitySendMessage", String.class,String.class,String.class);
            method.invoke(classtype,_unityObjName,_unityFuncName,args);
            return true;
        } catch (ClassNotFoundException e) {

        } catch (NoSuchMethodException e) {

        } catch (IllegalAccessException e) {

        } catch (InvocationTargetException e) {

        }
        return false;
    }

    /**
     * Toast显示unity发送过来的内容
     * @param content           消息的内容
     * @return                  调用是否成功
     */
    public boolean showToast(String content){
        Toast.makeText(getActivity(),content,Toast.LENGTH_SHORT).show();
        //这里是主动调用Unity中的方法，该方法之后unity部分会讲到
        callUnity("hello unity i'm android");
        return true;
    }

    public void UnityDoInit()
    {
        ACGameSdkInit(getActivity());
    }

    public void ACGameSdkInit(Activity activity)
    {
        try {


            YlInitInfo init_info = new YlInitInfo();
            init_info.setChannelId(1);
            init_info.setGameId(2);
            init_info.setDebug(false);
            init_info.setSignKey("ecd8b2026f61ebca742c53d6d22b11d9");//SCREEN_ORIENTATION_PORTRAIT SCREEN_ORIENTATION_LANDSCAPE
            YlGameSdk.init(activity, init_info, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

            //添加退出账号监听
            YlGameSdk.setLogoutCallback(new YlCallBack.LogoutCallback() {
                @Override
                public void onLogout() {

                }
            });

            Toast.makeText(getActivity(),"ACGameSdkInit OK",Toast.LENGTH_SHORT).show();
            //这里是主动调用Unity中的方法，该方法之后unity部分会讲到
            callUnity( "ACGameSdkInit OK");
        }
        catch (Exception e)
        {
            Log.e(TAG,Log.getStackTraceString(e));
            callUnity( Log.getStackTraceString(e));
        }

    }

    public void UnityDoLoginOut()
    {

        YlGameSdk.setLogoutCallback(new YlCallBack.LogoutCallback() {
            @Override
            public void onLogout() {
                AndroidContent anCon = new AndroidContent();
                anCon.eventType = EnAnEvent.LoginOut;
                Gson gson = new Gson();
                String sAnCon =  gson.toJson(anCon);
                callUnity( sAnCon);
            }
        });
    }
    public void UnityDoFastLogin()
    {


    }
    public void UnityDoLogin()
    {
        try {

            YlGameSdk.login(new YlCallBack.LoginCallback() {
                @Override
                public void onSuccess(String uid, String accessToken, String gameToken) {
                    Log.d(TAG, "UID:" + uid + "\n" + "ACCESS_TOKEN:" + accessToken + "\n" + "GAME_TOKEN:" + gameToken);
                    AndroidContent anCon = new AndroidContent();
                    anCon.eventType = EnAnEvent.Login;
                    anCon.value = 1;
                    anCon.content = uid + "+" + YlGameSdk.sGameAccount;
                    Gson gson = new Gson();
                    String sAnCon =  gson.toJson(anCon);
                    callUnity( sAnCon);
                }

                @Override
                public void onCancle() {

                    Log.d(TAG, "登陆已取消");
                }

                @Override
                public void onError(Exception e) {

                    Log.d(TAG, "错误：" + e.getMessage());
                }
            });

            Toast.makeText(getActivity(),"UnityDoLogin OK",Toast.LENGTH_SHORT).show();
            //这里是主动调用Unity中的方法，该方法之后unity部分会讲到
            callUnity( "UnityDoLogin OK");
        }
        catch (Exception e)
        {
            Log.e(TAG,Log.getStackTraceString(e));
            callUnity(Log.getStackTraceString(e));
        }
    }

    public void UnityDoPay()
    {
        YlPayInfo payInfo = new YlPayInfo();
        payInfo.setProductId("10001");
        payInfo.setServerId(0);
        payInfo.setPushInfo("透传服务器信息");
        payInfo.setCustom("DEMO GAME ORDER|ROLEID:1|ROLELEVE:99");
        long timest = new Date().getTime();
        payInfo.setGameOrderNO("GAMEDEMO" + Long.toString(timest));
        doPay(payInfo);

    }

    public void doPay(YlPayInfo payInfo) {
        YlGameSdk.pay(payInfo, new YlCallBack.PayCallback() {
            @Override
            public void onSuccess(YlPayInfo payinfo) {
                if (payinfo.isPaySuccess()) {

                } else {

                }
            }

            @Override
            public void onCancle() {

            }

            @Override
            public void onFault(final int code) {

            }

            @Override
            public void onError(final String errMsg) {

            }
        });
    }

}

class AndroidContent
{
    public EnAnEvent eventType;
    public int value;
    public String content;
}

enum EnAnEvent
{
    Login,
    LoginOut,
    Pay
}
