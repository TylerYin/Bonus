package com.bonus.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * @author Tyler
 * @version 1.0
 */
public class BonusUtils {
    /**
     * /商户号
     */
    public static final String MCH_ID = "xxx";

    /**
     * 公众账号appid
     */
    public static final String WXAPP_ID = WeiXinUtils.APP_ID;

    /**
     * 提供方名称
     */
    public static final String NICK_NAME = "xxx";

    /**
     * 商户名称
     */
    public static final String SEND_NAME = "xxx";

    /**
     * 红包最小金额 单位:分
     */
    public static final int HONGBAO_MIN_VALUE = 100;

    /**
     * 红包最大金额 单位:分
     */
    public static final int HONGBAO_MAX_VALUE = 100;

    /**
     * 红包发放人数
     */
    public static final int TOTAL_NUM = 1;

    /**
     * 红包祝福语
     */
    public static final String WISHING = "大吉大利";

    /**
     * 调用接口的机器IP，测试期间，给一个默认值，后续应该写一个方法来获取用户IP
     */
    public static final String CLIENT_IP = "127.0.0.1";

    /**
     * 活动名称
     */
    public static final String ACT_NAME = "吃鸡了";

    /**
     * 备注
     */
    public static final String REMARK = "发红包测试";

    /**
     * 秘钥, 微信商户支付密钥
     */
    public static final String KEY = "xxx";

    /**
     * 领取失败
     */
    public static final int FAIL = 0;

    /**
     * 领取成功
     */
    public static final int SUCCESS = 1;

    /**
     * 已在余额表中锁定该用户的余额,防止领取的红包金额大于预算
     */
    public static final int LOCK = 2;

    /**
     * 对请求参数名ASCII码从小到大排序后签名
     *
     * @param params
     * @return
     */
    public static void sign(SortedMap<String, String> params) {
        Set<Entry<String, String>> entries = params.entrySet();
        Iterator<Entry<String, String>> it = entries.iterator();
        String result = "";
        while (it.hasNext()) {
            Entry<String, String> entry = it.next();
            result += entry.getKey() + "=" + entry.getValue() + "&";
        }
        result += "key=" + KEY;
        String sign = null;
        try {
            sign = Md5Utils.md5(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        params.put("sign", sign);
    }

    public static String getRequestXml(SortedMap<String, String> params) {
        StringBuffer sb = new StringBuffer();
        sb.append("<xml>");
        Set es = params.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Entry entry = (Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            if ("nick_name".equalsIgnoreCase(k) || "send_name".equalsIgnoreCase(k) || "wishing".equalsIgnoreCase(k) || "act_name".equalsIgnoreCase(k) || "remark".equalsIgnoreCase(k) || "sign".equalsIgnoreCase(k)) {
                sb.append("<" + k + ">" + "<![CDATA[" + v + "]]></" + k + ">");
            } else {
                sb.append("<" + k + ">" + v + "</" + k + ">");
            }
        }
        sb.append("</xml>");
        return sb.toString();
    }

    public static SortedMap<String, String> createMap(String billNo, String openid, int amount) {
        SortedMap<String, String> params = new TreeMap<>();
        params.put("act_name", ACT_NAME);
        params.put("client_ip", CLIENT_IP);
        params.put("mch_billno", billNo);
        params.put("mch_id", MCH_ID);
        params.put("nonce_str", createNonceStr());
        params.put("re_openid", openid);
        params.put("remark", REMARK);
        params.put("send_name", SEND_NAME);
        params.put("total_amount", amount + "");
        params.put("total_num", TOTAL_NUM + "");
        params.put("wishing", WISHING);
        params.put("wxappid", WXAPP_ID);
        params.put("nick_name", NICK_NAME);
        params.put("min_value", amount + "");
        params.put("max_value", amount + "");
        return params;
    }

    /**
     * 生成随机字符串
     *
     * @return
     */
    public static String createNonceStr() {
        return UUID.randomUUID().toString().toUpperCase().replace("-", "");
    }

    /**
     * 生成商户订单号
     *
     * @param userId 该用户的userID
     * @return
     */
    public static String createBillNo(String userId) {
        Date dt = new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyymmdd");
        String nowTime = df.format(dt);
        int length = 10 - userId.length();
        return MCH_ID + nowTime + userId + getRandomNum(length);
    }

    /**
     * 生成特定位数的随机数字
     *
     * @param length
     * @return
     */
    public static String getRandomNum(int length) {
        String val = "";
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            val += String.valueOf(random.nextInt(10));
        }
        return val;
    }

    /**
     * 发送post请求
     *
     * @param requestXML
     * @param instream
     * @return
     * @throws NoSuchAlgorithmException
     * @throws CertificateException
     * @throws IOException
     * @throws KeyManagementException
     * @throws UnrecoverableKeyException
     * @throws KeyStoreException
     */
    public static String post(String requestXML, InputStream instream) throws NoSuchAlgorithmException, CertificateException, IOException, KeyManagementException, UnrecoverableKeyException, KeyStoreException {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try {
            keyStore.load(instream, MCH_ID.toCharArray());
        } finally {
            instream.close();
        }
        SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, MCH_ID.toCharArray()).build();
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext,
                new String[]{"TLSv1"},
                null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier());
        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        String result = "";
        try {
            HttpPost httpPost = new HttpPost("https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack");
            StringEntity reqEntity = new StringEntity(requestXML, "UTF-8");

            // 设置类型 
            reqEntity.setContentType("application/x-www-form-urlencoded");
            httpPost.setEntity(reqEntity);
            CloseableHttpResponse response = httpclient.execute(httpPost);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String text;
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(entity.getContent(), "UTF-8"));
                    while ((text = bufferedReader.readLine()) != null) {
                        result += text;
                    }
                }
                EntityUtils.consume(entity);
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
        return result;
    }
}