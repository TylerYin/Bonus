package com.bonus.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author Tyler
 */
public class WeiXinUtils {
    public static final String OPENID_KEY = "xxx";
    public static final String APP_ID = "xxx";
    public static final String APP_SECRET = "xxx";

    /**
     * 测试用，临时设置的变量值
     */
    public static final String QR_CODE = "xxx";
    public static final String OPEN_ID = "xxx";

    /**
     * 若需要获取用户相关信息，如昵称，头像等相关信息，需要修改snsapi_base为snsapi_userinfo，根据token和openid获取用户相关信息。
     * 若只是为了发红包，则只需要获取到用户的openid,使用snsapi_base参数就可以了。
     */
    public static final String OAUTH_BASE = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + APP_ID + "&redirect_uri=REDIRECT_URL&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect";
    public static final String REDIRECT_URI = "http://www.wnzc.ltd/bonus/wechat/oauth";

    public static String getUrl() {
        return "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + APP_ID + "&secret=" + APP_SECRET + "&code=CODE&grant_type=authorization_code";
    }

    public static String getOauthLinkURL(String state) {
        String oauthLink = OAUTH_BASE;
        String redirect = REDIRECT_URI;
        try {
            redirect = URLEncoder.encode(redirect, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        oauthLink = oauthLink.replace("REDIRECT_URL", redirect).replace("STATE", state);
        return oauthLink;
    }
}