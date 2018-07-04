package com.bonus.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.bonus.mapper.HongBaoMapper;
import com.bonus.mapper.LuckyMoneyMapper;
import com.bonus.service.BonusService;
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

    private boolean send(String openid, String qrcode) throws Exception {
        String billNo = BonusUtils.createBillNo(qrcode.substring(1, 11));
        Bonus bonus = getAmount(openid, billNo, qrcode);

        //默认发送结果失败
        bonus.setResult(BonusUtils.FAIL);
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
                bonus.setResult(BonusUtils.SUCCESS);
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
        return bonus.getResult().equals(BonusUtils.SUCCESS) ? true : false;
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
        bonus.setResult(BonusUtils.LOCK);
        bonus.setBillNo(billNo);
        bonus.setQrcode(qrcode);
        return bonus;
    }

    @Override
    public boolean existSendRecord(Map<String, Object> params) {
        return luckyMoneyMapper.existSendRecord(params) > 0;
    }
}
