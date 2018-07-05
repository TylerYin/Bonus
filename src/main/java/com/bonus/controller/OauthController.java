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
@RequestMapping("/wechat")
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

            /**
             * 若要获取用户的相关信息，可以在这个map中拿到access_token，使用access_token和open_id来获取用户的相关信息，但是需要在WeiXinUtils类中将nsapi_base修改为snsapi_userinfo，
             * 具体调用地址见微信开发文档，https://mp.weixin.qq.com/wiki?t=resource/res_main&id=mp1421140842
             */
            if (map.get(OPEN_ID) != null && ShareState.BONUS.getDesc().equals(state)) {
                SessionUtils.setAttribute("openid", map.get("openid").toString());
                return "redirect:/bonus/init";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "error";
    }
}