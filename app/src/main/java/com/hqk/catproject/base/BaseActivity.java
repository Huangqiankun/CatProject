package com.hqk.catproject.base;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hqk.catproject.MainActivity;

import java.util.List;

public abstract class BaseActivity extends AppCompatActivity {


    public abstract int getContentViewId();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());

        initData();

    }

    public abstract void initData();

    protected void logCat(String content) {
        Log.e("CatProject", content);
    }


    public void getWriteAndCameraPermission() {
        XXPermissions.with(this)
                // 申请单个权限
                .permission(Permission.CAMERA)
                // 申请多个权限
                .permission(Permission.Group.STORAGE)
                .request(new OnPermissionCallback() {

                    @Override
                    public void onGranted(List<String> permissions, boolean all) {
                        if (all) {
//                            toast("获取权限成功");
                            doPermissionSuccess();
                        } else {
                            getWriteAndCameraPermission();
                        }
                    }

                    @Override
                    public void onDenied(List<String> permissions, boolean never) {
                        if (never) {
                            toast("被永久拒绝授权，请手动授予权限");
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(BaseActivity.this, permissions);
                        } else {
                            toast("获取权限失败");
                        }
                    }
                });
    }

    protected abstract void doPermissionSuccess();


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == XXPermissions.REQUEST_CODE) {
            if (XXPermissions.isGranted(this, Permission.CAMERA) &&
                    XXPermissions.isGranted(this, Permission.Group.STORAGE)) {
                toast("用户已经在权限设置页授予了权限");
                getWriteAndCameraPermission();
            } else {
                toast("用户没有在权限设置页授予权限");
            }
        }
    }


    protected void toast(String content) {
        Toast.makeText(this, content, Toast.LENGTH_LONG).show();
    }

}
