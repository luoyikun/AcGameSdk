package com.example.test0402;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.acgame.sdk.YlCallBack;
import com.acgame.sdk.YlGameSdk;
import com.acgame.sdk.records.YlInitInfo;
import com.acgame.sdk.records.YlPayInfo;
import com.example.acgame04021.acgame04021;
import com.example.acgame0403.AcGame0403;
import com.example.appcomacti.UnityAppcom;


import java.util.Date;

public class MainActivity extends Activity {

    TextView txt = null;
    Button loginBtn = null;
    Button payBtn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
        setContentView(R.layout.activity_main);

        /*UnityAppcom com = new UnityAppcom();
        com._unityActivity = this;
        com.CreateAppCom();*/
        //ACGame.GetInstanceByAndroid(this,this.getBaseContext());
        acgame04021.GetInstanceByAndroid(this,this.getBaseContext());

        AcGame0403 game0403 = new AcGame0403();
        game0403.SetActivity(this);
        game0403.UnityDoLogin();
        txt = findViewById(R.id.title_view);
        loginBtn = findViewById(R.id.demo_dologin_btn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                acgame04021.Instance.UnityDoLogin();
            }
        });
        payBtn = findViewById(R.id.demo_dopay_btn);
        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                acgame04021.Instance.UnityDoPay();
            }
        });


       /* YlInitInfo init_info = new YlInitInfo();
        init_info.setChannelId(1);
        init_info.setGameId(2);
        init_info.setDebug(false);
        init_info.setSignKey("ecd8b2026f61ebca742c53d6d22b11d9");//SCREEN_ORIENTATION_PORTRAIT SCREEN_ORIENTATION_LANDSCAPE
        YlGameSdk.init(this, init_info, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //添加退出账号监听
        YlGameSdk.setLogoutCallback(new YlCallBack.LogoutCallback() {
            @Override
            public void onLogout() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        txt.setText("退出账号");
                    }
                });
            }
        });*/

    }

    public void doPay(YlPayInfo payInfo) {
        YlGameSdk.pay(payInfo, new YlCallBack.PayCallback() {
            @Override
            public void onSuccess(YlPayInfo payinfo) {
                if (payinfo.isPaySuccess()) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            txt.setText("支付成功");
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            txt.setText("支付失败");
                        }
                    });
                }
            }

            @Override
            public void onCancle() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        txt.setText("支付取消");
                    }
                });
            }

            @Override
            public void onFault(final int code) {

                runOnUiThread(new Runnable() {
                    public void run() {
                        switch (code) {
                            case -1:
                                txt.setText("帐号未登录，无法支付");
                                break;
                            case -2:
                                txt.setText("初始化未成功，稍后尝试");
                                break;
                            case -3:
                                txt.setText("未选择支付方式");
                                break;
                            case -4:
                                txt.setText("订单创建失败");
                                break;
                            case -5:
                                txt.setText("订单验证出错");
                                break;
                            case -6:
                                txt.setText("ProductId未录入");
                                break;
                            case -7:
                                txt.setText("订单扩展信息获取失败");
                                break;
                            case -8:
                                txt.setText("订单在PENDING状态，最终确认后会自动发货");
                                break;
                            default:
                                txt.setText("未知错误，无法支付");
                        }

                    }
                });
            }

            @Override
            public void onError(final String errMsg) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        txt.setText("发生错误：" + errMsg);
                    }
                });
            }
        });
    }

    public void onBackPressed() {
        YlGameSdk.onGameDestroy();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}