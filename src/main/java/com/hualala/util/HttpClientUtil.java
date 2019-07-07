package com.hualala.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author YuanChong
 * @create 2019-06-26 22:49
 * @desc
 */
public class HttpClientUtil {
    private static final HttpClientUtil httpClient = new HttpClientUtil();
    private volatile Logger logger = LoggerFactory.getLogger(this.getClass());

    private HttpClientUtil() {
    }

    public static HttpClientUtil getInstance() {
        return httpClient;
    }

    public HttpClientUtil.HttpResult post(String url, String content, String contentType) {
        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = null;
        HttpURLConnection conn = null;
        HttpClientUtil.HttpResult result = new HttpClientUtil.HttpResult();
        int i = 0;

        while (i < 3 && result.getStatusCode() != 200) {
            ++i;

            try {
                URL postUrl = new URL(url);
                conn = (HttpURLConnection) postUrl.openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(20000);
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setRequestMethod("POST");
                conn.setUseCaches(false);
                conn.setInstanceFollowRedirects(true);
                if (contentType != null && contentType.length() > 0) {
                    conn.setRequestProperty("Content-Type", contentType);
                } else {
                    conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                }

                conn.connect();
                DataOutputStream out = new DataOutputStream(conn.getOutputStream());
                out.write(content.getBytes("UTF-8"));
                out.flush();
                out.close();
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                result.setContent(buffer.toString());
                result.setStatusCode(200);
            } catch (Exception var20) {
                this.logger.error(var20.getMessage(), var20);
                var20.printStackTrace();
                result.setStatusCode(900);
                result.setT(var20);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException var19) {
                        this.logger.error(var19.getMessage(), var19);
                    }
                }

                if (conn != null) {
                    conn.disconnect();
                }

            }
        }

        return result;
    }

    public HttpClientUtil.HttpResult post(String url, String content) {
        return this.post(url, content, "application/json; charset=utf-8");
    }

    public HttpClientUtil.HttpResult post(String url) {
        return this.post(url, "", "application/json; charset=utf-8");
    }

    public HttpClientUtil.HttpResult postWithUrlDecode(String url, String conent) {
        return this.post(url, conent, "application/x-www-form-urlencoded");
    }

    public static class HttpResult {
        private String content;
        private int statusCode = 900;
        private Throwable t;

        public HttpResult() {
        }

        public Throwable getT() {
            return this.t;
        }

        public void setT(Throwable t) {
            this.t = t;
        }

        public String getContent() {
            return this.content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getStatusCode() {
            return this.statusCode;
        }

        public void setStatusCode(int statusCode) {
            this.statusCode = statusCode;
        }

        @Override
        public String toString() {
            return "statusCode :" + this.statusCode + " content :" + this.content;
        }
    }
}
