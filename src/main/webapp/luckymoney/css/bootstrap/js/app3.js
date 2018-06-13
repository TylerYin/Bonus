//app.js
//查看我的收到的交换

$(function() {
	var req_url = '/router?appKey=00001&method=card.undealCard&v=1.0&format=json';
	$.ajax({
		type: 'post',
		url: req_url,
		data: {
			myCardID: $('#myCardID').val()
		},
		dataType: 'json',
		success: function(data) {
			//console.log(data);
			var json = data.cardInfo,
				len = json.length,
				html = '';

			for (var i = 0; i < len; i++) {
				html += '<tr>'
					+		'<td>' + '<img class="img-thumbnail" width="100" height="100" src="' + json[i].headimgurl + '">' + '</td>'
					+		'<td>' + json[i].name + '</td>'
					+		'<td>' + json[i].company + '/' + json[i].position + '</td>'
					+		'<td data-personalID=' + json[i].cm_personalCardID + '>'

					+ 			'<a href="javascript: void(0);" class="btn btn-default btn-sm btn-deal-ignore">忽略</a>'
					+ 			'<br >'
					+			'<a href="javascript: void(0);" class="btn btn-warning btn-sm btn-deal-accept">'
					+				'<span class="glyphicon glyphicon-retweet"></span>接受'
					+			'</a>'
					+		'</td>'
					+	'</tr>';
			}

			$('#undealTbody').html(html);
		},
		error: function() {

		}
	});

});

(function() {
	var req_url = '/router?appKey=00001&method=card.handleCard&v=1.0&format=json';

	//忽略
	$('#undealTbody').on('click', '.btn-deal-ignore', function() {
	

		var $this = $(this);
		console.log($this.parent('td').attr('data-personalID'));
		$.ajax({
			type: 'post',
			url: req_url,
			data: {
				myCardID: $('#myCardID').val(),
				personalCardID: $this.parent('td').attr('data-personalID'),
				status:2
			},
			dataType: 'json',	
			success: function() {
				$this.parents('tr').remove();
			}	
		});
	});

	//接受
	$('#undealTbody').on('click', '.btn-deal-accept', function() {
		var $this = $(this);
		$.ajax({
			type: 'post',
			url: req_url,
			data: {
				myCardID: $('#myCardID').val(),
				personalCardID:$this.parent('td').attr('data-personalID'),
				status:3
			},
			dataType: 'json',	
			success: function() {
				$this.parents('tr').remove();
			}	
		});
	});	
})();