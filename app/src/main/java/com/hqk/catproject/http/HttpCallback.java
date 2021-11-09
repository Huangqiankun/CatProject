package com.hqk.catproject.http;


import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 回调接口的一种实现
 */
public abstract class HttpCallback<Result> implements ICallback{
    @Override
    public void onSuccess(String result) {
        //网络上回来的结果都 在result上
        //1.得到调用者用什么样的javaBean来接收数据
        Class<?> clz=analysisClassInfo(this);
        //2.把String转成javaBean对象交给用户
        Gson gson=new Gson();
        Result objResult=(Result)gson.fromJson(result,clz);
        //3.把结果交给用户
        onSuccess(objResult);
    }

    public abstract void onSuccess(Result objResult);

    /**
     * 通过该功能得到输入参数的实际类型
     */
    private Class<?> analysisClassInfo(Object object){
        //getGenericSuperclass()返回一个类型 对象
        //这个对象可以得到包含原始类型，参数化，数组，类型变量，基本数据类型
        Type getType=object.getClass().getGenericSuperclass();
        Type[] params=((ParameterizedType)getType).getActualTypeArguments();
        return (Class<?>)params[0];
    }

    @Override
    public void onFailure(String e) {

    }
}







