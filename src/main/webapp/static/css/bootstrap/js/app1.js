// app1.js
// 注册填写名片js
(function() {

	var $btnSave = $('#btnSave'),
		cardInfo = {};

	$btnSave.on('click', function() {

		var $this = $(this);

		cardInfo = {
			'myCardID': $('#myCardID').val(),
			'name': $('#name').val(),
			'company': $('#company').val(),
			'position': $('#position').val(),
			'type': getCompanyType(),
			'mobile': $('#mobile').val(),
			'qq': $('#qq').val(),
			'email': $('#email').val(),
			'wechat': $('#wechat').val(),
			'introduction': $('#introduction').val() + '',
			'appKey': '00001',
			'format': 'json',
			'v': '1.0',
			'method': 'card.register'
		};
		if (cardInfo['name'].replace(/(^\s*)|(\s*$)/g, '') === '') {
			$('.form-group').removeClass('has-warning').removeClass('has-error');
			$('#name').parents('.form-group').addClass('has-warning');
			return false;
		}
		if (cardInfo['company'].replace(/(^\s*)|(\s*$)/g, '') === '') {
			$('.form-group').removeClass('has-warning').removeClass('has-error');
			$('#company').parents('.form-group').addClass('has-warning');
			return false;
		}
		if (cardInfo['position'].replace(/(^\s*)|(\s*$)/g, '') === '') {
			$('.form-group').removeClass('has-warning').removeClass('has-error');
			$('#position').parents('.form-group').addClass('has-warning');
			return false;
		}
		if (cardInfo['mobile'].replace(/(^\s*)|(\s*$)/g, '') === '') {
			$('.form-group').removeClass('has-warning').removeClass('has-error');
			$('#mobile').parents('.form-group').addClass('has-warning');
			return false;
		} else if (cardInfo['mobile'].replace(/(^\s*)|(\s*$)/g, '') !== '') {
			if (!/^0?(13|15|18|14|17)[0-9]{9}$/.test(cardInfo['mobile'])) {
				$('.form-group').removeClass('has-warning').removeClass('has-error');
				$('#mobile').parents('.form-group').addClass('has-error');
				return false;
			}
		}
		$.ajax({
			type: 'post',
			url: '/router',
			data: cardInfo,
			dataType: 'json',
			success: function(json) {
				if (json.result == false) {
					$('#myModal').modal('show');
				} else {
					window.location.href = '/card/cardInfo.html?myCardID='+cardInfo['myCardID']+'&flag=1&personalCardID='+cardInfo['myCardID'];
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