package com.example.testadd;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unity3d.player.UnityPlayer;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TestAdd#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TestAdd extends Fragment {

    public static TestAdd Instance = null;

    private static  String TAG = "TestAdd";

    String m_gameObjectName;
    String m_funcName;

    private Context unityContext;
    private Activity unityActivity;


    public void UnityFunc(String gameObjectName, String funcName)
    {
        m_gameObjectName = gameObjectName;
        m_funcName = funcName;
    }

    public static TestAdd GetInstanceByAndroid(Activity activity,Context context)
    {
        if (Instance == null)
        {
            Instance = new TestAdd();
            Instance.unityActivity = activity;
            Instance.unityContext = context;

            activity.getFragmentManager().beginTransaction().add(Instance, TAG).commit();

        }
        return Instance;
    }



    public static TestAdd GetInstance()
    {
        if(Instance == null)
        {
            Instance = new TestAdd();
            Instance.unityActivity = UnityPlayer.currentActivity;
            Instance.unityContext = Instance.unityActivity.getBaseContext();
            UnityPlayer.currentActivity.getFragmentManager().beginTransaction().add(Instance, TAG).commit();

        }
        return Instance;
    }

    public int Add(int a,int b)
    {
        return a+b;

    }
}