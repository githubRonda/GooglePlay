package com.ronda.googleplay.http;

import android.util.Log;

import com.ronda.googleplay.utils.IOUtils;
import com.ronda.googleplay.utils.LogUtils;
import com.ronda.googleplay.utils.MD5Encoder;
import com.ronda.googleplay.utils.StringUtils;
import com.ronda.googleplay.utils.UIUtils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.SyncBasicHttpContext;

public class HttpHelper {

    public static final String URL = "http://127.0.0.1:8090/";

    /**
     * get请求，获取返回字符串内容
     */
    public static String get(String requestURI, Map<String, Object> params) {
        String completeUrl = URL + requestURI + parseParams(params);

        String cache = getCache(completeUrl);
        if (!StringUtils.isEmpty(cache)) { // 若有缓存且没有失效, 则直接返回缓存数据,否则请求网络加载新数据
            return cache;
        }

        HttpGet httpGet = new HttpGet(completeUrl);
        HttpResult httpResult = execute(completeUrl, httpGet);

        if (httpResult != null) {
            String result = httpResult.getString();
            //写缓存
            if (!StringUtils.isEmpty(result)) {
                setCache(completeUrl, result);
            }
            return result;
        }
        return null;
    }

    /**
     * post请求，获取返回字符串内容
     */
    public static HttpResult post(String url, byte[] bytes) {
        HttpPost httpPost = new HttpPost(url);
        ByteArrayEntity byteArrayEntity = new ByteArrayEntity(bytes);
        httpPost.setEntity(byteArrayEntity);
        return execute(url, httpPost);
    }

    /**
     * 下载
     */
    public static HttpResult download(String url) {
        HttpGet httpGet = new HttpGet(url);
        return execute(url, httpGet);
    }

    /**
     * 执行网络访问
     */
    private static HttpResult execute(String url, HttpRequestBase requestBase) {
        boolean isHttps = url.startsWith("https://");//判断是否需要采用https
        AbstractHttpClient httpClient = HttpClientFactory.create(isHttps);
        HttpContext httpContext = new SyncBasicHttpContext(new BasicHttpContext());
        HttpRequestRetryHandler retryHandler = httpClient.getHttpRequestRetryHandler();//获取重试机制
        int retryCount = 0;
        boolean retry = true;
        // 若网络不佳时, 会尝试请求多次
        while (retry) {
            try {
                HttpResponse response = httpClient.execute(requestBase, httpContext);//访问网络
                if (response != null) {
                    return new HttpResult(response, httpClient, requestBase);
                }
            } catch (Exception e) {
                IOException ioException = new IOException(e.getMessage());
                retry = retryHandler.retryRequest(ioException, ++retryCount, httpContext);//把错误异常交给重试机制，以判断是否需要采取重试
                LogUtils.e(e);
            }
        }
        return null;
    }

    /**
     * http的返回结果的封装，可以直接从中获取返回的字符串或者流
     */
    public static class HttpResult {
        private HttpResponse mResponse;
        private InputStream mIn;
        private String mStr;
        private HttpClient mHttpClient;
        private HttpRequestBase mRequestBase;

        public HttpResult(HttpResponse response, HttpClient httpClient, HttpRequestBase requestBase) {
            mResponse = response;
            mHttpClient = httpClient;
            mRequestBase = requestBase;
        }

        public int getCode() {
            StatusLine status = mResponse.getStatusLine();
            return status.getStatusCode();
        }

        /**
         * 从结果中获取字符串，一旦获取，会自动关流，并且把字符串保存，方便下次获取
         */
        public String getString() {
            if (!StringUtils.isEmpty(mStr)) {
                return mStr;
            }
            InputStream inputStream = getInputStream();
            ByteArrayOutputStream out = null;
            if (inputStream != null) {
                try {
                    out = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024 * 4];
                    int len = -1;
                    while ((len = inputStream.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                    }
                    byte[] data = out.toByteArray();
                    mStr = new String(data, "utf-8");
                } catch (Exception e) {
                    LogUtils.e(e);
                } finally {
                    IOUtils.close(out);
                    close();
                }
            }
            return mStr;
        }

        /**
         * 获取流，需要使用完毕后调用close方法关闭网络连接
         */
        public InputStream getInputStream() {
            if (mIn == null && getCode() < 300) {
                HttpEntity entity = mResponse.getEntity();
                try {
                    mIn = entity.getContent();
                } catch (Exception e) {
                    LogUtils.e(e);
                }
            }
            return mIn;
        }

        /**
         * 关闭网络连接
         */
        public void close() {
            if (mRequestBase != null) {
                mRequestBase.abort();
            }
            IOUtils.close(mIn);
            if (mHttpClient != null) {
                mHttpClient.getConnectionManager().closeExpiredConnections();
            }
        }
    }

    /**
     * @param map 值可以为String 或 int, 所以使用 Object
     * @return
     */
    public static String parseParams(Map<String, Object> map) {

        StringBuilder sb = new StringBuilder("?");
        Set<Map.Entry<String, Object>> entries = map.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (StringUtils.isEmpty(key) || value == null || StringUtils.isEmpty(value.toString())) {
                continue;
            }

            sb.append(key + "=" + value.toString() + "&");
        }
        return sb.substring(0, sb.length() - 1);
    }

    /**
     * 写缓存
     * 1. 以url为文件名, 以json为文件内容,保存在本地
     * 建议url最好使用md5加密, 因为文件名是不能有像 "/","?" 等特殊字符存在的
     * 2. 有设置缓存有效期
     *
     * @param url
     * @param json
     */
    public static void setCache(String url, String json) {
        // 以url为文件名, 以json为文件内容,保存在本地
        File cacheDir = UIUtils.getContext().getCacheDir(); //本应用的缓存文件夹:/data/data/com.ronda.googleplay/cache
        Log.d("Liu", "cacheDir: " + cacheDir);

        FileWriter writer = null;
        try {
            String cacheName = MD5Encoder.encode(url);
            //生成缓存文件
            File cacheFile = new File(cacheDir, cacheName);

            writer = new FileWriter(cacheFile);

            // 缓存失效截止日期
            long deadTime = System.currentTimeMillis() + 30 * 60 * 1000; // 半小时的有效期
            writer.write(deadTime + "\n"); // 在第一行写入缓存有效期
            writer.write(json); //写入json内容
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(writer);
        }
    }

    /**
     * 读缓存
     */
    public static String getCache(String url) {
        // 以url为文件名, 以json为文件内容,保存在本地
        File cacheDir = UIUtils.getContext().getCacheDir(); //本应用的缓存文件夹
        Log.d("Liu", "cacheDir: " + cacheDir);

        BufferedReader reader = null;

        try {
            String cacheName = MD5Encoder.encode(url);
            //生成缓存文件
            File cacheFile = new File(cacheDir, cacheName);

            //判断缓存是否存在
            if (cacheFile.exists()) {
                // 判断缓存是否有效
                reader = new BufferedReader(new FileReader(cacheFile));
                String deadTimeStr = reader.readLine(); // 读取第一行的缓存有效期
                long deadTime = Long.parseLong(deadTimeStr);

                if (System.currentTimeMillis() < deadTime) { //当前时间小于截止时间,则缓存有效
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {// 因为前面已经readLine了一次,所以这次是从第二行开始的
                        sb.append(line);
                    }
                    return sb.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(reader);
        }
        return null;
    }
}
