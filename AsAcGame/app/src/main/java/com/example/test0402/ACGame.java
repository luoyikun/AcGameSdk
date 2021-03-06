package com.example.test0402;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

//import androidx.fragment.app.Fragment;

import com.acgame.sdk.YlCallBack;
import com.acgame.sdk.YlGameSdk;
import com.acgame.sdk.records.YlInitInfo;
import com.acgame.sdk.records.YlPayInfo;
import com.example.appcomacti.appcom;
import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;


import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.util.Date;


public class ACGame extends Fragment {

    public static ACGame Instance = null;

    private static  String TAG = "ACGame";

    String m_gameObjectName;
    String m_funcName;

    private Context unityContext;
    private Activity unityActivity;


    public void UnityFunc(String gameObjectName, String funcName)
    {
        m_gameObjectName = gameObjectName;
        m_funcName = funcName;
    }

    public static ACGame GetInstanceByAndroid(Activity activity,Context context)
    {
        if (Instance == null)
        {
            Instance = new ACGame();
            Instance.unityActivity = activity;
            Instance.unityContext = context;

            activity.getFragmentManager().beginTransaction().add(Instance, TAG).commit();
            Instance.YlGameSdkInit(Instance.unityActivity);
        }
        return Instance;
    }



    public static ACGame GetInstance()
    {
        if(Instance == null)
        {
            Instance = new ACGame();
            Instance.unityActivity = UnityPlayer.currentActivity;
            Instance.unityContext = Instance.unityActivity.getBaseContext();
            UnityPlayer.currentActivity.getFragmentManager().beginTransaction().add(Instance, TAG).commit();
            Instance.YlGameSdkInit(Instance.unityActivity);
        }
        return Instance;
    }

    public void YlGameSdkInit(Activity activity)
    {
        YlInitInfo init_info = new YlInitInfo();
        init_info.setChannelId(1);
        init_info.setGameId(2);
        init_info.setDebug(false);
        init_info.setSignKey("ecd8b2026f61ebca742c53d6d22b11d9");//SCREEN_ORIENTATION_PORTRAIT SCREEN_ORIENTATION_LANDSCAPE
        YlGameSdk.init(activity, init_info, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //????????????????????????
        YlGameSdk.setLogoutCallback(new YlCallBack.LogoutCallback() {
            @Override
            public void onLogout() {

            }
        });

    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);  // ?????????????????????????????????Fragment???????????????????????????????????????????????????????????????Fragment?????????Activity???
    }

    // ?????? Unity
    // gameObjectName ??????????????????Unity ??? GameObject ?????????
    // functionName   ??????????????????GameObject ?????? C# ?????????????????????
    // _content       ????????????Unity ?????????
    public void CallUnity( String _content)
    {
        UnityPlayer.UnitySendMessage(m_gameObjectName, m_funcName, _content);
    }

    public void UnityDoLogin()
    {
        Intent intent = new Intent(getActivity(), appcom.class);
        getActivity().startActivity(intent);

        YlGameSdk.login(new YlCallBack.LoginCallback() {
            @Override
            public void onSuccess(String uid, String accessToken, String gameToken) {
                Log.d(TAG,"UID:" + uid + "\n" + "ACCESS_TOKEN:" + accessToken + "\n" + "GAME_TOKEN:" + gameToken);
            }

            @Override
            public void onCancle() {

                Log.d(TAG,"???????????????");
            }

            @Override
            public void onError(Exception e) {

                Log.d(TAG,"?????????" + e.getMessage());
            }
        });
    }

    public void UnityDoPay()
    {
        YlPayInfo payInfo = new YlPayInfo();
        payInfo.setProductId("10001");
        payInfo.setServerId(0);
        payInfo.setPushInfo("?????????????????????");
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