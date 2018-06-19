package com.bonus.controller;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.bonus.constants.ShareState;
import com.bonus.util.JsonUtils;
import com.bonus.util.SessionUtils;
import com.bonus.util.WeiXinUtils;

/**
 * Title: OauthController
 * Description:微信授权入口
 *
 * @author Tyler
 */
@Controller
@RequestMapping("/weChat")
public class OauthController extends BaseController {

    private final static String OPEN_ID = "openid";

    @RequestMapping("oauth")
    public String oauth(@RequestParam("code") String code, @RequestParam("state") String state) throws Exception {
        try {
            //没有code,非法链接进来的
            if (StringUtils.isBlank(code)) {
                return "/error";
            }

            String url = WeiXinUtils.getUrl().replace("CODE", code);
            Map<String, Object> map = JsonUtils.getMapByUrl(url);
            if (map.get(OPEN_ID) != null) {
                if (ShareState.LUCKYMONEY.getDesc().equals(state)) {
                    SessionUtils.setAttribute(WeiXinUtils.OPENID_KEY, map.get("openid").toString());
                    return "redirect:/bonus/init";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "/error";
    }
}
