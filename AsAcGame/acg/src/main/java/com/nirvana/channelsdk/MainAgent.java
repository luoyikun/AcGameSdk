
package com.nirvana.channelsdk;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.acgame.sdk.YlCallBack;
import com.acgame.sdk.YlGameSdk;
import com.acgame.sdk.records.YlInitInfo;
import com.acgame.sdk.records.YlPayInfo;
import com.google.gson.Gson;
import com.nirvana.android.ActivityManager;
import com.nirvana.channel.ChannelAgent;
import com.nirvana.channel.ConfigHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;


public class MainAgent extends ChannelAgent {
    String TAG = "MainAgent";

    class JsonLogin
    {
        public String uid;
        public String accessToken;
        public String gameToken;
        public String gameAccount;
    }

    public static Boolean is_debug = true;
    private String apiKey = ConfigHelper.getConfig("apiKey");
    private String sandboxApiKey = ConfigHelper.getConfig("sandboxApiKey");

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

    @Override
    public String getChannelID() {
        return ConfigHelper.getChannelID();
    }

    public String getAgentID(){
        return ConfigHelper.getChannelID();
    }

    public String getInitUrl(){
        return ConfigHelper.getChannelInitUrl();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        logs("Create！");

    }

    @Override
    public void initialize() {
        if(ConfigHelper.getConfig("channel_sdk_debug").equals("true"))
            is_debug = true;
        //  String game = ConfigHelper.getAppID();
        //   String key = ConfigHelper.getAppKey();

        UnityDoInit();
        logs("初始化！");
        this.triggerInitializedEvent(true);
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
                    triggerLogoutEvent();
                }
            });

            //Toast.makeText(getActivity(),"ACGameSdkInit OK",Toast.LENGTH_SHORT).show();
            //这里是主动调用Unity中的方法，该方法之后unity部分会讲到
            callUnity( "ACGameSdkInit OK");
        }
        catch (Exception e)
        {
            Log.e(TAG,Log.getStackTraceString(e));
            callUnity( Log.getStackTraceString(e));
        }

    }

    JsonLogin m_jsonLogin = new JsonLogin();
    @Override
    public void login(final String userInfo)
    {
        logs("登录！");
        UnityDoLogin();

    }

    public void UnityDoLogin()
    {
        try {

            YlGameSdk.login(new YlCallBack.LoginCallback() {
                @Override
                public void onSuccess(String uid, String accessToken, String gameToken) {
                    Log.d(TAG, "UID:" + uid + "\n" + "ACCESS_TOKEN:" + accessToken + "\n" + "GAME_TOKEN:" + gameToken);
                    m_jsonLogin.uid = uid;
                    m_jsonLogin.accessToken = accessToken;
                    m_jsonLogin.gameToken = gameToken;
                    m_jsonLogin.gameAccount = YlGameSdk.sGameAccount;

                    Gson gson = new Gson();
                    String sLogin =  gson.toJson(m_jsonLogin);
                    //Toast.makeText(getActivity(),sLogin,Toast.LENGTH_SHORT).show();
                    logs("LoginInfo:"+sLogin);
                    triggerLoginEvent(sLogin);
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


            //这里是主动调用Unity中的方法，该方法之后unity部分会讲到
            callUnity( "UnityDoLogin OK");
        }
        catch (Exception e)
        {
            Log.e(TAG,Log.getStackTraceString(e));
            callUnity(Log.getStackTraceString(e));
        }
    }

    @Override
    public void logout(String userInfo) {
        logs("登出！");
        this.triggerLogoutEvent();
    }

    @Override
    public void pay(String userInfo, String orderID, String productID, double amount) {

        logs("pay_userInfo : " + userInfo);

        UnityDoPay();

        /*try {
            JSONObject json_obj = new JSONObject(userInfo);

            final String RoleID = json_obj.getString("RoleID");
            final int RoleLevel = Integer.parseInt(json_obj.getString("RoleLevel"));
            final String RoleName = json_obj.getString("RoleName");
            final String ZoneID = json_obj.getString("ZoneID");
            final String ZoneName = json_obj.getString("ZoneName");
            final String GuildName = json_obj.getString("GuildName");
            final int VIP = Integer.parseInt(json_obj.getString("VIP"));
            final String ProductName = json_obj.getString("ProductName");
            final String ProductDesc = json_obj.getString("ProductDesc");
            final int Ratio = Integer.parseInt(json_obj.getString("Ratio"));
            final String UserID = json_obj.getString("UserID");
            String money=String.valueOf(amount);
            String VNPayitem =ConfigHelper.getConfig("VNPayitem");
            JSONObject jsonObject =new JSONObject(VNPayitem);
            logs("支付！");
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }*/
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

    public void onExit(){
        logs("退出！");
        this.triggerExitEvent();
    }

    public void reportEnterZone(String userInfo) {
//        submitExtraData(0, userInfo);// 选择服务器
    }

    public void reportCreateRole(String userInfo) {
        submitExtraData(1, userInfo);// 创建角色
    }

    public void reportLoginRole(String userInfo) {
        submitExtraData(2, userInfo);// 进入游戏
    }

    public void reportLevelUp(String userInfo) {
        submitExtraData(3, userInfo);// 等级提升
    }

    public void submitExtraData(int type, String userInfo) {
        logs("type : " + type +" userInfo : " + userInfo);
        try {
            JSONObject json_obj = new JSONObject(userInfo);
            final int Diamond = Integer.parseInt(json_obj.getString("Diamond"));
            final String RoleID = json_obj.getString("RoleID");
            final String RoleLevel = json_obj.getString("RoleLevel");
            final String RoleName = json_obj.getString("RoleName");
            final String ZoneID = json_obj.getString("ZoneID");
            final String ZoneName = json_obj.getString("ZoneName");
            final String GuildName = json_obj.getString("GuildName");
            final int VIP = Integer.parseInt(json_obj.getString("VIP"));
            final String RoleCreateTime = json_obj.getString("CreateTime");
            final String UserID = json_obj.getString("UserID");

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        logs("上报！");
    }

    public void logs(String msg)
    {
        if(is_debug)
            Log.e("vtcsdk", msg);
    }


}
