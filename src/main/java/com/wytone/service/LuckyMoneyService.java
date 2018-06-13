package com.wytone.service;

import java.util.Map;

/**
 * Title: LuckyMoneyService
 * Description: 发送红包
 * @author Tyler
 */
public interface LuckyMoneyService {
	/**
	 *
	 * Description: 根据用户填写的手机号发送红包
	 * @author Tyler
	 * @param mobile
	 * @param openid
	 * @return
	 * @throws Exception
	 */
	 boolean sendLuckyMoney(String openid,String mobile) throws Exception;
	 
	 /**
	  * 
	  * <p>Description: 是否已经领取过红包</p>
	  * @author Tyler
	  * @param params
	  * @return
	  */
	 boolean existSendRecord(Map<String,Object> params);
}
