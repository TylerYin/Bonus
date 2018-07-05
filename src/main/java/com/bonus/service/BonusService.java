package com.bonus.service;

import java.util.Map;

/**
 * Title: BonusService
 * Description: 发送红包
 *
 * @author Tyler
 */
public interface BonusService {
    /**
     * Description: 发送红包
     *
     * @param qrcode
     * @param openid
     * @return
     * @throws Exception
     * @author Tyler
     */
    boolean sendBonus(String openid, String qrcode) throws Exception;

    /**
     * Description: 是否已经领取过红包
     *
     * @param params
     * @return
     * @author Tyler
     */
    boolean existSendRecord(Map<String, Object> params);

    /**
     * @param openid
     * @param amount
     * @param qrcode
     * @param desc
     * @return
     * @Description：微信支付，企业向个人付款
     * @author Tyler
     */
    Map<String, String> transfer(String openid, String appId, String mchId, String qrcode, int amount, String desc);
}
