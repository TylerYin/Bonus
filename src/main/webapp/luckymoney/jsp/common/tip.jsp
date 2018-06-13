<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>

<style>
.tip-wrap {
	position: fixed;
	display: none;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background-color: rgba(0, 0, 0, .08);
	text-align: center;
	z-index: 9999;
}
.tip-txt {
	display: inline-block;
	margin: 50% 0;
	padding: 15px 10px;
	color: #fff;
	background-color: rgba(0, 0, 0, .7);
	-moz-border-radius: 5px;
	-webkit-border-radius: 5px;
	-ms-webkit-border-radius: 5px;
	-o-border-radius: 5px;
	border-radius: 5px;
	box-shadow: 4px 4px 5px rgba(0, 0, 0, .35);
	-moz-border-radius: translate(0px, -15px);
	-webkit-border-radius: translate(0px, -15px);
	-ms-border-radius: translate(0px, -15px);
	-o-border-radius: translate(0px, -15px);
	transform: translate(0px, -15px);
}
</style>

<div class="tip-wrap" id="CAIMEI____tips">
	<div class="tip-txt" id="CAIMEI____tip_txt">
	已添加购物车
	</div>
</div>
<script>
/*
 * 弹窗方法
 * @param string _msg   提示内容 
 * @param number _time  提示时间 
 */
function caimei_alert(_msg, _time) {
	var $tips    = $('#CAIMEI____tips'),
		$tip_txt = $('#CAIMEI____tip_txt'),
		timer    = null;
	$tip_txt.text(_msg);
	$tips.show();
	timer = setTimeout(function() {
		$tips.hide('slow');
	}, _time || 3000);
	// 清空定时
	timer = null;
}
</script>