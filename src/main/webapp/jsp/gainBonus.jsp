<%@ page pageEncoding="utf-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>领红包</title>
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
    <meta name="apple-mobile-web-app-capable" content="yes">
    <meta name="apple-mobile-web-app-status-bar-style" content="black">
    <meta http-equiv="pragram" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache, must-revalidate">
    <meta http-equiv="expires" content="0">
</head>

<body>
<div>
    open_id : ${openid}<br>
    qrcode:${qrcode}
    <c:choose>
        <c:when test="${existRecord}">
            你已经领取过红包
        </c:when>
        <c:otherwise>
            <div id="form">
                <span>普通红包</span>
                <form name="gainBonus" method="post" action="${pageContext.request.contextPath}/bonus/sendBonus">
                    防伪码：<input type="text" name="qrcode" placeholder="防伪码" value="${qrcode}">
                    <input type="submit" value="领取红包">
                </form>

                <span>企业支付到微信零钱</span>
                <form name="gainBonus" method="post" action="${pageContext.request.contextPath}/bonus/transfer">
                    防伪码：<input type="text" name="qrcode" placeholder="防伪码" value="${qrcode}">
                    <input type="submit" value="领取红包">
                </form>
            </div>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>