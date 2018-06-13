// app2.js
// 名片夹
// var flags = {
// 	'supplier': 0,
// 	'agent': 0,
// 	'club': 0,
// 	'others': 0
// };
var flags = [0, 0, 0, 0];

$(function() {

    req_url = '/router?appKey=00001&method=card.total&v=1.0&format=json';
	$.ajax({
		type: 'post',
		url: req_url,
		data: {
			myCardID: $('#myCardID').val()
		},
		dataType: 'json',
		success: function(data) {
			$('#unchangedNum').text(data.total);
		},
		error: function() {

		}
	});

});


(function() {
	var $navTabs = $('#navTabs').find('li'),
		$tabPanel = $('.tab-content .tab-pane'),
		req_url = '/router?appKey=00001&method=card.exCardInfoList&v=1.0&format=json';  // + '&myCardID=1&userType=1';


	$navTabs.on('click', function() {
		var $this = $(this),
			index = $navTabs.index($this);

		if (flags[index] != 1) {
			$.ajax({
				type: 'post',
				url: req_url,
				data: {
					myCardID: $('#myCardID').val(),
					userType: index
				},
				dataType: 'json',
				success: function(data) {

					var html = '',
						json  = data.cardInfo,
						len = json.length;

					if (len > 0) {
						for (var i = 0; i < len; i++) {
		
							html += '<tr data-personalID=' + json[i].cm_personalCardID + '>'
								+		'<td>' + '<img class="img-thumbnail" width="100" height="100" src="' + json[i].headimgurl + '">' + '</td>'
								+		'<td>' + json[i].name + '</td>'
								+		'<td>' + json[i].company + '/' + json[i].position + '</td>'
								+ '</tr>';
						}


						$('#tbody' + index).html(html);
						flags[index] = 1;
					}

					if (len <= 9) {
						$('#loadingMore' + index).hide();
					}
				},
				error: function(err) {
					//do nothing
				}				
			});
		}

	}).eq(0).trigger('click');

	$('tbody').on('click', 'tr', function() {
		var $this = $(this),
			myCardID = $('#myCardID').val(),
			personalID = $this.attr('data-personalID');
 
		location.href="/card/cardInfo.html?flag=1&myCardID="+myCardID+"&personalCardID="+personalID;
	});

})();

var page0 = 1;
var page1 = 1;
var page2 = 1;
var page3 = 1;

function loadingmore(type, _this) {
	
	var req_url = '/router?appKey=00001&method=card.exCardInfoList&v=1.0&format=json';
	var page;
	var $this = $(_this); 
	$this.unbind('click');

	if (type==0) {
	    page = page0;
	} else if (type==1){
	    page = page1;
	} else if (type==2){
	    page = page2;
	} else if (type==3){
	    page = page3;
	}
    $.ajax({
		type: 'post',
		url: req_url,
		data: {
			myCardID: $('#myCardID').val(),
			userType: type,
			page: page
		},
		dataType: 'json',
		success: function(data) {

            if(type==0){
		       page0++;
		   }else if(type==1){
		       page1++;
		   }else if(type==2){
		       page2++;
		   }else if(type==3){
		       page3++;
		   }
			var html = '',
				json  = data.cardInfo,
				len = json.length;

			if (len > 0) {
				for (var i = 0; i < len; i++) {
		
					html += '<tr data-personalID=' + json[i].cm_personalCardID + '>'
						+		'<td>' + '<img class="img-thumbnail" width="100" height="100" src="' + json[i].headimgurl + '">' + '</td>'
						+		'<td>' + json[i].name + '</td>'
						+		'<td>' + json[i].company + '/' + json[i].position + '</td>'
						+ '</tr>';

				}

				console.log('qingqiu');
				$('#tbody' + type).append(html);
			} else {
				$this.removeClass('btn-warning').addClass('btn-default');
			}
			
			setTimeout(function() {
				$this.bind('click');
			}, 30);
		},
		error: function(err) {
			//do nothing
			$this.bind('click');
		}				
	});

}

