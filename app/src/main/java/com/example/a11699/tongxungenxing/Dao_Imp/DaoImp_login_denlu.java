package com.example.a11699.tongxungenxing.Dao_Imp;

import com.example.a11699.tongxungenxing.Activity.Login_Activity;
import com.example.a11699.tongxungenxing.Dao.Dao_login_denlu;
import com.example.a11699.tongxungenxing.ECApplication;
import com.example.a11699.tongxungenxing.Internet.Net;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DaoImp_login_denlu implements Dao_login_denlu {
    Login_Activity login_activity;
    //g构造函数

    public DaoImp_login_denlu(Login_Activity login_activity) {
        this.login_activity = login_activity;
    }

    @Override
    public void func_denlu(final String userName, final String UserPassword){
        Handler handler=new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                String url= Net.firstLogin+"?s_sid="+userName+"&s_pwd="+UserPassword;
                Log.i("denglu",url);
                OkHttpClient okHttpClient=new OkHttpClient();
                Request request=new Request.Builder().url(url).build();
                Call call=okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.i("denglu","网络请求失败");
                        Looper.prepare();
                        Toast.makeText(login_activity,"网络请求失败",Toast.LENGTH_SHORT).show();
                        Looper.loop();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseData=response.body().string();
                        try {
                            JSONObject jsonObject=new JSONObject(responseData);
                            JSONObject jsonObject1=jsonObject.getJSONObject("firstLogin");
                            try{
                                //这两个获值只是为了判断登录成功没，没登录成功话获取就会报错然后抛出异常
                                String stu_id=jsonObject1.getString("sid");
                                String password=jsonObject1.getString("token");
                                login_activity.loginCallback();//判断登录情况
                            }catch(Exception e){
                                login_activity.loginBack();
                                Log.i("zjc","没这项值");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });


    }
}
