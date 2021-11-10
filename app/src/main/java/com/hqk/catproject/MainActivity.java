package com.hqk.catproject;

import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.hqk.catproject.base.BaseActivity;
import com.hqk.catproject.bean.CatInfo;
import com.hqk.catproject.util.Base64Util;
import com.hqk.catproject.util.FileUtil;
import com.hqk.catproject.util.HttpUtil;
import com.hqk.catproject.util.SendImageUtil;
import com.huantansheng.easyphotos.EasyPhotos;
import com.huantansheng.easyphotos.callback.SelectCallback;
import com.huantansheng.easyphotos.models.album.entity.Photo;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity {

    ImageView imageCat;

    TextView textCatName;

    private String accessToken = "";

    Handler handler = null;
    @Override
    public int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {
        imageCat = findViewById(R.id.image_cat);
        textCatName = findViewById(R.id.text_cat_name);
        handler = new Handler(this.getMainLooper(), this);
        Log.e("MainActivity", "initData");
        getToken();
    }



    @Override
    public boolean handleMessage(@NonNull @NotNull Message message) {
        logCat("handleMessage  message.what "+message.what);
        switch (message.what) {
            case 1001:

                /*
                * {
                    "refresh_token": "25.6a79e94b2cc92571e57c12f16b3d062d.315360000.1951874992.282335-25104979",
                    "expires_in": 2592000,
                    "session_key": "9mzdA5/DKxagseJ/y/Gmyi+PuJzy8+QyeISLV0LFDFpMMe67P4NW6v+6lTIjnLlNpWcoZbzSXZqGqflBX913xRNT9mmTug==",
                    "access_token": "24.8511933b8b244e2d0863ebb0d43ca5e2.2592000.1639106992.282335-25104979",
                    "scope": "public vis-classify_dishes vis-classify_car brain_all_scope vis-classify_animal vis-classify_plant brain_object_detect brain_realtime_logo brain_dish_detect brain_car_detect brain_animal_classify brain_plant_classify brain_ingredient brain_advanced_general_classify brain_custom_dish brain_poi_recognize brain_vehicle_detect brain_redwine brain_currency brain_vehicle_damage brain_multi_ object_detect wise_adapt lebo_resource_base lightservice_public hetu_basic lightcms_map_poi kaidian_kaidian ApsMisTest_Test权限 vis-classify_flower lpq_开放 cop_helloScope ApsMis_fangdi_permission smartapp_snsapi_base smartapp_mapp_dev_manage iop_autocar oauth_tp_app smartapp_smart_game_openapi oauth_sessionkey smartapp_swanid_verify smartapp_opensource_openapi smartapp_opensource_recapi fake_face_detect_开放Scope vis-ocr_虚拟人物助理 idl-video_虚拟人物助理 smartapp_component smartapp_search_plugin avatar_video_test b2b_tp_openapi b2b_tp_openapi_online",
                    "session_secret": "94ec45de181d151310d55efb96aa02b3"
                  }
                * */

                accessToken = (String) message.obj;
                toast("初始化完成！");
                break;
            case 1002:
                String result = (String) message.obj;
                //{"result":[{"name":"孟买猫","score":"0.870167"},{"name":"德文帝王猫","score":"0.0221098"},{"name":"玄猫","score":"0.0211879"},{"name":"哈瓦那棕猫","score":"0.0176104"},{"name":"科拉特猫","score":"0.00979233"},{"name":"俄罗斯蓝猫","score":"0.00612372"}],"log_id":1458311455921558025}
                logCat("result: "+result);
                parseCatInfo(result);
                break;
        }
        return super.handleMessage(message);
    }

    private void parseCatInfo(String result) {
        CatInfo catInfo = gson.fromJson(result, CatInfo.class);
        List<CatInfo.ResultBean> listCat = catInfo.getResult();
        if (listCat.size() == 0) {
            toast("猫脸识别识别！");
            return;
        }
        textCatName.setText(listCat.get(0).getName());
    }

    @Override
    protected void doPermissionSuccess() {
        EasyPhotos.createCamera(this).start(new SelectCallback() {
            @Override
            public void onResult(ArrayList<Photo> photos, ArrayList<String> paths, boolean isOriginal) {
                logCat("doPhotoLast onResult : ");
                if (paths == null || paths.size() == 0)
                    return;
                doPhotoLast(paths.get(0));
            }
        });
    }


    private void getToken() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String token = getAuth(BuildConfig.AK, BuildConfig.SECRET);
                logCat("token  "+token);
                Message message = handler.obtainMessage();
                message.obj = token;
                message.what = 1001;
                handler.sendMessage(message);

            }
        }).start();
    }


    /**
     * 获取API访问token
     * 该token有一定的有效期，需要自行管理，当失效时需重新获取.
     *
     * @param ak - 百度云官网获取的 API Key
     * @param sk - 百度云官网获取的 Securet Key
     * @return assess_token 示例：
     * "24.460da4889caad24cccdb1fea17221975.2592000.1491995545.282335-1234567"
     */
    public static String getAuth(String ak, String sk) {
        // 获取token地址
        String authHost = BuildConfig.GET_OAUTH_TOKEN_URL;
        String getAccessTokenUrl = authHost
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + ak
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + sk;
        try {
            URL realUrl = new URL(getAccessTokenUrl);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.err.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            /**
             * 返回结果示例
             */
            System.err.println("result:" + result);
            JSONObject jsonObject = new JSONObject(result);
            String access_token = jsonObject.getString("access_token");
            return access_token;
        } catch (Exception e) {
            System.err.printf("获取token失败！");
            e.printStackTrace(System.err);
        }
        return null;
    }


    private void doPhotoLast(String path) {
        logCat(path);
        SendImageUtil.compressImage(path);
        Glide.with(this).load(path).into(imageCat);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String result = requestCat(path);
                Message message = handler.obtainMessage();
                message.what = 1002;
                message.obj = result;
                handler.sendMessage(message);
            }
        }).start();
    }

    public String requestCat(String path) {
        String url = BuildConfig.GET_ANIMAL_URL;
        byte[] imgData = new byte[0];
        String result = null;
        try {
            imgData = FileUtil.readFileByBytes(path);
            //  字节数组转base64字符串
            String base64Str = Base64Util.encode(imgData);
            logCat(base64Str);
            String imgParam = URLEncoder.encode(base64Str, "UTF-8");
            String param = "image=" + imgParam;


            try {
                result = HttpUtil.post(url, accessToken, param);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    public void onClickCamera(View view) {
        if (accessToken == null || accessToken.length() == 0) {
            toast("初始化未完成！");
            return;
        }
        getWriteAndCameraPermission();
    }
}