using Newtonsoft.Json;
using System;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class AcGame0403 : MonoBehaviour {
    public Button m_btnInit;
    public Button m_btnLogin;
    public Button m_btnPay;
    public Button m_btnTest;
    public Text text;
    AndroidJavaObject _ajc;

    AndroidJavaObject _appcom;
    // Use this for initialization
    void Start () {
        m_btnInit.onClick.AddListener(OnInit);
        m_btnLogin.onClick.AddListener(OnLogin);
        m_btnPay.onClick.AddListener(OnPay);
        m_btnTest.onClick.AddListener(OnBtnClick);
        //_ajc = new AndroidJavaObject("com.example.appcomacti.UnityAppcom");
        _ajc = new AndroidJavaObject("com.example.acgame0403.AcGame0403");
        Debug.Log("Start OK");

        
    }

    public void OnBtnClick()
    {
        //通过API来调用原生代码的方法
        bool success = _ajc.Call<bool>("showToast", "this is unity");
        if (true == success)
        {
            //请求成功
        }
    }

    private void OnInit()
    {
        _ajc.Call("SetUnityInfo", "AndroidTools", "FromAndroid");
        //_ajc.Call("CreateAppCom");
        _ajc.Call("UnityDoInit");
    }

    void OnLogin()
    {
        _ajc.Call("UnityDoLogin");
    }

    void OnPay()
    {
        _ajc.Call("UnityDoPay");
    }

    public void FromAndroid(string content)
    {
        Debug.Log(content);
        try
        {
            AndroidContent anCon = JsonConvert.DeserializeObject<AndroidContent>(content);
            if (anCon != null)
            {
                switch (anCon.eventType)
                {
                    case EnAnEvent.Login:
                        Debug.Log(anCon.content);
                        break;
                    case EnAnEvent.LoginOut:
                        break;
                    case EnAnEvent.Pay:
                        break;
                    default:
                        break;
                }
            }
        }
        catch (Exception e)
        {
            Debug.Log("FromAndroid:" + e.ToString());
        }
    }

    public void Update()
    {

    }
}

public class AndroidContent
{
    public EnAnEvent eventType;
    public int value;
    public string content;
}

public enum EnAnEvent
{
    Login,
    LoginOut,
    Pay
}

