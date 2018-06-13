
$(function() {
    if (!is_weixin()) {
        window.location.href = 'https://open.weixin.qq.com/connect/oauth2/authorize?appid='+appID+'&redirect_uri='+server+'/card/oauth.html&response_type=code&scope=snsapi_userinfo&state=1#wechat_redirect';
    }
});

function is_weixin(){
    var ua = navigator.userAgent.toLowerCase();
    if(ua.match(/MicroMessenger/i)=="micromessenger") {
        return true;
    } else {
        return false;
    }
}
