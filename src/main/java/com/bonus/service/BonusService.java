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
}
