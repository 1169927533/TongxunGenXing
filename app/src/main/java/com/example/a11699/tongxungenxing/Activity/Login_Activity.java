package com.example.a11699.tongxungenxing.Activity;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.example.a11699.tongxungenxing.Dao.Dao_login_denlu;
import com.example.a11699.tongxungenxing.Dao_Imp.DaoImp_login_denlu;
import com.example.a11699.tongxungenxing.ECApplication;
import com.example.a11699.tongxungenxing.R;
import com.example.a11699.tongxungenxing.Util.MathTools;
import com.hyphenate.EMCallBack;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Login_Activity extends Base_Activity {
    @BindView(R.id.ac_login_floatButton)
    public FloatingActionButton ac_login_floatingActionButton;
    @BindView(R.id.ac_login_username)
    public EditText ac_login_username;//账号
    @BindView(R.id.ac_login_password)
    public EditText ac_login_password;//密码
    ProgressDialog progressDialog;//进度条

    String login_userName;//登录账号
    String login_passWord;//登录密码

    //调用登录接口
    Dao_login_denlu dao_login_denlu = new DaoImp_login_denlu(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);
        ButterKnife.bind(this);//初始化绑定ButterKnife
    }

    //登录的点击事件
    @OnClick(R.id.ac_login_denglu)
    public void bt_denglu() {
        progressDialog = new ProgressDialog(Login_Activity.this);
        progressDialog.setMessage("正在登录，请稍等");
        progressDialog.show();
        //还无法判断账号密码的准确性
        login_userName = ac_login_username.getText().toString().trim();
        login_passWord = ac_login_password.getText().toString().trim();

        String login_password_encrypt = MathTools.getMd5(login_passWord);//将密码加密
        dao_login_denlu.func_denlu(login_userName, login_password_encrypt);//调用登录的方法
    }

    //浮动FloatingActionButton的点击事件
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @OnClick(R.id.ac_login_floatButton)
    public void ac_login_func_floatButton() {
        getWindow().setExitTransition(null);//activity转场的过度动画
        getWindow().setEnterTransition(null);//入场的过度动画
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //通过共享元素实现的过度动画
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login_Activity.this, ac_login_floatingActionButton, ac_login_floatingActionButton.getTransitionName());
            startActivity(new Intent(Login_Activity.this, ac_zhuce_Activity.class), options.toBundle());
        } else {
            startActivity(new Intent(Login_Activity.this, ac_zhuce_Activity.class));
        }
    }

    //登录成功的回调函数
    public void loginCallback() {
        singup();
    }

    void singup() {
        ECApplication.setEC_UserId(login_userName);
        ECApplication.setEC_UserPassword(login_passWord);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //环信注册用户
                try {
                    EMClient.getInstance().createAccount(login_userName, login_passWord);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!Login_Activity.this.isFinishing()) {
                                progressDialog.dismiss();
                            }
                            //跳到填写个人信息的界面
                            tianxieinformation();
                        }
                    });
                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    //注册过的话会跳到这里
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!Login_Activity.this.isFinishing()) {

                            }
                            /**
                             * 关于错误码可以参考官方api详细说明
                             * http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1_e_m_error.html
                             */
                            int errorCode = e.getErrorCode();
                            String message = e.getMessage();
                            Log.d("lzan13",
                                    String.format("sign up - errorCode:%d, errorMsg:%s", errorCode,
                                            e.getMessage()));
                            switch (errorCode) {
                                // 网络错误
                                case EMError.NETWORK_ERROR:
                                    Toast.makeText(Login_Activity.this,
                                            "网络错误 code: " + errorCode + ", message:" + message,
                                            Toast.LENGTH_LONG).show();
                                    break;
                                // 用户已存在
                                case EMError.USER_ALREADY_EXIST:
                                    denglu();
                                    break;
                                // 参数不合法，一般情况是username 使用了uuid导致，不能使用uuid注册
                                case EMError.USER_ILLEGAL_ARGUMENT:
                                    Toast.makeText(Login_Activity.this,
                                            "参数不合法，一般情况是username 使用了uuid导致，不能使用uuid注册 code: "
                                                    + errorCode
                                                    + ", message:"
                                                    + message, Toast.LENGTH_LONG).show();
                                    break;
                                // 服务器未知错误
                                case EMError.SERVER_UNKNOWN_ERROR:
                                    Toast.makeText(Login_Activity.this,
                                            "服务器未知错误 code: " + errorCode + ", message:" + message,
                                            Toast.LENGTH_LONG).show();
                                    break;
                                case EMError.USER_REG_FAILED:
                                    Toast.makeText(Login_Activity.this,
                                            "账户注册失败 code: " + errorCode + ", message:" + message,
                                            Toast.LENGTH_LONG).show();
                                    break;
                                default:
                                    Toast.makeText(Login_Activity.this,
                                            "ml_sign_up_failed code: " + errorCode + ", message:" + message,
                                            Toast.LENGTH_LONG).show();
                                    break;
                            }
                        }
                    });

                }
            }
        }).start();

    }

    void denglu() {
        //进行环信登录
        EMClient.getInstance().login(login_userName, login_passWord, new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.i("zjc","登录成功");
                runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {
                        ECApplication.setEC_UserId(login_userName);
                        ECApplication.setEC_UserPassword(login_passWord);
                        ECApplication.editor.putString("s_userId",login_userName);
                        ECApplication.editor.putString("s_userPassword",login_passWord);
                        ECApplication.editor.apply();

                        Log.i("zjc","登录成功二次检查");
                        //可以确保群 聊天消息都被加载完
                        EMClient.getInstance().chatManager().loadAllConversations();
                        Intent intent=new Intent(Login_Activity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    }
                });
            }

            @Override
            public void onError(final int i, final String s) {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        progressDialog.dismiss();
                        Log.d("lzan13", "登录失败 Error code:" + i + ", message:" + s);
                        /**
                         * 关于错误码可以参考官方api详细说明
                         * http://www.easemob.com/apidoc/android/chat3.0/classcom_1_1hyphenate_1_1_e_m_error.html
                         */
                        switch (i) {
                            // 网络异常 2
                            case EMError.NETWORK_ERROR:
                                Toast.makeText(Login_Activity.this,
                                        "网络错误 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 无效的用户名 101
                            case EMError.INVALID_USER_NAME:
                                Toast.makeText(Login_Activity.this,
                                        "无效的用户名 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 无效的密码 102
                            case EMError.INVALID_PASSWORD:
                                Toast.makeText(Login_Activity.this,
                                        "无效的密码 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 用户认证失败，用户名或密码错误 202
                            case EMError.USER_AUTHENTICATION_FAILED:
                                Toast.makeText(Login_Activity.this,
                                        "用户认证失败，用户名或密码错误 code: " + i + ", message:" + s, Toast.LENGTH_LONG)
                                        .show();
                                break;
                            // 用户不存在 204
                            case EMError.USER_NOT_FOUND:
                                Toast.makeText(Login_Activity.this,
                                        "用户不存在 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 无法访问到服务器 300
                            case EMError.SERVER_NOT_REACHABLE:
                                Toast.makeText(Login_Activity.this,
                                        "无法访问到服务器 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 等待服务器响应超时 301
                            case EMError.SERVER_TIMEOUT:
                                Toast.makeText(Login_Activity.this,
                                        "等待服务器响应超时 code: " + i + ", message:" + s, Toast.LENGTH_LONG)
                                        .show();
                                break;
                            // 服务器繁忙 302
                            case EMError.SERVER_BUSY:
                                Toast.makeText(Login_Activity.this,
                                        "服务器繁忙 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            // 未知 Server 异常 303 一般断网会出现这个错误
                            case EMError.SERVER_UNKNOWN_ERROR:
                                Toast.makeText(Login_Activity.this,
                                        "未知的服务器异常 code: " + i + ", message:" + s, Toast.LENGTH_LONG).show();
                                break;
                            default:
                                Toast.makeText(Login_Activity.this,
                                        "ml_sign_in_failed code: " + i + ", message:" + s,
                                        Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                });
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    //登录失败的回调函数
    public void loginBack() {
        login_userName = "";
        login_passWord = "";
        Toast.makeText(this, "账号/密码错误", Toast.LENGTH_SHORT).show();
    }

    //填写信息
    void tianxieinformation() {
        ac_login_floatingActionButton.performClick();
    }
}
