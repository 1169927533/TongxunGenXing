package com.example.a11699.tongxungenxing.Activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.a11699.tongxungenxing.Internet.Net;
import com.example.a11699.tongxungenxing.R;
import com.example.a11699.tongxungenxing.Util.RealPathFromUriUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ac_zhuce_Activity extends Base_Activity {
    private EditText adduser_xuehao, adduser_workplace, adduser_gongsi, adduser_zhiwu;
    private Button btn_shangchuan, btn_subbmit;
    private ImageView adduser_tupian;
    CardView cvAdd;
    FloatingActionButton fab;
    ProgressBar processsd;
    RelativeLayout root_RelativeLayout;
    private String xuehao, workplace, gongsi, zhuwu;
    public static final int REQUEST_PICK_IMAGE = 11101;
    String realPathFromUri;//图片的位置
    String baseEncode = "";//转base64后的图片信息
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ac_zhuce_);
        initview();
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what==20){
                processsd.setVisibility(View.GONE);
                processsd.setIndeterminate(false);
                fab.performClick();
            }
        }
    };
    void initview() {

        cvAdd = findViewById(R.id.cv_add);
        fab = findViewById(R.id.fab);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ShowEnterAnimation();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                animateRevealClose();
            }
        });
        processsd=findViewById(R.id.processsd);
        adduser_xuehao = findViewById(R.id.adduser_xuehao);
        adduser_workplace = findViewById(R.id.adduser_workplace);
        adduser_gongsi = findViewById(R.id.adduser_gongsi);
        adduser_zhiwu = findViewById(R.id.adduser_zhiwu);
        btn_shangchuan = findViewById(R.id.btn_shangchuan);
        btn_subbmit = findViewById(R.id.btn_subbmit);
        adduser_tupian = findViewById(R.id.adduser_tupian);

        btn_subbmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //拿到数据
                xuehao = adduser_xuehao.getText().toString();
                workplace = adduser_workplace.getText().toString();
                gongsi = adduser_gongsi.getText().toString();
                zhuwu = adduser_zhiwu.getText().toString();
                root_RelativeLayout = (RelativeLayout) findViewById(R.id.sad);
                processsd.setVisibility(View.VISIBLE);
                processsd.setIndeterminate(true);
                //   Bitmap bitmap=getimage(realPathFromUri);
                //   Log.i("zjcc",bitmap.toString()+"");
                initData(xuehao,workplace,gongsi,zhuwu);//用post方式传递数据
            }
        });

        btn_shangchuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                } else {
                    intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                }
                startActivityForResult(intent, REQUEST_PICK_IMAGE);
            }
        });
    }
    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PICK_IMAGE:
                    if (data != null) {
                        realPathFromUri = RealPathFromUriUtils.getRealPathFromUri(this, data.getData());
                        Log.i("zjc", realPathFromUri);
                        baseEncode = getImageStr(realPathFromUri);
                        //  Log.i("zjcf", strBase64);
                        Log.i("zjcx", baseEncode);
                        // 从相册返回的数据
                        if (data != null) {
                            // 得到图片的全路径
                            Uri uri = data.getData();
                            adduser_tupian.setImageURI(uri);
                        }
                    } else {
                        Toast.makeText(this, "图片损坏，请重新选择", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    }
    public static String getImageStr(String imgFile) {
        File param = new File(imgFile);
        Bitmap bitmap = BitmapFactory.decodeFile(param.getPath());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, bos);//参数100表示不压缩
        byte[] bytes = bos.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }
    //post请求网络数据
    private void initData(String xuehaoo,String workplacee,String gongsii,String zhuwuu){
        //接口 
        String path = Net.updateStudentInfo;
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .build();
        FormBody formBody = new FormBody.Builder()
                .add("s_sid", xuehaoo)
                .add("s_city", workplacee)
                .add("s_company",gongsii)
                .add("s_job",zhuwuu)
                .add("image",baseEncode)
                .build();
        Request request = new Request.Builder()
                .url(path)
                .post(formBody)
                .build();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ac_zhuce_Activity.this,"网络连接失败,",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                try {
                    JSONObject jsonObject=new JSONObject(string);
                    if(jsonObject.getString("result").equals("1")){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(ac_zhuce_Activity.this,"success,",Toast.LENGTH_SHORT).show();
                                fab.performClick();
                            }
                        });

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void ShowEnterAnimation() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.transition);
            getWindow().setSharedElementEnterTransition(transition);
            transition.addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                    cvAdd.setVisibility(View.GONE);
                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    transition.removeListener(this);
                    animateRevealShow();
                }

                @Override
                public void onTransitionCancel(Transition transition) {

                }

                @Override
                public void onTransitionPause(Transition transition) {

                }

                @Override
                public void onTransitionResume(Transition transition) {

                }
            });
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth() / 2, 0, fab.getWidth() / 2, cvAdd.getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                cvAdd.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth() / 2, 0, cvAdd.getHeight(), fab.getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cvAdd.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                fab.setImageResource(R.drawable.plus);
                ac_zhuce_Activity.super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }
}
