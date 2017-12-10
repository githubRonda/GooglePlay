package com.ronda.googleplay.http.gson;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Author: Ronda(1575558177@qq.com)
 * Date: 2017/03/13
 * Version: v1.0
 */
public class GsonUtil {
    /**
     * 使用这个gson来序列化和反序列化实体类type，可以避免null值的情况
     * Bean bean = gson.fromJson(jsonStr, type);// 把JsonStr转成bean对象时，可以把null值变为空字符串
     *
     * String jsonStr = gson.toJson(bean.type); // 把bean对象转成jsonStr时，可以去掉值为null的属性字段
     *
     * @return
     */
    public static Gson getGson(){
        return new GsonBuilder().registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory()).create();
    }
    //new Gson().fromJson(response, new TypeToken<RegistBean>(){}.getType());
    //RegistBean registBean = new Gson().fromJson(response, RegistBean.class);
}
