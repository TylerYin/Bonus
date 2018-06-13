<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>领红包</title>
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1">
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="apple-mobile-web-app-status-bar-style" content="black">

<meta http-equiv="pragram" content="no-cache">
<meta http-equiv="cache-control" content="no-cache, must-revalidate">
<meta http-equiv="expires" content="0">

<script>
document.write('<script src="/luckymoney/lib/' + ('__proto__' in {} ? 'zepto.min' : 'jquery.min') + '.js"><\/script>');
</script>
</head>

<body>
	<div >
		<c:if test="${existRecord == true }">
			你已经领取过红包
		</c:if>
		<c:if test="${existRecord == false}">
			<div id="form">
				<div>
                    手机号码：<input type="text"  name="mobile" id="mobile"placeholder="填写入驻采美的手机号码" value="">
				    <a href="javascript: void(0);" id="signup" >拿红包</a>
				</div>

				<%@ include file="/luckymoney/jsp/common/tip.jsp"%>
				<script type="text/javascript">
	              var done = 0;
	              $("#signup").click(function(){
	             	 if(done == 1){
	         		 	return;
	        		 }
	         		 done = 1;
	       			
			         var mobile = $('#mobile').val();
			         if(!mobile) {
				         caimei_alert("请填写你的手机号" ,2000);
					     $('#mobile').focus();
					     done = 0;
					     return;
				     }

				     if(!/1[1-9][0-9]{9}$/.test(mobile)) {
					     caimei_alert("手机号格式不正确" ,2000);
					     $('#mobile').focus();
					     done = 0;
					     return;
				     }

				     var params = {};
				     params['mobile'] = mobile;
				     $.ajax({
				                 url: "/luckymoney/init.shtml",
				                 type: "post",
				                 data: params,
				                 dataType: "json",
				                 success: function(data){
					                 if(data){
					                 	code = data.code;
					                 	var html = '';
					                	if(code == 0){
					                         html = '领取成功';
					                    }else if(code == 1){
					                         done = 0;
							                 caimei_alert(data.mobileerr+"" ,2000);
							                 $('#mobile').focus();
						                }else if(code == 2){
							                 html = '系统异常';
						                 }else if(code == 3){
						                        html = '该手机号已经领取过了';
						                 }
						                 if(code != 1){
						                    $('#form').html(html);
						                 }
					               }
				                 },
				                error: function(XMLHttpRequest, textStatus, errorThrown) {
				                 done = 0;
				                }
				             }); 
				         });
		         </script>
			</div>
		</c:if>
	</div>
</body>
</html>