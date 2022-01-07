package com.github.jfcloud.jos.core.util;

/**
 * @author zj
 * @date 2021/12/31
 */
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.config.RequestConfig.Builder;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpsUtils {
    private static final int MAX_TIMEOUT = 7000;
    private static final Logger logger = LoggerFactory.getLogger(HttpsUtils.class);
    private static PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager();
    private static RequestConfig requestConfig;

    public HttpsUtils() {
    }

    public static InputStream doGet(String url) {
        HttpEntity httpEntity = doGetHttpEntity(url, new HashMap());
        InputStream inputStream = null;

        try {
            inputStream = httpEntity.getContent();
        } catch (IOException var4) {
            logger.error(var4.getMessage());
        }

        return inputStream;
    }

    public static String doGetString(String url, Map<String, Object> params) {
        HttpEntity httpEntity = doGetHttpEntity(url, params);
        String result = null;

        try {
            result = EntityUtils.toString(httpEntity, "UTF-8");
        } catch (IOException var5) {
            logger.error(var5.getMessage());
        }

        return result;
    }

    public static String doGetString(String url) {
        HttpEntity httpEntity = doGetHttpEntity(url, new HashMap());
        String result = null;

        try {
            result = EntityUtils.toString(httpEntity, "UTF-8");
        } catch (IOException var4) {
            logger.error(var4.getMessage());
        }

        return result;
    }

    public static HttpEntity doGetHttpEntity(String url, Map<String, Object> params) {
        StringBuffer param = new StringBuffer();
        int i = 0;

        Iterator result;
        String key;
        for(result = params.keySet().iterator(); result.hasNext(); ++i) {
            key = (String)result.next();
            if (i == 0) {
                param.append("?");
            } else {
                param.append("&");
            }

            param.append(key).append("=").append(params.get(key));
        }

        String apiUrl = url + param;
        result = null;
        key = null;
        CloseableHttpClient httpClient;
        if (apiUrl.startsWith("https")) {
            httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory()).setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        } else {
            httpClient = HttpClients.createDefault();
        }

        InputStream instream = null;
        HttpEntity httpEntity = null;

        try {
            HttpGet httpGet = new HttpGet(apiUrl);
            HttpResponse response = httpClient.execute(httpGet);
            httpEntity = response.getEntity();
        } catch (IOException var11) {
            logger.error(var11.getMessage());
        }

        return httpEntity;
    }

    public static String doPost(String apiUrl) {
        return doPost(apiUrl, (Map)(new HashMap()));
    }

    public static String doPost(String apiUrl, Map<String, Object> params) {
        CloseableHttpClient httpClient = null;
        if (apiUrl.startsWith("https")) {
            httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory()).setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        } else {
            httpClient = HttpClients.createDefault();
        }

        String httpStr = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        try {
            httpPost.setConfig(requestConfig);
            List<NameValuePair> pairList = new ArrayList(params.size());
            Iterator var7 = params.entrySet().iterator();

            while(var7.hasNext()) {
                Entry<String, Object> entry = (Entry)var7.next();
                NameValuePair pair = new BasicNameValuePair((String)entry.getKey(), entry.getValue().toString());
                pairList.add(pair);
            }

            httpPost.setEntity(new UrlEncodedFormEntity(pairList, Charset.forName("UTF-8")));
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            httpStr = EntityUtils.toString(entity, "UTF-8");
        } catch (IOException var18) {
            logger.error(var18.getMessage());
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException var17) {
                    logger.error(var17.getMessage());
                }
            }

        }

        return httpStr;
    }

    public static String doPost(String apiUrl, Object json) {
        CloseableHttpClient httpClient = null;
        if (apiUrl.startsWith("https")) {
            httpClient = HttpClients.custom().setSSLSocketFactory(createSSLConnSocketFactory()).setConnectionManager(connMgr).setDefaultRequestConfig(requestConfig).build();
        } else {
            httpClient = HttpClients.createDefault();
        }

        String httpStr = null;
        HttpPost httpPost = new HttpPost(apiUrl);
        CloseableHttpResponse response = null;

        try {
            httpPost.setConfig(requestConfig);
            StringEntity stringEntity = new StringEntity(json.toString(), "UTF-8");
            stringEntity.setContentEncoding("UTF-8");
            stringEntity.setContentType("application/json");
            httpPost.setEntity(stringEntity);
            response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            httpStr = EntityUtils.toString(entity, "UTF-8");
        } catch (IOException var16) {
            logger.error(var16.getMessage());
        } finally {
            if (response != null) {
                try {
                    EntityUtils.consume(response.getEntity());
                } catch (IOException var15) {
                    logger.error(var15.getMessage());
                }
            }

        }

        return httpStr;
    }

    private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
        SSLConnectionSocketFactory sslsf = null;

        try {
            SSLContext sslContext = (new SSLContextBuilder()).loadTrustMaterial((KeyStore)null, new TrustStrategy() {
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            sslsf = new SSLConnectionSocketFactory(sslContext, new HostnameVerifier() {
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (GeneralSecurityException var2) {
            logger.error(var2.getMessage());
        }

        return sslsf;
    }

    static {
        connMgr.setMaxTotal(100);
        connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());
        connMgr.setValidateAfterInactivity(1000);
        Builder configBuilder = RequestConfig.custom();
        configBuilder.setConnectTimeout(7000);
        configBuilder.setSocketTimeout(7000);
        configBuilder.setConnectionRequestTimeout(7000);
        requestConfig = configBuilder.build();
    }
}
