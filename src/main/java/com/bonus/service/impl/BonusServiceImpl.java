package com.bonus.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.*;

import javax.net.ssl.SSLContext;
import javax.servlet.http.HttpServletRequest;

import com.bonus.mapper.HongBaoMapper;
import com.bonus.mapper.LuckyMoneyMapper;
import com.bonus.service.BonusService;
import com.bonus.util.ConfigUtils;
import com.bonus.util.Md5Utils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.bonus.po.Bonus;
import com.bonus.util.BonusUtils;
import com.bonus.util.XmlUtils;

/**
 * @author Tyler
 */
@Transactional(rollbackFor = Exception.class)
@Service(value = "bonusService")
public class BonusServiceImpl implements BonusService {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private HongBaoMapper hongBaoMapper;

    @Autowired
    private LuckyMoneyMapper luckyMoneyMapper;

    private final static String SUCCESS = "SUCCESS";

    @Override
    public boolean sendBonus(String openid, String qrcode) throws Exception {
        //TODO 做一些发送红包前的业务

        //发送红包
        return send(openid, qrcode);
    }

    /**
     * 方式1
     * 普通红包
     *
     * @param openid
     * @param qrcode
     * @return
     * @throws Exception
     */
    private boolean send(String openid, String qrcode) throws Exception {
        String billNo = BonusUtils.createBillNo(qrcode.substring(1, 11));
        Bonus bonus = getAmount(openid, billNo, qrcode);

        //默认发送结果失败
        bonus.setResult(Integer.valueOf(ConfigUtils.getConfig("bonus.status.fail")));
        int amount = bonus.getAmount();
        SortedMap<String, String> sortMap = BonusUtils.createMap(billNo, openid, amount);
        BonusUtils.sign(sortMap);
        String requestXML = BonusUtils.getRequestXml(sortMap);
        System.out.println("[" + qrcode + "--" + qrcode + "]提交微信领红包的请求:" + requestXML);
        logger.info("提交微信领红包的请求:" + requestXML);

        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            InputStream inputStream = request.getSession().getServletContext().getResourceAsStream("/WEB-INF/weixinCert/apiclient_cert.p12");
            String responseXML = BonusUtils.post(requestXML, inputStream);
            System.out.println("[" + qrcode + "--" + qrcode + "]微信发送红包处理结果:" + responseXML);
            logger.info("微信发送红包处理结果:" + responseXML);

            Map<String, String> resultMap = XmlUtils.parseXml(responseXML);
            String returnCode = resultMap.get("result_code").toString();
            if (SUCCESS.equals(returnCode)) {
                bonus.setResult(Integer.valueOf(ConfigUtils.getConfig("bonus.status.success")));
            }
            bonus.setRemark(responseXML);
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //TODO, 后续应该打开这段代码，需要插入红包领取记录
            //不管发送成功不成功,都要更新红包发送记录,失败的商户余额回滚,成功的标志记录为成功
            //hongBaoMapper.updateByPrimaryKeySelective(bonus);
        }
        //如果微信发送失败抛出异常的话,会导致收集到的用户信息回滚,此处为了保留用户信息
        return bonus.getResult().equals(Integer.valueOf(ConfigUtils.getConfig("bonus.status.success"))) ? true : false;
    }

    /**
     * 方式2
     * 企业商户直接给微信用户打钱到零钱帐户
     *
     * @param openid 收款人的openID(微信的openID)
     * @param amount 付款金额
     * @param qrcode 防伪码
     * @param desc   付款描述
     * @return map{state:SUCCESS/FAIL}{payment_no:'支付成功后，微信返回的订单号'}{payment_time:'支付成功的时间'}{err_code:'支付失败后，返回的错误代码'}{err_code_des:'支付失败后，返回的错误描述 ' }
     * @Description：微信支付，企业向个人付款
     */
    @Override
    public Map<String, String> transfer(String openid, String appId, String mchId, String qrcode, int amount, String desc) {
        String partnerTradeNo = BonusUtils.createBillNo(qrcode.substring(1, 11));
        Map<String, String> map = new HashMap<>(10);
        try {
            String url = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers";
            InetAddress ia = InetAddress.getLocalHost();
            String ip = ia.getHostAddress();
            String uuid = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");

            // 设置支付参数
            SortedMap<Object, Object> signParams = new TreeMap<>();
            signParams.put("mch_appid", appId);
            signParams.put("mchid", mchId);
            signParams.put("nonce_str", uuid);

            //订单号(系统业务逻辑用到的订单号)
            signParams.put("partner_trade_no", partnerTradeNo);
            signParams.put("openid", openid);

            /**
             * FORCE_CHECK：强校验真实姓名（未实名认证的用户会校验失败，无法转账）
             * OPTION_CHECK：针对已实名认证的用户才校验真实姓名（未实名认证用户不校验，可以转账成功）
             */
            signParams.put("check_name", "NO_CHECK");
            signParams.put("amount", amount);
            signParams.put("desc", desc);
            signParams.put("spbill_create_ip", ip);

            String data = "<xml><mch_appid>";
            data += appId + "</mch_appid><mchid>";
            data += mchId + "</mchid><nonce_str>";
            data += uuid + "</nonce_str><partner_trade_no>";
            data += partnerTradeNo + "</partner_trade_no><openid>";
            data += openid + "</openid><check_name>NO_CHECK</check_name><amount>";

            // 企业付款金额，单位为分
            data += amount + "</amount><desc>";

            // 企业付款操作说明信息
            data += desc + "</desc><spbill_create_ip>";

            // 调用接口的机器Ip地址
            data += ip + "</spbill_create_ip><sign>";

            // 生成支付签名，要采用URLENCODER的原始值进行MD5算法！
            data += createSign(signParams) + "</sign></xml>";

            // 获取证书，发送POST请求
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            InputStream in = request.getSession().getServletContext().getResourceAsStream("/WEB-INF/weixinCert/apiclient_cert.p12");
            keyStore.load(in, mchId.toCharArray());
            in.close();

            SSLContext sslcontext = SSLContexts.custom().loadKeyMaterial(keyStore, mchId.toCharArray()).build();
            SSLConnectionSocketFactory sslSF = new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1"}, null, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
            CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(sslSF).build();
            HttpPost httPost = new HttpPost(url);
            httPost.addHeader("Connection", "keep-alive");
            httPost.addHeader("Accept", "*/*");
            httPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            httPost.addHeader("Host", "api.mch.weixin.qq.com");
            httPost.addHeader("X-Requested-With", "XMLHttpRequest");
            httPost.addHeader("Cache-Control", "max-age=0");
            httPost.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0) ");
            httPost.setEntity(new StringEntity(data, "UTF-8"));

            CloseableHttpResponse response = httpclient.execute(httPost);
            HttpEntity entity = response.getEntity();
            String jsonStr = EntityUtils.toString(response.getEntity(), "UTF-8");
            EntityUtils.consume(entity);

            // 把返回的字符串解释成DOM节点
            Document dom = DocumentHelper.parseText(jsonStr);
            Element root = dom.getRootElement();
            String returnCode = root.element("result_code").getText();
            if (StringUtils.equals(returnCode, "SUCCESS")) {
                // 获取支付流水号
                String payment_no = root.element("payment_no").getText();
                // 获取支付时间
                String payment_time = root.element("payment_time").getText();
                map.put("state", returnCode);
                map.put("payment_no", payment_no);
                map.put("payment_time", payment_time);
            } else {
                // 获取错误代码
                String err_code = root.element("err_code").getText();
                // 获取错误描述
                String err_code_des = root.element("err_code_des").getText();
                map.put("state", returnCode);
                map.put("err_code", err_code);
                map.put("err_code_des", err_code_des);
            }
            return map;
        } catch (DocumentException ex) {
            ex.printStackTrace();
            return map;
        } catch (UnrecoverableKeyException ex) {
            ex.printStackTrace();
            return map;
        } catch (KeyManagementException ex) {
            ex.printStackTrace();
            return map;
        } catch (KeyStoreException ex) {
            ex.printStackTrace();
            return map;
        } catch (CertificateException ex) {
            ex.printStackTrace();
            return map;
        } catch (IOException ex) {
            ex.printStackTrace();
            return map;
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
            return map;
        }
    }

    private static String createSign(SortedMap<Object, Object> parameters) {
        StringBuffer sb = new StringBuffer();
        Set<Map.Entry<Object, Object>> es = parameters.entrySet();
        Iterator<Map.Entry<Object, Object>> it = es.iterator();
        while (it.hasNext()) {
            Map.Entry<Object, Object> entry = it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + ConfigUtils.getConfig("bonus.mch.key"));
        String sign = null;
        try {
            sign = Md5Utils.md5(sb.toString()).toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sign;
    }

    /**
     * Description: 获取红包金额
     *
     * @param openid
     * @param billNo
     * @param qrcode
     * @return
     * @author Tyler
     */
    private synchronized Bonus getAmount(String openid, String billNo, String qrcode) {
        //该用户获取红包金额，单位为分，微信红包默认最少需要发送1块钱的红包
        int amount = 100;
        Bonus bonus = new Bonus();
        bonus.setAddTime(new Date());
        bonus.setAmount(amount);
        bonus.setOpenid(openid);
        bonus.setResult(Integer.valueOf(ConfigUtils.getConfig("bonus.lock")));
        bonus.setBillNo(billNo);
        bonus.setQrcode(qrcode);
        return bonus;
    }

    @Override
    public boolean existSendRecord(Map<String, Object> params) {
        return luckyMoneyMapper.existSendRecord(params) > 0;
    }
}
