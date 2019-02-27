package com.snbc.incremental.update;

import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    /**
     * 旧版本
     */
    //    String old = getsdpath() + "old.apk";
    String old = "";
    /**
     * 新版本
     */
    String newp = getsdpath() + "new.apk";
    /**
     * 差分包
     */
    String patch = getsdpath() + "patch.patch";
    /**
     * 旧版apk和差分包合并生成的新版apk
     */
    String tmp = getsdpath() + "new_new.apk";
    private TextView tv_msg;
    private ImageView iv_image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_diff).setOnClickListener(this);
        findViewById(R.id.btn_patch).setOnClickListener(this);
        iv_image = findViewById(R.id.iv_image);

        tv_msg = findViewById(R.id.tv_msg);
                new Handler()
                        .postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                tv_msg.setText("落花有意随流水,\n流水无心恋落花");
                                Picasso.get().load(R.mipmap.icon_bg)
                                        .into(iv_image);
                            }
                        }, 5000);

        old = getInstallPath2();

        Picasso.get().load(R.mipmap.icon_splash)
                .into(iv_image);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_diff:
                try {
                    long s = System.currentTimeMillis();
                    diff(old, newp, patch);
                    long s1 = System.currentTimeMillis();
                    Toast.makeText(this, "生成差分包成功，用时:" + (s1 - s) + "ms", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.btn_patch:
                long s2 = System.currentTimeMillis();
                patch(old, tmp, patch);
                long s3 = System.currentTimeMillis();
                Toast.makeText(this, "差分包合并成功，用时:" + (s3 - s2) + "ms", Toast.LENGTH_SHORT).show();
                break;
            default:
        }
    }

    private String getsdpath() {
        return Environment.getExternalStorageDirectory().getPath() + File.separator;
    }

    /**
     * 获取当前APK文件路径
     *
     * @return
     */
    public String getInstallPath() {
        try {
            String apkPath = getPackageResourcePath();
            Log.d("@@@", "当前旧包APK地址-->" + apkPath);
            return apkPath;
        } catch (Exception e) {
            Log.e("@@@", "获取当前旧包APK地址失败-->", e);
            return null;
        }
    }

    public String getInstallPath2() {
        try {
            String packageName = getPackageName();
            ApplicationInfo info = getPackageManager().getApplicationInfo(packageName, 0);
            String apkPath = info.sourceDir;
            Log.d("@@@", "当前旧包APK地址-->" + apkPath);
            return apkPath;
        } catch (Exception e) {
            Log.e("@@@", "获取当前旧包APK地址失败-->", e);
            return null;
        }
    }


    /**
     * 生成差分包
     *
     * @param oldpath 旧包路径
     * @param newpath 新包路径
     * @param patch   生成的差分包路径
     * @return 生成差分包所用时间
     */
    public native int diff(String oldpath, String newpath, String patch);

    /**
     * 旧apk和差分包合并
     *
     * @param oldpath 旧包路径
     * @param newpath 差分包路径
     * @param patch   生成的新包路径
     * @return 合并安装包用时间
     */
    public native int patch(String oldpath, String newpath, String patch);
}
