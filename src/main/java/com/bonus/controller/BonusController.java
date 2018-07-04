package com.bonus.controller;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bonus.constants.ShareState;
import com.bonus.service.BonusService;
import com.bonus.util.SessionUtils;
import com.bonus.util.WeiXinUtils;

/**
 * Title: BonusController
 * Description: 发红包
 *
 * @author Tyler
 */
@Controller
@RequestMapping("/bonus")
public class BonusController extends BaseController {
    @Resource
    private BonusService bonusService;

    /**
     * 领取红包
     *
     * @param qrcode
     * @return
     * @throws Exception
     */
    @RequestMapping("/init")
    public String gainBonus(String qrcode) throws Exception {
        //测试用，后续应该删除
        qrcode = WeiXinUtils.QR_CODE;
        String openid = WeiXinUtils.OPEN_ID;
        //Object openid = SessionUtils.getAttribute(WeiXinUtils.OPENID_KEY);

        //没有获取到openid 重新授权进入
        if (openid == null) {
            String reOauth = WeiXinUtils.getOauthLinkURL(ShareState.BONUS.getDesc());
            return "redirect:" + reOauth;
        }

        Map<String, Object> params = new HashMap<>(10);
        params.put("openid", openid);

        //TODO
        //是否已经发送过红包记录
        //boolean existRecord = bonusService.existSendRecord(params);
        //getRequest().setAttribute("existRecord", existRecord);

        getRequest().setAttribute("qrcode", qrcode);
        getRequest().setAttribute("openid", openid);
        return "gainBonus";
    }

    /**
     * 发送红包
     * code = 0 本次领取成功
     * code = 1 输入内容有误
     * code = 2 系统错误
     * code = 3 已经领取过红包
     *
     * @param qrcode 防伪码
     * @return
     * @throws Exception
     * @author Tyler
     */
    @ResponseBody
    @RequestMapping("sendBonus")
    public Map<String, Object> sendBonus(String qrcode) throws Exception {
        Map<String, Object> result = new HashMap<>(10);
        int code = 0;
        if (code == 0) {

            //测试用，后续应该删除
            //Object openID = SessionUtils.getAttribute(WeiXinUtils.OPENID_KEY);
            Object openID = WeiXinUtils.OPEN_ID;
            if (openID == null) {
                result.put("syserr", "用户信息丢失,请重新进入");
                code = 2;
            } else {
                Map<String, Object> params = new HashMap<>(10);
                params.put("qrcode", qrcode);

                //TODO, 后续应该打开这段代码，防止重复领取红包
//                boolean existRecord = bonusService.existSendRecord(params);
//                if (!existRecord) {
                try {
                    boolean sendResult = bonusService.sendBonus((String) openID, qrcode);
                    if (!sendResult) {
                        result.put("wechaterr", "通过微信发送红包失败");
                        code = 2;
                    }
                } catch (Exception e) {
                    result.put("syserr", e.getMessage());
                    code = 2;
                }
//                } else {
//                    code = 3;
//                }
            }
        }
        result.put("code", code);
        return result;
    }
}
