package com.wytone.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wytone.constants.ShareState;
import com.wytone.service.BonusService;
import com.wytone.util.SessionUtils;
import com.wytone.util.WeiXinUtils;

/**
 * Title: LuckyMoneyController
 * Description: 发红包
 *
 * @author Tyler
 */
@Controller
@RequestMapping("/luckymoney")
public class BonusController extends BaseController {
    @Resource
    private BonusService bonusService;

    private final static Pattern PATTERN = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");

    /**
     * Description: 去报名领取红包
     *
     * @return
     * @throws Exception
     * @author Tyler
     */
    @RequestMapping("/signup")
    public String signup() throws Exception {
        SessionUtils.setAttribute(WeiXinUtils.OPENID_KEY, "oVTYvt3wsDfxCTSyo");
        Object openid = SessionUtils.getAttribute(WeiXinUtils.OPENID_KEY);
        if (openid == null) {
            //没有获取到openid 重新授权进入
            String reOauth = WeiXinUtils.getOauthLinkURL(ShareState.LUCKYMONEY.getDesc());
            return "redirect:" + reOauth;
        }

        Map<String, Object> params = new HashMap<>(10);
        params.put("openid", openid);

        //是否已经发送过红包记录
        boolean existRecord = bonusService.existSendRecord(params);
        getRequest().setAttribute("existRecord", existRecord);
        getRequest().setAttribute("openid", openid);
        return "activity/signup";
    }

    /**
     * <p>Description: 发送红包</p>
     * <p>code = 0 本次领取成功</p>
     * <p>code = 1 输入内容有误</p>
     * <p>code = 2 系统错误</p>
     * <p>code = 3 已经领取过红包</p>
     *
     * @param mobile 注册手机号
     * @return
     * @throws Exception
     * @author Tyler
     */
    @ResponseBody
    @RequestMapping("init")
    public Map<String, Object> initHeHe(String mobile) throws Exception {
        Map<String, Object> result = new HashMap<>(10);
        int code = 0;

        if (StringUtils.isBlank(mobile)) {
            result.put("mobileerr", "请填写您的手机号");
            code = 1;
        } else if (!PATTERN.matcher(mobile).find()) {
            result.put("mobileerr", "手机号格式不正确");
            code = 1;
        }

        if (code == 0) {
            Object openid = SessionUtils.getAttribute(WeiXinUtils.OPENID_KEY);
            if (openid == null) {
                result.put("syserr", "用户信息丢失,请重新进入");
                code = 2;
            } else {
                Map<String, Object> params = new HashMap<>(10);
                params.put("mobile", mobile);
                boolean existRecord = bonusService.existSendRecord(params);
                if (!existRecord) {
                    try {
                        boolean sendResult = bonusService.sendLuckyMoney((String) openid, mobile);
                        if (!sendResult) {
                            result.put("wechaterr", "通过微信发送红包失败");
                            code = 2;
                        }
                    } catch (Exception e) {
                        result.put("syserr", e.getMessage());
                        code = 2;
                    }
                } else {
                    code = 3;
                }
            }
        }
        result.put("code", code);
        return result;
    }
}
