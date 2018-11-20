package com.example.a11699.tongxungenxing.Activity;

import android.Manifest;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import com.example.a11699.tongxungenxing.ECApplication;
import com.example.a11699.tongxungenxing.R;
import com.gyf.barlibrary.ImmersionBar;

public class WelCome_Activity extends Base_Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wel_come_);
        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //判断手机里面 stu_id是否存有值，有值得话就直接进入主页面
                if (TextUtils.isEmpty(ECApplication.pref.getString("s_userId",""))){
                    Intent intent=new Intent(WelCome_Activity.this,Login_Activity.class);
                    startActivity(intent);
                }else{
                    ECApplication.setEC_UserId(ECApplication.pref.getString("s_userId",""));
                    ECApplication.setEC_UserPassword(ECApplication.pref.getString("s_userPassword",""));
                    Intent intent=new Intent(WelCome_Activity.this, MainActivity.class);
                    startActivity(intent);
                }
                finish();//销毁这个页面，不然
            }
        },2000);
    }
}
