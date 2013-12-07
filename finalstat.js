var pagesCount = 54;
var playersCount = 2;
var myName = 'tyamgin';
var result = {};
for(var page = 1; page <= pagesCount; page++) {
	var data = $('<div></div>');
	$.get('http://russianaicup.ru/profile/' + myName + '/contest4/page/' + page, function(resp) {
		data.append(resp.replace(/<script.+script>/g, ''));
	});
	setTimeout(function(page, data) {
		var first = [];
		var second = [];
		$('td a[href*=profile][style*=text]', data).each(function(i, el) { 
			href = el.href;
			name = href.substr(href.lastIndexOf('/') + 1);
			if (i % 2 == 0) 
				first.push(name); 
			else 
				second.push(name); 
		});
		var myIdx = [];
		for(var i = 0; i < first.length; i++)
			myIdx.push(first[i] === myName ? 0 : 1);
		var maps = [];
		$('td img[title=Карта]', data).each(function(idx, el) {
			maps.push(el.src.substr(el.src.lastIndexOf('/') + 1));
		});
		$('td[style*=important]', data).each(function(idx, el) {
			var text = el.innerHTML.split('<hr style="margin-bottom: 8px; margin-top: 8px; width: 100%;">');
			for(var i = 0; i < text.length; i++) {
				if (i == myIdx[idx]) {
					if (!result[maps[idx]])
						result[maps[idx]] = 0;
					result[maps[idx]] += parseInt(text[i].substr(3));
				}
			}
		});
		console.log(page + ' done');
	}, 5000, page, data);
}

/*

logo-cheeser.map.png: 158
logo-default.map.png: 156
logo-fefer.map.png: 146
logo-map01.map.png: 158
logo-map02.map.png: 132
logo-map03.map.png: 124
logo-map04.map.png: 133
logo-map05.map.png: 128
logo-map06.map.png: 144

*/