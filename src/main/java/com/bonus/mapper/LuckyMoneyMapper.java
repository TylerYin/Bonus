package com.bonus.mapper;

import java.util.Map;

import com.bonus.po.Balance;

/**
 * @author Tyler
 */
public interface LuckyMoneyMapper {
    /**
     * Description:存在成功发送红包的记录
     *
     * @param params
     * @return
     * @author Tyler
     */
    Integer existSendRecord(Map<String, Object> params);

    /**
     * Description: 查询商户红包余额
     *
     * @return
     * @author Tyler
     */
    Balance selectCHongbao();
}
