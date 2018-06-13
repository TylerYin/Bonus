/**
 *
 */
package com.wytone.controller;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.wytone.constants.ShareState;
import com.wytone.util.JsonUtils;
import com.wytone.util.SessionUtils;
import com.wytone.util.WeiXinUtils;

/**
 * <p>Title: OauthController</p>
 * <p>Description:微信授权入口 </p>
 *
 * @author userwyh
 * @date 2015年11月24日 下午1:22:06
 */
@Controller
@RequestMapping("/wechat")
public class OauthController extends BaseController {

    private final static String OPEN_ID = "openid";

    @RequestMapping("oauth")
    public String oauth(@RequestParam("code") String code, @RequestParam("state") String state) throws Exception {
        try {
            //没有code,非法链接进来的
            if (StringUtils.isBlank(code)) {
                return "error";
            }

            String url = WeiXinUtils.getUrl().replace("CODE", code);
            Map<String, Object> map = JsonUtils.getMapByUrl(url);
            if (map.get(OPEN_ID) != null) {
                if (ShareState.LUCKYMONEY.getDesc().equals(state)) {
                    SessionUtils.setAttribute(WeiXinUtils.OPENID_KEY, map.get("openid").toString());
                    return "redirect:/luckymoney/signup";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }
}
