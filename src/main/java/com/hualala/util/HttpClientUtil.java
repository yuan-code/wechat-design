package com.hualala.util;

import com.hualala.common.ResultCode;
import com.hualala.exception.BusinessException;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author YuanChong
 * @create 2019-06-26 22:49
 * @desc
 */
@Log4j2
public class HttpClientUtil {


    private static final RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();


    public static HttpClientUtil.HttpResult post(String url) {
        return post(url, Collections.emptyMap());
    }

    public static HttpClientUtil.HttpResult get(String url) {
        return get(url, Collections.emptyMap());
    }

    public static HttpClientUtil.HttpResult post(String url, Map<String, String> params) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(requestConfig);
        packageParam(params, httpPost);
        return getHttpResult(httpClient, httpPost);
    }

    public static HttpClientUtil.HttpResult get(String url, Map<String, String> params) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = buildHttpGet(url, params);
        httpGet.setConfig(requestConfig);
        return getHttpResult(httpClient, httpGet);
    }

    /**
     * 从网络Url中下载文件
     *
     * @param url
     * @throws IOException
     */
    public static byte[] downLoadFromUrl(String url) {
        CloseableHttpClient httpClient = null;
        InputStream inputStream = null;
        try {
            httpClient = HttpClients.createDefault();
            HttpGet method = new HttpGet(url);
            HttpResponse result = httpClient.execute(method);
            inputStream = result.getEntity().getContent();
            return IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            throw new BusinessException(ResultCode.HTTP_CLIENT_ERROR);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {

            }
            try {
                httpClient.close();
            } catch (IOException e) {

            }
        }
    }


    /**
     * 构建get请求
     * @param url
     * @param params
     * @return
     */
    private static HttpGet buildHttpGet(String url, Map<String, String> params) {
        try {
            // 创建访问的地址
            URIBuilder uriBuilder = new URIBuilder(url);
            uriBuilder.setCharset(StandardCharsets.UTF_8);
            if (params != null) {
                Set<Map.Entry<String, String>> entrySet = params.entrySet();
                for (Map.Entry<String, String> entry : entrySet) {
                    uriBuilder.setParameter(entry.getKey(), entry.getValue());
                }
            }
            return new HttpGet(uriBuilder.build());
        } catch (Exception e) {
            throw new BusinessException(ResultCode.HTTP_CLIENT_ERROR);
        }
    }

    /**
     * 获取结果
     * @param httpClient
     * @param httpMethod
     * @return
     */
    private static HttpResult getHttpResult(CloseableHttpClient httpClient, HttpRequestBase httpMethod) {
        HttpResult result = new HttpResult();
        int i = 0;

        while (i < 3 && result.getStatusCode() != 200) {
            ++i;
            CloseableHttpResponse httpResponse = null;
            try {
                httpResponse = httpClient.execute(httpMethod);
                // 获取返回结果
                if (httpResponse != null && httpResponse.getStatusLine() != null) {
                    if (httpResponse.getEntity() != null) {
                        String content = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
                        result.setContent(content);
                    }
                }
                result.setStatusCode(200);
                return result;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                result.setStatusCode(900);
                result.setThrowable(e);
            } finally {
                release(httpResponse, httpClient);
            }
        }
        return result;
    }

    /**
     * 释放资源
     * @param httpResponse
     * @param httpClient
     */
    private static void release(CloseableHttpResponse httpResponse, CloseableHttpClient httpClient) {
        try {
            if (httpResponse != null) {
                httpResponse.close();
            }
            if (httpClient != null) {
                httpClient.close();
            }
        } catch (Exception e) {
            //do nothing
        }
    }


    /**
     * Description: 封装请求参数
     *
     * @param params
     * @param httpMethod
     * @throws UnsupportedEncodingException
     */
    private static void packageParam(Map<String, String> params, HttpEntityEnclosingRequestBase httpMethod) {
        try {
            // 封装请求参数
            if (params != null) {
                List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                Set<Map.Entry<String, String>> entrySet = params.entrySet();
                for (Map.Entry<String, String> entry : entrySet) {
                    nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                // 设置到请求的http对象中
                httpMethod.setEntity(new UrlEncodedFormEntity(nvps, "utf-8"));
            }
        } catch (Exception e) {
            throw new BusinessException(ResultCode.HTTP_CLIENT_ERROR);
        }
    }



    @Data
    public static class HttpResult {
        private String content;
        private int statusCode = 900;
        private Throwable throwable;

    }
}
