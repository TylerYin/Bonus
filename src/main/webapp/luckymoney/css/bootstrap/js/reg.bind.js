(function() {

	var $btnSave = $('#btnSave');     
	var $btnbind = $('#btnBind'),
		userInfo = {};

	$btnSave.on('click', function() {
		var $this = $(this);
		userInfo = {
			'userName': $('#name').val(),
			'password': $('#pwd').val(),
			'openid':$('#openid').val(),
			'pwd1': $('#pwd1').val(),
			'userType': getCompanyType(),
			'appKey': '00001',
			'format': 'json',
			'v': '1.0',
			'method': 'weike.register'
		};
		
		if (userInfo['userName'].replace(/(^\s*)|(\s*$)/g, '') === '') {
			$('.form-group').removeClass('has-warning').removeClass('has-error');
			$('#name').parents('.form-group').addClass('has-warning');
			return false;
		}
		if (userInfo['password'].replace(/(^\s*)|(\s*$)/g, '') === '') {
			$('.form-group').removeClass('has-warning').removeClass('has-error');
			$('#pwd').parents('.form-group').addClass('has-warning');
			return false;
		}
		if (userInfo['pwd1'].replace(/(^\s*)|(\s*$)/g, '') === '') {
			$('.form-group').removeClass('has-warning').removeClass('has-error');
			$('#pwd1').parents('.form-group').addClass('has-warning');
			return false;
		}
		if(userInfo['pwd1'].replace(/(^\s*)|(\s*$)/g, '') != userInfo['password'].replace(/(^\s*)|(\s*$)/g, '')){
			$('.form-group').removeClass('has-warning').removeClass('has-error');
			$('#pwd1').parents('.form-group').addClass('has-warning');
			return false;
		}
		if(!$("#agreement").is(':checked')){
			 $('#errmsg').html('请先同意用户协议与隐私权政策');
			 return false;
		}
		$.ajax({
			type: 'post',
			url: '/router',
			data: userInfo,
			dataType: 'json',
			success: function(json) {
				if(typeof json.subErrors !== 'undefined'){
					 $('#errmsg').html(json.subErrors[0].message);
					 return;
			    }
				if (json.result == true) {
					$('#account').val(userInfo['userName']);
					$('#password').val(userInfo['password']);
					doAction('login');
				} else {
					$('#myModal').modal('show');
				}
			},
			error: function(err) {
				//do nothing
			}
		});
	});
	

	
	$btnbind.on('click', function() {
		var $this = $(this);
		userInfo = {
			'userName': $('#name').val(),
			'password': $('#pwd').val(),
			'openid':$('#openid').val(),
			'appKey': '00001',
			'format': 'json',
			'v': '1.0',
			'method': 'weike.bind'
		};
		if (userInfo['userName'].replace(/(^\s*)|(\s*$)/g, '') === '') {
			$('.form-group').removeClass('has-warning').removeClass('has-error');
			$('#name').parents('.form-group').addClass('has-warning');
			return false;
		}
		if (userInfo['password'].replace(/(^\s*)|(\s*$)/g, '') === '') {
			$('.form-group').removeClass('has-warning').removeClass('has-error');
			$('#pwd').parents('.form-group').addClass('has-warning');
			return false;
		}
		
		$.ajax({
			type: 'post',
			url: '/router',
			data: userInfo,
			dataType: 'json',
			success: function(json) {
			   if(typeof json.subErrors !== 'undefined'){
					 $('#errmsg').html(json.subErrors[0].message);
					 return;
			   }
				if (json.result == true) {
					$('#account').val(userInfo['userName']);
					$('#password').val(userInfo['password']);
					doAction('login');
				} else {
					$('#myModal').modal('show');
				}
			},
			error: function(err) {
				//do nothing
			}
		});
	});

})();

//获取单选框的值
function getCompanyType() {
	return $('input[name=type]:checked').val();
}