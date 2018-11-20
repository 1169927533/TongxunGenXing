package com.example.a11699.tongxungenxing.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a11699.tongxungenxing.ECApplication;
import com.example.a11699.tongxungenxing.R;
import com.example.a11699.tongxungenxing.Util.tools.DataCleanManager;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

import java.io.File;

import butterknife.BindView;

public class MainActivity extends Base_Activity implements   DrawerLayout.DrawerListener {
    @BindView(R.id.ac_main_Navigation)
    NavigationView navigationView;//侧拉菜单
    @SuppressLint("ResourceType")
    @BindView(R.layout.head)
    View view;//头部视图
    private Bitmap bitmap;//头部视图的头像
    @BindView(R.id.ac_main_DrawerLayout)
    DrawerLayout drawerLayout;
    ImageView person;//头部人的图片
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout.addDrawerListener(this);//给抽屉注入监听事件
    }
    //侧滑菜单的显示
    MenuItem ren_workname, ren_workplace, ren_workzhiwu,qiandaoo,ren_clean,ren_teach,min_tel;
    void xianshi(){
        Menu menu=navigationView.getMenu();
        ren_workname = menu.findItem(R.id.ren_workname);
        ren_workplace = menu.findItem(R.id.ren_workplace);
        ren_workzhiwu = menu.findItem(R.id.ren_workzhiwu);
          qiandaoo = menu.findItem(R.id.qiandaoo);
          ren_clean = menu.findItem(R.id.ren_clean);
          ren_teach = menu.findItem(R.id.ren_teach);
          min_tel = menu.findItem(R.id.min_tel);
        final MenuItem ren_huancunbig = menu.findItem(R.id.ren_huancunbig);
        MenuItem ren_tuichu = menu.findItem(R.id.ren_tuichu);
        ren_workname.setTitle("  实习单位: " );
        ren_workplace.setTitle("  实习地点: " ) ;
        ren_workzhiwu.setTitle("  职务: "  );
        qiandaoo.setTitle("  签到+记录: "   );
        ren_clean.setTitle("  清理缓存");
        ren_teach.setTitle("  指导老师：" );
        min_tel.setTitle("  电话："  );

        File file = new File(this.getCacheDir().getPath());
        try {
            ren_huancunbig.setTitle("  占用缓存: " + DataCleanManager.getCacheSize(file));
        } catch (Exception e) {
            e.printStackTrace();
        }

        ren_tuichu.setTitle("  退出登录");

        TextView textView = view.findViewById(R.id.ren_name);
        person = view.findViewById(R.id.ac_head_CircleView);
        textView.setText("去个名字");
        //给侧拉菜单的菜单项设立点击事件
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.qiandaoo:
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {//未开启定位权限
                            //开启定位权限,200是标识码
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
                        } else {
                            Toast.makeText(MainActivity.this, "已开启定位权限", Toast.LENGTH_LONG).show();
                            qiandao();
                        }
                        break;
                    case R.id.ren_clean:
                        clean(ren_huancunbig);
                        break;
                    case R.id.ren_tuichu:
                        tuichu();
                        break;
                }
                return false;
            }
        });
    }

    void qiandao() {
      //  Intent intent = new Intent(this, qiandaoActivity.class);
        //startActivity(intent);
    }
    void tuichu() {
        EMClient.getInstance().logout(false, new EMCallBack() {
            @Override
            public void onSuccess() {
                Log.i("lzan13", "logout success");
                // 调用退出成功，结束app
                exit();
                finish();
            }

            @Override
            public void onError(int i, String s) {
                Log.i("lzan13", "logout error " + i + " - " + s);
            }

            @Override
            public void onProgress(int i, String s) {

            }
        });
    }

    public static void exit() {
      /*  userInformationStatic.unit="";//公司
        userInformationStatic.name="";//
        userInformationStatic.job="";
        userInformationStatic.tutor="";//导师
        userInformationStatic.qiandaoCount="";//签到次数
        userInformationStatic.tPhone="";//老师电话
        userInformationStatic.picture="";//学生图片
        userInformationStatic.city="";//实习地点
        userInformationStatic.pictureB=null;//学生图片
        MyApplication.setS_id("");
        MyApplication.setS_name("");
        editor.clear();
        editor.apply();
        Intent intent = new Intent(ECApplication.getmContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ECApplication.getmContext().startActivity(intent);
        */
    }

    void clean(final MenuItem ren_huancunbig) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //设置参数
        builder.setTitle("是否清理缓存").setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try {
                    DataCleanManager.cleanInternalCache(getApplicationContext());
                    findHuanSize(ren_huancunbig);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }
    void findHuanSize(MenuItem ren_huancunbig) {
        //获得应用内部缓存(/data/data/com.example.androidclearcache/cache)
        File file = new File(this.getCacheDir().getPath());
        try {
            ren_huancunbig.setTitle(DataCleanManager.getCacheSize(file));
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onDrawerSlide(@NonNull View view, float v) {

    }

    @Override
    public void onDrawerOpened(@NonNull View view) {
        Log.i("head","抽屉展开");
    }

    @Override
    public void onDrawerClosed(@NonNull View view) {
        Log.i("head","抽屉关闭");
    }

    @Override
    public void onDrawerStateChanged(int i) {
        Log.i("head","抽屉状态发生关闭");
    }
}
