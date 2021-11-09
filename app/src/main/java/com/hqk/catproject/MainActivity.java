package com.hqk.catproject;

import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hqk.catproject.base.BaseActivity;
import com.hqk.catproject.util.FileUtil;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.callback.SelectCallback;
import com.huantansheng.easyphotos.models.album.entity.Photo;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    @Override
    public int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {
        Log.e("MainActivity","initData");
    }

    @Override
    protected void doPermissionSuccess() {
        EasyPhotos.createCamera(this).start(new SelectCallback() {
            @Override
            public void onResult(ArrayList<Photo> photos, ArrayList<String> paths, boolean isOriginal) {
                logCat("doPhotoLast onResult : " );
                if (paths == null || paths.size() == 0)
                    return;
                doPhotoLast(paths.get(0));
            }
        });

    }

    private void doPhotoLast(String path) {
        logCat(path);
        path = FileUtil.getRealPathFromUri(this, Uri.parse(path));
        byte[] imgData = FileUtil.readFileByBytes(new File(path));

        //  字节数组转base64字符串
        String base64Str = Base64.encodeToString(imgData, Base64.DEFAULT);
        String imgParam = null;
        try {
            imgParam = URLEncoder.encode(base64Str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String param = "image=" + imgParam;
        logCat(param);
    }


    public void onClickCamera(View view) {

        Log.e("onClickCamera","onClickCamera");
        toast("onClickCamera");

        getWriteAndCameraPermission();
    }
}