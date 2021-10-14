//------------------------------------------------------------------------------
// Copyright (c) 2018-2018 Nirvana Technology Co. Ltd.
// All Right Reserved.
// Unauthorized copying of this file, via any medium is strictly prohibited.
// Proprietary and confidential.
//------------------------------------------------------------------------------

package com.nirvana.channelsdk;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.content.LocalBroadcastManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.nirvana.android.ActivityManager;
import com.nirvana.channel.ChannelAgent;
import com.nirvana.channel.ConfigHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;


public class MainAgent extends ChannelAgent {
    public static Boolean is_debug = true;
    private String apiKey = ConfigHelper.getConfig("apiKey");
    private String sandboxApiKey = ConfigHelper.getConfig("sandboxApiKey");


    @Override
    public String getChannelID() {
        return ConfigHelper.getChannelID();
    }

    public String getAgentID(){
        return "";
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
        logs("初始化！");
        this.triggerInitializedEvent(true);
    }

    @Override
    public void login(final String userInfo) {
        logs("登录！");
    }

    @Override
    public void logout(String userInfo) {
        logs("登出！");
        this.triggerLogoutEvent();
    }

    @Override
    public void pay(String userInfo, String orderID, String productID, double amount) {

        logs("pay_userInfo : " + userInfo);
        try {
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
        }
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
