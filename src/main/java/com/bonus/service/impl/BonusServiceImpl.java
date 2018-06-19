package com.bonus.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Date;
import java.util.Map;
import java.util.SortedMap;

import javax.servlet.http.HttpServletRequest;

import com.bonus.service.BonusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.bonus.mapper.HongBaoMapper;
import com.bonus.mapper.LuckyMoneyMapper;
import com.bonus.po.Balance;
import com.bonus.po.Bonus;
import com.bonus.util.HongBaoUtils;
import com.bonus.util.XmlUtils;

/**
 * @author Tyler
 */
@Transactional(rollbackFor = Exception.class)
@Service(value = "bonusService")
public class BonusServiceImpl implements BonusService {
    @Autowired
    private HongBaoMapper hongBaoMapper;

    @Autowired
    private LuckyMoneyMapper luckyMoneyMapper;

    private final static String SUCCESS = "SUCCESS";

    @Override
    public boolean sendLuckyMoney(String openid, String qrcode) throws Exception {
        //TODO 做一些发送红包前的业务

        return sendBonus(openid, qrcode);
    }

    private boolean sendBonus(String openid, String qrcode) throws Exception {
        String billNo = HongBaoUtils.createBillNo(qrcode.substring(1, 11));
        Bonus bonus = getAmount(openid, billNo, qrcode);

        //默认发送结果失败
        bonus.setResult(HongBaoUtils.FAIL);
        int amount = bonus.getAmount();
        SortedMap<String, String> sortMap = HongBaoUtils.createMap(billNo, openid, amount);
        HongBaoUtils.sign(sortMap);
        String requestXML = HongBaoUtils.getRequestXml(sortMap);
        System.out.println("[" + qrcode + "--" + qrcode + "]领红包提交微信的请求:" + requestXML);

        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            InputStream inputStream = request.getSession().getServletContext().getResourceAsStream("/WEB-INF/weixinCert/apiclient_cert.p12");
            String responseXML = HongBaoUtils.post(requestXML, inputStream);
            System.out.println("[" + qrcode + "--" + qrcode + "]微信发送红包处理结果:" + responseXML);

            Map<String, String> resultMap = XmlUtils.parseXml(responseXML);
            String returnCode = resultMap.get("return_code").toString();
            if (SUCCESS.equals(returnCode)) {
                bonus.setResult(HongBaoUtils.SUCCESS);
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
            //不管发送成功不成功,都要更新红包发送记录,失败的商户余额回滚,成功的标志记录为成功
            hongBaoMapper.updateByPrimaryKeySelective(bonus);
        }
        //如果微信发送失败抛出异常的话,会导致收集到的用户信息回滚,此处为了保留用户信息
        return bonus.getResult().equals(HongBaoUtils.SUCCESS) ? true : false;
    }

    /**
     * Description: 获取红包金额
     *
     * @param openid
     * @param billNo
     * @param mobile
     * @return
     * @author Tyler
     */
    private synchronized Bonus getAmount(String openid, String billNo, String mobile) {
        //该用户获取的随机红包金额
        int amount = (int) Math.round(Math.random() * (1000 - 500) + 500);

        //商户的红包总余额
        Balance balance = luckyMoneyMapper.selectCHongbao();

        //如果此次随机金额比商户红包余额还要大,则返回商户红包余额
        if (amount > balance.getBalance()) {
            amount = balance.getBalance();
        }

        Bonus bonus = new Bonus();
        bonus.setAddTime(new Date());
        bonus.setAmount(amount);
        bonus.setOpenid(openid);
        bonus.setResult(HongBaoUtils.LOCK);
        bonus.setBillNo(billNo);
        bonus.setMobile(mobile);

        //先锁定用户领取的金额,防止领取金额超过预算金额
        hongBaoMapper.insertSelective(bonus);
        return bonus;
    }

    @Override
    public boolean existSendRecord(Map<String, Object> params) {
        return luckyMoneyMapper.existSendRecord(params) > 0;
    }
}
