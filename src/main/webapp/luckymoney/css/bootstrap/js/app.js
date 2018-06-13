// app.js

/*---------- 名片交换 ----------*/


// var flags = {
// 	'supplier': 0,
// 	'agent': 0,
// 	'club': 0,
// 	'others': 0
// };
var flags = [0, 0, 0, 0];

(function() {
	var $navTabs = $('#navTabs').find('li'),
		$tabPanel = $('.tab-content .tab-pane'),
		req_url = '/router?appKey=00001&method=card.cardInfoList&v=1.0&format=json';  // + '&myCardID=1&userType=1';


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
						//	if (json[i].status == 1) continue;
							html += '<tr>'
								+		'<td>' + '<img class="img-thumbnail" width="100" height="100" src="' + json[i].headimgurl + '">' + '</td>'
								+		'<td>' + json[i].name + '</td>'
								+		'<td>' + json[i].company + '/' + json[i].position + '</td>'

								+		'<td>';

							json[i].status == 0 ? (html += '<a href="javascript: void(0);" class="btn btn-default btn-sm btn-xchanging">' + '已申请' + '</a>')
	  			 				: (html += '<a href="javascript: void(0);" data-personalID=' + json[i].cm_personalCardID + ' class="btn btn-warning btn-sm btn-xchanging">' + '<span class="glyphicon glyphicon-retweet"></span>' + '交换' + '</a>');

	  			 			html +=	'</td>' + '</tr>';

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

	$('tbody').on('click', '.btn-xchanging', function() {
		var $this = $(this);
		var changing_url = '/router?appKey=00001&method=card.exchangeCard&v=1.0&format=json'; //'&myCardID=1&personalCardID=3';

		$.ajax({
			type: 'post',
			url: changing_url,
			data: {
				myCardID: $('#myCardID').val(),
				personalCardID: $this.attr('data-personalID')
			},
			dataType: 'json',
			success: function() {
				console.log($this);

				$this.removeClass('btn-warning').addClass('btn-default').click(function() {
					return false;
				});
				$this.empty().html('已申请');
			},
			error: function() {

			}
		});

	});



})();


/*------ 加载更多 --------*/
var page0 = 1;
var page1 = 1;
var page2 = 1;
var page3 = 1;

function loadingmore(type, _this) {
	
	var req_url = '/router?appKey=00001&method=card.cardInfoList&v=1.0&format=json';
	var page;
	var $this = $(_this);
	$this.unbind('click');


	if (type==0) {
	    page = page0;
	} else if (type == 1) {
	    page = page1;
	} else if (type == 2) {
	    page = page2;
	} else if (type == 3) {
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
					if (json[i].status == 1) continue;
					html += '<tr>'
						+		'<td>' + '<img class="img-thumbnail" width="100" height="100" src="' + json[i].headimgurl + '">' + '</td>'
						+		'<td>' + json[i].name + '</td>'
						+		'<td>' + json[i].company + '/' + json[i].position + '</td>'

						+		'<td>';

					json[i].status == 0 ? (html += '<a href="javascript: void(0);" class="btn btn-default btn-sm btn-xchanging">' + '已申请' + '</a>')
			 				: (html += '<a href="javascript: void(0);" data-personalID=' + json[i].cm_personalCardID + ' class="btn btn-warning btn-sm btn-xchanging">' + '<span class="glyphicon glyphicon-retweet"></span>' + '交换' + '</a>');

			 			html +=	'</td>' + '</tr>';
				}

				$('#tbody' + type).append(html);
				setTimeout(function() {
					$this.bind('click');
				}, 30);
			} else {
				$this.removeClass('btn-warning').addClass('btn-default');
			}
		},
		error: function(err) {
		    $this.bind('click');	//do nothing
		}				
	});

}


